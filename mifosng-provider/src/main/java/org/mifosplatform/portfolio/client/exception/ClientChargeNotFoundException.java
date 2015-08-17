package org.mifosplatform.portfolio.client.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ClientChargeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ClientChargeNotFoundException(final Long id) {
        super("error.msg.client.charge.id.invalid", "Client charge with identifier " + id + " does not exist", id);
    }

    public ClientChargeNotFoundException(final Long id, final Long clientId) {
        super("error.msg.client.charge.id.invalid.for.given.client",
                "Client charge with identifier " + id + " does not exist for client with id " + clientId, id, clientId);
    }

}