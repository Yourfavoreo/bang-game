//
// $Id$

package com.samskivert.bang.client;

import java.awt.event.ActionEvent;

import com.samskivert.swing.event.CommandEvent;

import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.BodyObject;
import com.threerings.crowd.data.PlaceConfig;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.crowd.util.CrowdContext;

import com.threerings.parlor.game.client.GameController;

import com.threerings.toybox.util.ToyBoxContext;

import com.samskivert.bang.data.BangObject;
import com.samskivert.bang.data.PiecePath;

import static com.samskivert.bang.Log.log;

/**
 * Handles the logic and flow of the client side of a game.
 */
public class BangController extends GameController
{
    /** The name of the command posted by the "Back to lobby" button in
     * the side bar. */
    public static final String BACK_TO_LOBBY = "BackToLobby";

    /** A command that requests to set a path on a piece. */
    public static final String SET_PATH = "SetPath";

    // documentation inherited
    public void init (CrowdContext ctx, PlaceConfig config)
    {
        _ctx = (ToyBoxContext)ctx;
        super.init(ctx, config);
    }

    // documentation inherited
    public void willEnterPlace (PlaceObject plobj)
    {
        super.willEnterPlace(plobj);
        _bangobj = (BangObject)plobj;

        // determine our player index
        BodyObject me = (BodyObject)_ctx.getClient().getClientObject();
        _pidx = _bangobj.getPlayerIndex(me.username);

        // we may be returning to an already started game
        if (_bangobj.isInPlay()) {
            _panel.view.startGame(_bangobj, _pidx);
        }
    }

    /** Handles a request to leave the game. Generated by the {@link
     * #BACK_TO_LOBBY} command. */
    public void handleBackToLobby (Object source)
    {
        _ctx.getLocationDirector().moveBack();
    }

    /** Handles a request to set a path on a piece. Generated by the
     * {@link #SET_PATH} command. */
    public void handleSetPath (Object source, PiecePath path)
    {
        log.info("Requesting " + path);
        _bangobj.service.setPath(_ctx.getClient(), path);
    }

    // documentation inherited
    protected PlaceView createPlaceView ()
    {
        _panel = new BangPanel(_ctx, this);
        return _panel;
    }

    // documentation inherited
    protected void gameDidStart ()
    {
        super.gameDidStart();

        // we may be returning to an already started game
        _panel.view.startGame(_bangobj, _pidx);
    }

    // documentation inherited
    protected void gameWillReset ()
    {
        super.gameWillReset();
        _panel.view.endGame();
    }

    // documentation inherited
    protected void gameDidEnd ()
    {
        super.gameDidEnd();
        _panel.view.endGame();
    }

    /** A casted reference to our context. */
    protected ToyBoxContext _ctx;

    /** Contains our main user interface. */
    protected BangPanel _panel;

    /** A casted reference to our game object. */
    protected BangObject _bangobj;

    /** Our player index or -1 if we're not a player. */
    protected int _pidx;
}
