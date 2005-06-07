//
// $Id$

package com.threerings.bang.ranch.client;

import com.jme.bui.BToggleButton;

import com.threerings.bang.data.UnitConfig;
import com.threerings.bang.util.BangContext;

/**
 * Displays a static view of a unit model for use as an icon in interface
 * displays.
 */
public class UnitIcon extends BToggleButton
{
    public UnitIcon (BangContext ctx, UnitConfig config)
    {
        super(config.type);
        _config = config;
    }

    public UnitConfig getUnit ()
    {
        return _config;
    }

    protected void stateDidChange ()
    {
        super.stateDidChange();
        if (_selected && _parent instanceof UnitPalette) {
            ((UnitPalette)_parent).iconSelected(this);
        }
    }

    protected UnitConfig _config;
}
