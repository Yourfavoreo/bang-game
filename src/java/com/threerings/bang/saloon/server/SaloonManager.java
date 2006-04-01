//
// $Id$

package com.threerings.bang.saloon.server;

import java.util.HashMap;
import java.util.logging.Level;

import com.samskivert.util.Interval;

import com.threerings.presents.data.ClientObject;
import com.threerings.presents.dobj.DObject;
import com.threerings.presents.dobj.ObjectAccessException;
import com.threerings.presents.dobj.Subscriber;
import com.threerings.presents.server.InvocationException;

import com.threerings.crowd.chat.data.SpeakMarshaller;
import com.threerings.crowd.chat.server.SpeakDispatcher;
import com.threerings.crowd.chat.server.SpeakProvider;
import com.threerings.crowd.data.OccupantInfo;
import com.threerings.crowd.server.PlaceManager;

import com.threerings.bang.data.PlayerObject;
import com.threerings.bang.game.data.BangConfig;
import com.threerings.bang.server.BangServer;

import com.threerings.bang.admin.server.RuntimeConfig;

import com.threerings.bang.saloon.client.SaloonService;
import com.threerings.bang.saloon.data.Criterion;
import com.threerings.bang.saloon.data.MatchObject;
import com.threerings.bang.saloon.data.SaloonCodes;
import com.threerings.bang.saloon.data.SaloonMarshaller;
import com.threerings.bang.saloon.data.SaloonObject;

import static com.threerings.bang.Log.log;

/**
 * Implements the server side of the Saloon.
 */
public class SaloonManager extends PlaceManager
    implements SaloonCodes, SaloonProvider
{
    // documentation inherited from interface SaloonProvider
    public void findMatch (ClientObject caller, Criterion criterion,
                          final SaloonService.ResultListener listener)
        throws InvocationException
    {
        final PlayerObject user = (PlayerObject)caller;

        // if we're not allowing new games, fail immediately
        if (!RuntimeConfig.server.allowNewGames) {
            throw new InvocationException("m.new_games_disabled");
        }

        // look for an existing match that is compatible
        for (Match match : _matches.values()) {
            // don't allow players to join matches that are about to start
            if (match.matchobj.starting) {
                continue;
            }

            if (match.join(user, criterion)) {
                listener.requestProcessed(match.matchobj.getOid());

                // check to see if this match is ready to go
                switch (match.checkReady()) {
                case COULD_START:
                    couldStartMatch(match);
                    break;
                case START_NOW:
                    startMatch(match);
                    break;
                }
                return;
            }
        }

        // otherwise we need to create a new match
        final Match match = new Match(user, criterion);
        BangServer.omgr.createObject(MatchObject.class, new Subscriber() {
            public void objectAvailable (DObject object) {
                match.setObject((MatchObject)object);
                match.matchobj.setSpeakService(
                    (SpeakMarshaller)BangServer.invmgr.registerDispatcher(
                        new SpeakDispatcher(new SpeakProvider(object, null)),
                        false));
                _matches.put(object.getOid(), match);
                BangServer.statobj.setPendingMatches(_matches.size());
                listener.requestProcessed(object.getOid());
            }
            public void requestFailed (int oid, ObjectAccessException cause) {
                log.warning("Failed to create match object " + cause + ".");
                listener.requestFailed(INTERNAL_ERROR);
            }
        });
    }

    // documentation inherited from interface SaloonProvider
    public void leaveMatch (ClientObject caller, int matchOid)
    {
        Match match = _matches.get(matchOid);
        if (match != null) {
            clearPlayerFromMatch(match, caller.getOid());

            // if the match is queued to be started and is no longer ready,
            // cancel its starter interval
            if (match.checkReady() == Match.Readiness.NOT_READY &&
                match.starter != null) {
                match.starter.cancel();
                match.starter = null;
                match.matchobj.setStarting(false);
            }
        }
    }

    @Override // documentation inherited
    protected Class getPlaceObjectClass ()
    {
        return SaloonObject.class;
    }

    @Override // documentation inherited
    protected long idleUnloadPeriod ()
    {
        // we don't want to unload
        return 0L;
    }

    @Override // documentation inherited
    protected void didStartup ()
    {
        super.didStartup();

        // register our invocation service
        _salobj = (SaloonObject)_plobj;
        _salobj.setService((SaloonMarshaller)
                           BangServer.invmgr.registerDispatcher(
                               new SaloonDispatcher(this), false));
    }

    @Override // documentation inherited
    protected void bodyLeft (int bodyOid)
    {
        super.bodyLeft(bodyOid);

        // clear this player out of any match they might have been in
        for (Match match : _matches.values()) {
            if (clearPlayerFromMatch(match, bodyOid)) {
                break;
            }
        }
    }

    @Override // documentation inherited
    protected void bodyUpdated (OccupantInfo info)
    {
        super.bodyUpdated(info);

        // if a player disconnects during the matchmaking phase, remove them
        // from their pending match
        if (info.status == OccupantInfo.DISCONNECTED) {
            for (Match match : _matches.values()) {
                if (clearPlayerFromMatch(match, info.bodyOid)) {
                    break;
                }
            }
        }
    }

    /**
     * Called when a match could potentially be started.
     */
    protected void couldStartMatch (final Match match)
    {
        if (match.starter != null) {
            log.warning("Requested to couldStart match that's already queued " +
                        "for something " + match + ".");
            return;
        }
        match.starter = new Interval(BangServer.omgr) {
            public void expired () {
                match.starter = null;
                if (match.checkReady() != Match.Readiness.NOT_READY) {
                    startMatch(match);
                }
            }
        };
        match.starter.schedule(GO_WITH_IT_DELAY);
    }

    /**
     * Called when a match should be started. Marks the match as such and waits
     * a few second before actually starting the match to give participants a
     * chance to see who the last joiner was and potentially balk.
     */
    protected void startMatch (final Match match)
    {
        if (match.starter != null) {
            log.warning("Requested to start match that's already queued " +
                        "for starting " + match + ".");
            return;
        }
        match.matchobj.setStarting(true);
        match.starter = new Interval(BangServer.omgr) {
            public void expired () {
                // make sure the match is still ready (this shouldn't happen as
                // we cancel matches that become non-ready, but there are edge
                // cases where we might not get canceled in time)
                if (match.checkReady() == Match.Readiness.NOT_READY) {
                    match.starter = null;
                    match.matchobj.setStarting(false);
                    return;
                }
                // go like the wind!
                BangConfig config = match.createConfig();
                try {
                    BangServer.plreg.createPlace(config, null);
                } catch (Exception e) {
                    log.log(Level.WARNING, "Choked creating game " +
                            "[config=" + config + "].", e);
                }
                clearMatch(match);
            }
        };
        match.starter.schedule(START_DELAY);
    }

    protected boolean clearPlayerFromMatch (Match match, int playerOid)
    {
        if (match.remove(playerOid)) {
            if (match.getPlayerCount() == 0) {
                clearMatch(match);
            }
            return true;
        }
        return false;
    }

    protected void clearMatch (Match match)
    {
        int moid = match.matchobj.getOid();
        if (match.matchobj.speakService != null) {
            BangServer.invmgr.clearDispatcher(match.matchobj.speakService);
        }
        BangServer.omgr.destroyObject(moid);
        _matches.remove(moid);
        BangServer.statobj.setPendingMatches(_matches.size());
    }

    protected SaloonObject _salobj;
    protected HashMap<Integer,Match> _matches = new HashMap<Integer,Match>();

    protected static final long START_DELAY = 5000L;
    protected static final long GO_WITH_IT_DELAY = 10000L;
}
