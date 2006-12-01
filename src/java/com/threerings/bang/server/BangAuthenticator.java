//
// $Id$

package com.threerings.bang.server;

import com.samskivert.io.PersistenceException;

import com.threerings.presents.server.Authenticator;

/**
 * Extends the standard authenticator with some extra bits.
 */
public abstract class BangAuthenticator extends Authenticator
{
    /**
     * Called to indicate that an account has become an active Bang! player (played for the first
     * time) or is no longer an active Bang! player (their Bang! data has been purged).
     */
    public abstract void setAccountIsActive (String username, boolean isActive)
        throws PersistenceException;
}