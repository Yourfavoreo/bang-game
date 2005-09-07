//
// $Id$

package com.threerings.bang.game.data.card;

import com.threerings.bang.game.data.BangObject;
import com.threerings.bang.game.data.effect.AreaDamageEffect;
import com.threerings.bang.game.data.effect.Effect;

/**
 * A card that allows the player to launch a missile that does an area of
 * effect damage.
 */
public class Missile extends Card
{
    public int power = 60;

    public int radius = 2;

    @Override // documentation inherited
    public void init (BangObject bangobj, int owner)
    {
        super.init(bangobj, owner);

        // if our player is "in the nooksak", give them a big missile
        if (bangobj.pdata[owner].power < 30) {
            power = 100;
            radius = 4;
        } else if (bangobj.pdata[owner].powerFactor < 0.34) {
            power = 80;
            radius = 3;
        }
    }

    @Override // documentation inherited
    public String getIconPath ()
    {
        return "missile";
    }

    @Override // documentation inherited
    public int getRadius ()
    {
        return radius;
    }

    @Override // documentation inherited
    public Effect activate (int x, int y)
    {
        return new AreaDamageEffect(owner, power, getRadius(), x, y);
    }
}
