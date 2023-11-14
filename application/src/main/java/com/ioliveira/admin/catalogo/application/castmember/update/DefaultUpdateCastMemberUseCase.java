package com.ioliveira.admin.catalogo.application.castmember.update;

import com.ioliveira.admin.catalogo.domain.Identifier;
import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.admin.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.admin.catalogo.domain.validation.handler.Notification;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultUpdateCastMemberUseCase extends UpdateCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultUpdateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public UpdateCastMemberOutput execute(final UpdateCastMemberCommand command) {
        final CastMemberID id = CastMemberID.from(command.id());

        final CastMember member = this.castMemberGateway
                .findById(id)
                .orElseThrow(notFound(id));

        final Notification notification = Notification.create();
        notification.validate(() -> member.update(command.name(), command.type()));

        if (notification.hasErrors()) {
            throw new NotificationException("Could not update an aggregate CastMember", notification);
        }

        return UpdateCastMemberOutput.from(this.castMemberGateway.update(member));
    }

    private static Supplier<NotFoundException> notFound(final Identifier id) {
        return () -> NotFoundException.with(CastMember.class, id);
    }
}
