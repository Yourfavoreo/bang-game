//
// $Id$

package com.threerings.bang.game.client;

/**
 * Waits for all sprites involved in a shot to stop moving and then
 * animates the fired shot.
 */
public class InstantShotHandler extends ShotHandler
{
    protected void fireShot (int sx, int sy, int tx, int ty)
    {
        // play the bang sound
        if (_bangSound != null) {
            _bangSound.play(false);
        }

        // apply the shot effect which will damage the target and trigger
        // the appropriate animations and whatnot
        _view.applyEffectDirect(_shot);
    }
}
