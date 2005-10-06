//
// $Id: BangClient.java 3283 2004-12-22 19:23:00Z ray $

package com.threerings.bang.client;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.jmex.bui.BLookAndFeel;
import com.jmex.bui.BWindow;
import com.jmex.bui.BRootNode;
import com.jme.input.InputHandler;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;

import com.samskivert.util.Config;
import com.samskivert.util.RunQueue;
import com.samskivert.util.StringUtil;

import com.threerings.jme.JmeApp;
import com.threerings.jme.tile.FringeConfiguration;
import com.threerings.resource.ResourceManager;
import com.threerings.util.CompiledConfig;
import com.threerings.util.MessageManager;
import com.threerings.util.Name;

import com.threerings.presents.client.Client;
import com.threerings.presents.client.InvocationService.ConfirmListener;
import com.threerings.presents.client.SessionObserver;
import com.threerings.presents.dobj.DObjectManager;

import com.threerings.crowd.chat.client.ChatDirector;
import com.threerings.crowd.client.PlaceView;

import com.threerings.parlor.game.data.GameAI;

import com.threerings.bang.data.PlayerObject;
import com.threerings.bang.game.client.BangView;
import com.threerings.bang.game.client.effect.ParticleFactory;
import com.threerings.bang.game.data.BangConfig;
import com.threerings.bang.util.BangContext;
import com.threerings.bang.util.RenderUtil;

import static com.threerings.bang.Log.log;

/**
 * Takes care of instantiating all of the proper managers and loading up
 * all of the necessary configuration and getting the client bootstrapped.
 */
public class BangClient extends BasicClient
    implements SessionObserver
{
    /**
     * Initializes a new client and provides it with a frame in which to
     * display everything.
     */
    public void init (BangApp app)
    {
        _ctx = new BangContextImpl();
        initClient(_ctx, app, app);

        // listen for logon
        _client.addClientObserver(this);

        // create and display the logon view
        displayLogon();

        // and start unpacking our resources
        initResources(_lview);
    }

    /**
     * Returns a reference to the context in effect for this client. This
     * reference is valid for the lifetime of the application.
     */
    public BangContext getContext ()
    {
        return _ctx;
    }

    // documentation inherited from interface SessionObserver
    public void clientDidLogon (Client client)
    {
        // remove the logon display
        clearLogon();

        if (System.getProperty("test") != null) {
            // create a one player game of bang
            BangConfig config = new BangConfig();
//             config.players = new Name[] {
//                 _client.getCredentials().getUsername(), new Name("Larry"),
//                 new Name("Moe"), new Name("Curly")  };
//             config.ais = new GameAI[] {
//                 null, new GameAI(0, 50), new GameAI(0, 50), new GameAI(0, 50) };
            config.players = new Name[] {
                _client.getCredentials().getUsername(), new Name("Larry") };
            config.ais = new GameAI[] { null, new GameAI(0, 50) };
            config.scenarios = new String[] { "ch" };
            ConfirmListener cl = new ConfirmListener() {
                public void requestProcessed () {
                }
                public void requestFailed (String reason) {
                    log.warning("Failed to create game: " + reason);
                }
            };
            _ctx.getParlorDirector().startSolitaire(config, cl);

        } else {
            // display the town view
            _tview = new TownView(_ctx);
            _ctx.getRootNode().addWindow(_tview);
        }
    }

    // documentation inherited from interface SessionObserver
    public void clientObjectDidChange (Client client)
    {
        // nada
    }

    // documentation inherited from interface SessionObserver
    public void clientDidLogoff (Client client)
    {
        System.exit(0);
    }

    @Override // documentation inherited
    protected void createContextServices (RunQueue rqueue)
    {
        super.createContextServices(rqueue);

        // create our custom directors
        _chatdir = new BangChatDirector(_ctx);

        // initialize our user interface helper
        BangUI.init(_ctx);

        // warm up the particle factory
        ParticleFactory.warmup(_ctx);
    }

    protected void displayLogon ()
    {
        _lview = new LogonView(_ctx);
        _ctx.getRootNode().addWindow(_lview);

        int width = _ctx.getDisplay().getWidth();
        int height = _ctx.getDisplay().getHeight();
        _lview.pack();
        _lview.setLocation((width - _lview.getWidth())/2,
                           (height - _lview.getHeight())/2);
    }

    protected void clearLogon ()
    {
        _ctx.getRootNode().removeWindow(_lview);
        _lview = null;
    }

    /** The context implementation. This provides access to all of the
     * objects and services that are needed by the operating client. */
    protected class BangContextImpl extends BasicContextImpl
        implements BangContext
    {
        /** Apparently the default constructor has default access, rather
         * than protected access, even though this class is declared to be
         * protected. Why, I don't know, but we need to be able to extend
         * this class elsewhere, so we need this. */
        protected BangContextImpl () {
        }

        public Config getConfig () {
            return _config;
        }

        public ChatDirector getChatDirector () {
            return _chatdir;
        }

        public void setPlaceView (PlaceView view) {
            if (_pview != null) {
                _ctx.getRootNode().removeWindow(_pview);
            } else if (_tview != null) {
                _ctx.getRootNode().removeWindow(_tview);
            }

            // wire a status view to this place view (show by pressing esc);
            // the window must be modal prior to adding it to the hierarchy to
            // ensure that it is a default event target (and hears the escape
            // key pressed event)
            _pview = (BWindow)view;
            if (!(_pview instanceof BangView)) {
                _pview.setModal(true);
                new StatusView(_ctx).bind(_pview);
            }

            // now we can add the window to the hierarchy
            _ctx.getRootNode().addWindow(_pview);

            // size the view to fill the display
            _pview.setBounds(0, 0, _ctx.getDisplay().getWidth(),
                             _ctx.getDisplay().getHeight());
        }

        public void clearPlaceView (PlaceView view) {
            if (_pview != view) {
                log.warning("Requested to clear non-current place view " +
                            "[have=" + _pview + ", got=" + view + "].");
                // try to cope
                if (_pview != null) {
                    _ctx.getRootNode().removeWindow(_pview);
                }
            }
            _ctx.getRootNode().removeWindow((BWindow)view);
            _pview = null;
            _ctx.getRootNode().addWindow(_tview);
        }

        public PlayerObject getUserObject () {
            return (PlayerObject)getClient().getClientObject();
        }
    }

    protected BangContextImpl _ctx;
    protected Config _config = new Config("bang");

    protected BangChatDirector _chatdir;

    protected BWindow _pview;
    protected LogonView _lview;
    protected TownView _tview;

    /** The prefix prepended to localization bundle names before looking
     * them up in the classpath. */
    protected static final String MESSAGE_MANAGER_PREFIX = "rsrc.i18n";
}
