package com.ioliveira.admin.catalogo.application.castmember.create;

import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.admin.catalogo.domain.validation.handler.Notification;

import java.util.Objects;

public class DefaultCreateCastMemberUseCase extends CreateCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultCreateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public CreateCastMemberOutput execute(final CreateCastMemberCommand command) {

        final Notification notification = Notification.create();
        final CastMember castMember = notification.validate(() -> CastMember.newMember(command.name(), command.type()));

        if (notification.hasErrors()) {
            throw new NotificationException("Could not create an aggregate CastMember", notification);
        }

        return CreateCastMemberOutput.from(this.castMemberGateway.create(castMember));
    }
}
