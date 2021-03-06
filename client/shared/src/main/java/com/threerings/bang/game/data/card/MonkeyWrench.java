//
// $Id$

package com.threerings.bang.game.data.card;

import com.threerings.bang.data.BangCodes;
import com.threerings.bang.data.UnitConfig;

import com.threerings.bang.game.data.BangObject;
import com.threerings.bang.game.data.effect.Effect;
import com.threerings.bang.game.data.effect.MonkeyWrenchEffect;
import com.threerings.bang.game.data.piece.Piece;
import com.threerings.bang.game.data.piece.Unit;

/**
 * A card that makes a unit take damage at every step moved.
 */
public class MonkeyWrench extends Card
{
    @Override // documentation inherited
    public String getType ()
    {
        return "monkey_wrench";
    }

    @Override // documentation inherited
    public boolean isValidPiece (BangObject bangobj, Piece target)
    {
        return (target instanceof Unit && target.isAlive() && 
                ((Unit)target).getConfig().make == UnitConfig.Make.STEAM);
    }

    @Override // documentation inherited
    public String getTownId ()
    {
        return BangCodes.BOOM_TOWN;
    }

    @Override // documentation inherited
    public int getWeight ()
    {
        return 40;
    }

    @Override // documentation inherited
    public int getScripCost ()
    {
        return 40;
    }

    @Override // documenataion inherited
    public Effect activate (BangObject bangobj, Object target)
    {
        MonkeyWrenchEffect effect = new MonkeyWrenchEffect();
        effect.pieceId = (Integer)target;
        return effect;
    }
}
