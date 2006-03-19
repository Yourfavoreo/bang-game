//
// $Id$

package com.threerings.bang.game.client.sprite;

import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;

import com.jmex.bui.background.BBackground;

import com.threerings.bang.game.client.TerrainNode;
import com.threerings.bang.game.data.piece.Piece;

import com.threerings.bang.util.BasicContext;
import com.threerings.bang.util.RenderUtil;

import static com.threerings.bang.Log.log;
import static com.threerings.bang.client.BangMetrics.*;

/**
 * A helper class to manage the composition of our unit status display.
 */
public class UnitStatus extends Node
{
    /** The size of the status textures. */
    public static final int STATUS_SIZE = 128;

    /** The size of the status icon on screen. */
    public static final int ICON_SIZE = 64;

    /**
     * Creates a unit status helper with the supplied unit sprite highlight
     * node. The status will be textured onto the highlight node (using a
     * {@link SharedMesh}) and will be textured onto a set of quads which will
     * be used to display our iconic unit status (which we make available as a
     * {@link BBackground}.
     */
    public UnitStatus (BasicContext ctx, TerrainNode.Highlight highlight)
    {
        super("unit_status");
        _ctx = ctx;

        // load and create our various textures if necessary
        if (_tempstate == null) {
            // we'll use this to load our textures into OpenGL as we go
            _tempstate = ctx.getRenderer().createTextureState();

            // load up our various static textures
            _ticktexs = new Texture[5];
            for (int ii = 0; ii < _ticktexs.length; ii++) {
                _ticktexs[ii] = prepare("tick_counter_" + ii + ".png");
            }
            _movetex = prepare("move_order.png");
            _shoottex = prepare("shoot_order.png");
            _outtex = prepare("tick_outline.png");
            _routtex = prepare("tick_ready_outline.png");

            // we generate ten discrete damage levels and pick the closest one
            // to represent a unit's damage (this is to avoid slow and
            // expensive BufferedImage rendering during the game)
            BufferedImage empty = ctx.getImageCache().getBufferedImage(
                PPRE + "health_meter_empty.png");
            BufferedImage full = ctx.getImageCache().getBufferedImage(
                PPRE + "health_meter_full.png");
            _damtexs = new Texture[11];
            _damtexs[0] = RenderUtil.createTexture(
                ctx.getImageCache().createImage(full, false));
            _damtexs[10] = RenderUtil.createTexture(
                ctx.getImageCache().createImage(empty, false));
            for (int ii = 1; ii < 10; ii++) {
                _damtexs[ii] = createDamageTexture(ctx, empty, full, ii*10);
            }
            for (int ii = 0; ii < _damtexs.length; ii++) {
                prepare(_damtexs[ii]);
            }
        }

        // we have one outline and three info layers
        for (int ii = 0; ii < _info.length; ii++) {
            _info[ii] = new SharedMesh("info" + ii, highlight);
            _info[ii].setRenderState(ctx.getRenderer().createTextureState());
            _info[ii].updateRenderState();
            attachChild(_info[ii]);

            _icon[ii] = new Quad("icon" + ii, ICON_SIZE, ICON_SIZE);
            _icon[ii].setRenderState(ctx.getRenderer().createTextureState());
            _icon[ii].setRenderState(RenderUtil.blendAlpha);
            _icon[ii].getLocalTranslation().x = ICON_SIZE/2f;
            _icon[ii].getLocalTranslation().y = ICON_SIZE/2f;
            _icon[ii].updateGeometricState(0, true);
            _icon[ii].updateRenderState();
        }

        // we'll set up textures in the first call to update()
    }

    /**
     * Called by the {@link UnitSprite} to keep our textures rotated in line
     * with the camera.
     */
    public void rotateWithCamera (Quaternion camrot, Vector3f camtrans)
    {
        for (int ii = 0; ii < _info.length; ii++) {
            if (_info[ii].getCullMode() != CULL_ALWAYS) {
                Texture tex = getTextureState(_info[ii]).getTexture();
                if (tex != null) {
                    tex.setRotation(camrot);
                    tex.setTranslation(camtrans);
                }
            }
        }
    }

    /**
     * Returns a background that can be used to render this unit's status in
     * iconic form in the unit status user interface.
     */
    public BBackground getIconBackground ()
    {
        return new BBackground() {
            public int getMinimumWidth () {
                return ICON_SIZE;
            }
            public int getMinimumHeight () {
                return ICON_SIZE;
            }
            public void render (Renderer renderer, int x, int y,
                                int width, int height, float alpha) {
                for (int ii = 0; ii < _icon.length; ii++) {
                    if (_icon[ii].getCullMode() != CULL_ALWAYS) {
                        _icon[ii].draw(renderer);
                    }
                }
            }
        };
    }

    /**
     * Recomposites if necessary our status texture and updates the texture
     * state.
     */
    public void update (Piece piece, int ticksToMove,
                        UnitSprite.AdvanceOrder pendo, boolean selected)
    {
        if (_ticksToMove != ticksToMove) {
            _ticksToMove = ticksToMove;

            // update our tick texture
            int tickidx = Math.max(0, 4-ticksToMove);
            Texture ttex = _ticktexs[tickidx];
            getTextureState(_info[1]).setTexture(ttex.createSimpleClone());
            getTextureState(_icon[1]).setTexture(ttex.createSimpleClone());

            // update our outline texture
            Texture otex = (_ticksToMove > 0) ? _outtex : _routtex;
            getTextureState(_info[0]).setTexture(otex.createSimpleClone());
            getTextureState(_icon[0]).setTexture(otex.createSimpleClone());
        }

        if (_owner != piece.owner) {
            // set up our starting outline color the first time we're updated
            if (_owner == -1) {
                _info[0].setDefaultColor(DARKER_COLORS[piece.owner]);
                _icon[0].setDefaultColor(DARKER_COLORS[piece.owner]);
            }
            _owner = piece.owner;
            for (int ii = 1; ii < _info.length; ii++) {
                _info[ii].setDefaultColor(JPIECE_COLORS[_owner]);
                _icon[ii].setDefaultColor(JPIECE_COLORS[_owner]);
            }
        }

        int dlevel = (int)Math.ceil(piece.damage/10f);
        if (_dlevel != dlevel) {
            _dlevel = dlevel;
            Texture dtex = _damtexs[dlevel];
            getTextureState(_info[2]).setTexture(dtex.createSimpleClone());
            getTextureState(_icon[2]).setTexture(dtex.createSimpleClone());
        }

        if (_pendo == null || _pendo != pendo) {
            _pendo = pendo;

            Texture otex = null;
            switch (_pendo) {
            case MOVE: otex = _movetex; break;
            case MOVE_SHOOT: otex = _shoottex; break;
            }
            if (otex == null) {
                _info[3].setCullMode(CULL_ALWAYS);
                _icon[3].setCullMode(CULL_ALWAYS);
            } else {
                _info[3].setCullMode(CULL_DYNAMIC);
                _icon[3].setCullMode(CULL_DYNAMIC);
                getTextureState(_info[3]).setTexture(otex.createSimpleClone());
                getTextureState(_icon[3]).setTexture(otex.createSimpleClone());
            }
        }

        if (_selected != selected) {
            _selected = selected;
            ColorRGBA color = _selected ?
                ColorRGBA.white : DARKER_COLORS[_owner];
            _info[0].setDefaultColor(color);
            _icon[0].setDefaultColor(color);
        }
    }

    protected Texture prepare (String path)
    {
        Texture tex = RenderUtil.createTexture(
            _ctx.getImageCache().getImage(PPRE + path, false));
        return prepare(tex);
    }

    protected Texture prepare (Texture texture)
    {
        texture.setWrap(Texture.WM_BCLAMP_S_BCLAMP_T);
        _tempstate.setTexture(texture);
        _tempstate.load();
        return texture;
    }

    protected final TextureState getTextureState (Spatial spatial)
    {
        return (TextureState)spatial.getRenderState(RenderState.RS_TEXTURE);
    }

    protected static Texture createDamageTexture (
        BasicContext ctx, BufferedImage empty, BufferedImage full, int level)
    {
        BufferedImage target = ctx.getImageCache().createCompatibleImage(
            STATUS_SIZE, STATUS_SIZE, true);
        Graphics2D gfx = (Graphics2D)target.getGraphics();
        try {
            // combine the empty and full images with a custom clip
            gfx.drawImage(empty, 0, 0, null);
            float extent = (100 - level) / 100f * (90 - 2*ARC_INSETS);
            gfx.setClip(new Arc2D.Float(
                            -STATUS_SIZE/8, -STATUS_SIZE/8,
                            // expand the width and height a smidge to avoid
                            // funny business around the edges
                            10*STATUS_SIZE/8, 10*STATUS_SIZE/8,
                            90 - ARC_INSETS - extent, extent, Arc2D.PIE));
            gfx.drawImage(full, 0, 0, null);
        } finally {
            gfx.dispose();
        }
        return RenderUtil.createTexture(
            ctx.getImageCache().convertImage(target));
    }

    protected BasicContext _ctx;
    protected int _owner = -1, _ticksToMove = -1, _dlevel = -1;
    protected boolean _selected;
    protected UnitSprite.AdvanceOrder _pendo;

    protected SharedMesh[] _info = new SharedMesh[4];
    protected Quad[] _icon = new Quad[4];

    protected static TextureState _tempstate;
    protected static Texture[] _ticktexs, _damtexs;
    protected static Texture _outtex, _routtex, _movetex, _shoottex;

    /** Defines the amount by which the damage arc image is inset from a
     * full quarter circle (on each side): 8 degrees. */
    protected static final float ARC_INSETS = 7;

    /** The path prefix for all of our textures. */
    protected static final String PPRE = "textures/ustatus/";
}
