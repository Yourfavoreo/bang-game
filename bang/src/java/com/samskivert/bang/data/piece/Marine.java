//
// $Id$

package com.samskivert.bang.data.piece;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import com.threerings.media.util.MathUtil;

import com.samskivert.bang.client.sprite.PieceSprite;
import com.samskivert.bang.client.sprite.UnitSprite;
import com.samskivert.bang.data.BangObject;
import com.samskivert.bang.data.Shot;
import com.samskivert.bang.util.PieceSet;
import com.samskivert.bang.util.PointSet;

import static com.samskivert.bang.Log.log;

/**
 * Handles the state and behavior of the marine piece.
 */
public class Marine extends Piece
    implements PlayerPiece
{
    /** A marine can fire at a target up to two squares away. */
    public static final int FIRE_DISTANCE = 2;

    @Override // documentation inherited
    public PieceSprite createSprite ()
    {
        return new UnitSprite("marine");
    }

    @Override // documentation inherited
    public int getSightDistance ()
    {
        return 9;
    }

    @Override // documentation inherited
    public void react (BangObject bangobj, Piece[] pieces, PieceSet updates,
                       ArrayList<Shot> shots)
    {
        Piece target = null;
        int dist = Integer.MAX_VALUE;
        int fdist = FIRE_DISTANCE*FIRE_DISTANCE;

        // locate the closest target in range and shoot 'em!
        for (int ii = 0; ii < pieces.length; ii++) {
            Piece p = pieces[ii];
            if (!validTarget(p)) {
                continue;
            }
            int pdist = MathUtil.distanceSq(x, y, p.x, p.y);
            if (pdist <= fdist && pdist < dist) {
                dist = pdist;
                target = p;
            }
        }

        if (target != null) {
            shots.add(shoot(target));
        }
    }

    @Override // documentation inherited
    public void enumerateAttacks (PointSet set)
    {
        int fdist = FIRE_DISTANCE*FIRE_DISTANCE;
        for (int yy = y - FIRE_DISTANCE; yy <= y + FIRE_DISTANCE; yy++) {
            for (int xx = x - FIRE_DISTANCE; xx <= x + FIRE_DISTANCE; xx++) {
                int pdist = MathUtil.distanceSq(x, y, xx, yy);
                if ((xx != x || yy != y) && (pdist <= fdist)) {
                    set.add(xx, yy);
                }
            }
        }
    }

    @Override // documentation inherited
    protected int computeDamage (Piece target)
    {
        if (target instanceof Tank) {
            return 13;
        } else if (target instanceof Chopper) {
            return 50;
        } else if (target instanceof Artillery) {
            return 25;
        } else if (target instanceof Marine) {
            return 20;
        } else {
            return super.computeDamage(target);
        }
    }
}
