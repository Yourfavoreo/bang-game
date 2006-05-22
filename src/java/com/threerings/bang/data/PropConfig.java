//
// $Id$

package com.threerings.bang.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import com.samskivert.util.StringUtil;
import com.threerings.bang.util.BangUtil;

import static com.threerings.bang.Log.log;

/**
 * Loads and manages prop configuration information.
 */
public class PropConfig
{
    /** The name of this prop type (ie. <code>buildings/saloon</code>). */
    public String type;

    /** The width of this prop in tiles. */
    public int width;

    /** The height of this prop in tiles. */
    public int height;

    /** The vertical size of the prop in tiles. */
    public float depth;
    
    /** If true, even air units cannot pass over the prop. */
    public boolean tall;
    
    /** A custom class for this prop, if one was specified. */
    public String propClass;

    /** Whether or not units can pass over/through the prop. */
    public boolean passable;
    
    /** Whether or not units can fire through the prop. */
    public boolean penetrable;
    
    /** Returns a string representation of this instance. */
    public String toString ()
    {
        return StringUtil.fieldsToString(this);
    }

    /**
     * Returns the prop configuration for the specified prop type.
     */
    public static PropConfig getConfig (String type)
    {
        return _types.get(type);
    }

    /**
     * Returns a collection containing all registered prop configurations.
     */
    public static Collection<PropConfig> getConfigs ()
    {
        return _types.values();
    }
    
    /**
     * Returns an array of configurations for all prop types
     * accessible in the specified town.
     */
    public static PropConfig[] getTownProps (String townId)
    {
        return _townMap.get(townId);
    }

    public static void main (String[] args)
    {
        for (PropConfig config : _types.values()) {
            System.err.println("" + config);
        }
    }

    protected static void registerProp (String type)
    {
        // load up the properties file for this prop
        Properties props = BangUtil.resourceToProperties(
            "rsrc/props/" + type + "/prop.properties");

        // fill in a config instance from the properties file
        PropConfig config = new PropConfig();
        config.type = type;
        config.propClass = props.getProperty("class");

        config.width = BangUtil.getIntProperty(type, props, "width", 1);
        config.height = BangUtil.getIntProperty(type, props, "height", 1);
        config.depth = BangUtil.getFloatProperty(type, props, "depth", 2f);
        
        config.tall = Boolean.parseBoolean(props.getProperty("tall"));
        config.passable = Boolean.parseBoolean(props.getProperty("passable"));
        config.penetrable = Boolean.parseBoolean(
            props.getProperty("penetrable",
                (config.width == 1 && config.height == 1) ? "true" : "false"));
        
        // map this config into the proper towns
        String towns = BangUtil.requireProperty(type, props, "towns");
        boolean andSoOn = false;
        for (int ii = 0; ii < BangCodes.TOWN_IDS.length; ii++) {
            String town = BangCodes.TOWN_IDS[ii];
            if (andSoOn || towns.indexOf(town) != -1) {
                mapTown(town, config);
                andSoOn = andSoOn || (towns.indexOf(town + "+") != -1);
            }
        }

        // map the type to the config
        _types.put(type, config);
    }

    protected static void mapTown (String town, PropConfig config)
    {
        PropConfig[] configs = _townMap.get(town);
        if (configs == null) {
            configs = new PropConfig[0];
        }
        PropConfig[] nconfigs = new PropConfig[configs.length+1];
        System.arraycopy(configs, 0, nconfigs, 0, configs.length);
        nconfigs[configs.length] = config;
        _townMap.put(town, nconfigs);
    }

    /** A mapping from prop type to its configuration. */
    protected static HashMap<String,PropConfig> _types =
        new HashMap<String,PropConfig>();

    /** A mapping from town to all props accessible in that town. */
    protected static HashMap<String,PropConfig[]> _townMap =
        new HashMap<String,PropConfig[]>();

    static {
        // register our props
        String[] props = BangUtil.resourceToStrings(
            "rsrc/props/props.txt");
        for (int ii = 0; ii < props.length; ii++) {
            registerProp(props[ii]);
        }
    }
}
