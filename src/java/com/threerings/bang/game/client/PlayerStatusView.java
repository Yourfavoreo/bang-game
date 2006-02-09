//
// $Id$

package com.threerings.bang.game.client;

import java.awt.image.BufferedImage;

import com.jme.image.Image;
import com.jme.renderer.Renderer;
import com.jme.util.TextureManager;

import com.jmex.bui.BButton;
import com.jmex.bui.BContainer;
import com.jmex.bui.BLabel;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.icon.BIcon;
import com.jmex.bui.icon.ImageIcon;
import com.jmex.bui.icon.SubimageIcon;
import com.jmex.bui.layout.AbsoluteLayout;
import com.jmex.bui.layout.GroupLayout;
import com.jmex.bui.layout.TableLayout;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Point;
import com.jmex.bui.util.Rectangle;

import com.threerings.presents.dobj.AttributeChangeListener;
import com.threerings.presents.dobj.AttributeChangedEvent;
import com.threerings.presents.dobj.ElementUpdateListener;
import com.threerings.presents.dobj.ElementUpdatedEvent;
import com.threerings.presents.dobj.EntryAddedEvent;
import com.threerings.presents.dobj.EntryRemovedEvent;
import com.threerings.presents.dobj.EntryUpdatedEvent;
import com.threerings.presents.dobj.SetListener;

import com.threerings.bang.avatar.client.AvatarView;
import com.threerings.bang.avatar.data.Look;
import com.threerings.bang.avatar.util.AvatarLogic;

import com.threerings.bang.data.BangOccupantInfo;
import com.threerings.bang.util.BangContext;

import com.threerings.bang.game.data.BangObject;
import com.threerings.bang.game.data.GameCodes;
import com.threerings.bang.game.data.card.Card;

import static com.threerings.bang.Log.log;
import static com.threerings.bang.client.BangMetrics.*;

/**
 * Displays the name, score, cash and cards held by a particular player.
 */
public class PlayerStatusView extends BContainer
    implements AttributeChangeListener, ElementUpdateListener, SetListener,
               ActionListener
{
    public PlayerStatusView (BangContext ctx, BangObject bangobj,
                             BangController ctrl, int pidx)
    {
        super(new AbsoluteLayout());
        setStyleClass("player_status_cont");

        _ctx = ctx;
        _bangobj = bangobj;
        _bangobj.addListener(this);
        _ctrl = ctrl;
        _pidx = pidx;

        // load up the background and rank images for our player
        _color = new ImageIcon(
            _ctx.loadImage("ui/pstatus/background" + pidx + ".png"));
        _rankimg = _ctx.loadImage("ui/pstatus/rank" + pidx + ".png");

        // create our interface elements
        _player = new BLabel(_bangobj.players[_pidx].toString(),
                             "player_status" + _pidx);
        add(_player, NAME_RECT);

        _cash = new BLabel("");
        add(_cash, CASH_LOC);
        add(_ranklbl = new BLabel(createRankIcon(-1)), RANK_RECT);

        updateAvatar();
        updateStatus();
    }

    /**
     * Sets the rank displayed for this player to the specified index (0 is 1st
     * place, 1 is 2nd, etc.).
     */
    public void setRank (int rank)
    {
        if (_rank != rank) {
            _rank = rank;
            _ranklbl.setIcon(createRankIcon(rank));
        }
    }

    // documentation inherited from interface AttributeChangeListener
    public void attributeChanged (AttributeChangedEvent event)
    {
        if (event.getName().equals(BangObject.STATE)) {
            updateStatus();
        }
    }

    // documentation inherited from interface ElementUpdateListener
    public void elementUpdated (ElementUpdatedEvent event)
    {
        updateStatus();
    }

    // documentation inherited from interface SetListener
    public void entryAdded (EntryAddedEvent event)
    {
        String name = event.getName();
        if (name.equals(BangObject.CARDS)) {
            Card card = (Card)event.getEntry();
            if (card.owner != _pidx) {
                return;
            }
            int cidx = -1;
            for (int ii = 0; ii < _cards.length; ii++) {
                if (_cards[ii] == null) {
                    cidx = ii;
                    break;
                }
            }
            if (cidx == -1) {
                return;
            }
            _cards[cidx] = createButton(card);
            Rectangle rect = new Rectangle(CARD_RECT);
            rect.x += (rect.width * cidx);
            add(_cards[cidx], rect);

        } else if (name.equals(BangObject.OCCUPANT_INFO) && _avatar == null) {
            updateAvatar();
        }
    }

    // documentation inherited from interface SetListener
    public void entryUpdated (EntryUpdatedEvent event)
    {
        if (event.getName().equals(BangObject.OCCUPANT_INFO) &&
            _avatar == null) {
            updateAvatar();
        }
    }

    // documentation inherited from interface SetListener
    public void entryRemoved (EntryRemovedEvent event)
    {
        if (!event.getName().equals(BangObject.CARDS)) {
            return;
        }
        Card card = (Card)event.getOldEntry();
        if (card.owner != _pidx) {
            return;
        }
        String cid = "" + card.cardId;
        for (int ii = 0; ii < _cards.length; ii++) {
            if (cid.equals(_cards[ii].getAction())) {
                remove(_cards[ii]);
                _cards[ii] = null;
                return;
            }
        }
    }

    // documentation inherited from interface ActionListener
    public void actionPerformed (ActionEvent event)
    {
        try {
            _ctrl.placeCard(Integer.parseInt(event.getAction()));
        } catch (Exception e) {
            log.warning("Bogus card '" + event.getAction() + "': " + e);
        }
    }

    @Override // documentation inherited
    protected void renderBackground (Renderer renderer)
    {
        // first draw our color
        _color.render(renderer, BACKGROUND_LOC.x, BACKGROUND_LOC.y);

        // then draw our avatar
        if (_avatar != null) {
            _avatar.render(renderer, AVATAR_LOC.x, AVATAR_LOC.y);
        }

        // then draw the normal background
        super.renderBackground(renderer);
    }

    protected void updateAvatar ()
    {
        // load up this player's avatar image
        BangOccupantInfo boi = (BangOccupantInfo)
            _bangobj.getOccupantInfo(_bangobj.players[_pidx]);
        if (boi != null) {
            BufferedImage aimage = AvatarView.getImage(_ctx, boi.avatar);
            _avatar = new ImageIcon(
                aimage.getScaledInstance(
                    AvatarLogic.WIDTH/10, AvatarLogic.HEIGHT/10,
                    BufferedImage.SCALE_SMOOTH));
        }
    }

    protected void updateStatus ()
    {
        _cash.setText("$" + _bangobj.funds[_pidx]);
    }

    protected BButton createButton (Card card)
    {
        BIcon icon = new ImageIcon(
            _ctx.loadImage("cards/" + card.getType() + "/icon.png"));
        BButton btn = new BButton(icon, "" + card.cardId);
        btn.setStyleClass("card_button");
        btn.addListener(this);
        return btn;
    }

    protected SubimageIcon createRankIcon (int rank)
    {
        return new SubimageIcon(
            _rankimg, (rank + 1) * RANK_RECT.width, 0,
            RANK_RECT.width, RANK_RECT.height);
    }

    protected BangContext _ctx;
    protected BangObject _bangobj;
    protected BangController _ctrl;
    protected int _pidx, _rank = -1;

    protected ImageIcon _color, _avatar;
    protected Image _rankimg;

    protected BLabel _player, _cash, _ranklbl;
    protected BButton[] _cards = new BButton[GameCodes.MAX_CARDS];

    protected static final Point BACKGROUND_LOC = new Point(33, 13);
    protected static final Point AVATAR_LOC = new Point(33, 8);
    protected static final Point CASH_LOC = new Point(97, 34);

    protected static final Rectangle RANK_RECT = new Rectangle(8, 35, 21, 23);
    protected static final Rectangle NAME_RECT = new Rectangle(11, 0, 100, 16);
    protected static final Rectangle CARD_RECT = new Rectangle(146, 16, 30, 39);
}
