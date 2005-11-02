//
// $Id$

package com.threerings.bang.avatar.server;

import com.threerings.bang.avatar.client.BarberService;
import com.threerings.bang.avatar.data.BarberMarshaller;
import com.threerings.bang.avatar.data.Look;
import com.threerings.presents.client.Client;
import com.threerings.presents.client.InvocationService;
import com.threerings.presents.data.ClientObject;
import com.threerings.presents.data.InvocationMarshaller;
import com.threerings.presents.server.InvocationDispatcher;
import com.threerings.presents.server.InvocationException;

/**
 * Dispatches requests to the {@link BarberProvider}.
 */
public class BarberDispatcher extends InvocationDispatcher
{
    /**
     * Creates a dispatcher that may be registered to dispatch invocation
     * service requests for the specified provider.
     */
    public BarberDispatcher (BarberProvider provider)
    {
        this.provider = provider;
    }

    // documentation inherited
    public InvocationMarshaller createMarshaller ()
    {
        return new BarberMarshaller();
    }

    // documentation inherited
    public void dispatchRequest (
        ClientObject source, int methodId, Object[] args)
        throws InvocationException
    {
        switch (methodId) {
        case BarberMarshaller.CONFIGURE_LOOK:
            ((BarberProvider)provider).configureLook(
                source,
                (String)args[0], (int[])args[1], (InvocationService.ConfirmListener)args[2]
            );
            return;

        case BarberMarshaller.PURCHASE_LOOK:
            ((BarberProvider)provider).purchaseLook(
                source,
                (Look)args[0], (InvocationService.ConfirmListener)args[1]
            );
            return;

        default:
            super.dispatchRequest(source, methodId, args);
        }
    }
}
