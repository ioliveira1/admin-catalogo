package com.ioliveira.admin.catalogo.domain.castmember;

import com.ioliveira.admin.catalogo.domain.AggregateRoot;
import com.ioliveira.admin.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.admin.catalogo.domain.utils.InstantUtils;
import com.ioliveira.admin.catalogo.domain.validation.ValidationHandler;
import com.ioliveira.admin.catalogo.domain.validation.handler.Notification;

import java.time.Instant;

public class CastMember extends AggregateRoot<CastMemberID> {

    private String name;
    private CastMemberType type;
    private Instant createdAt;
    private Instant updatedAt;

    private CastMember(final CastMemberID castMemberID, final String name, final CastMemberType type, final Instant createdAt, final Instant updatedAt) {
        super(castMemberID);
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        selfValidate();
    }

    public static CastMember newMember(final String name, final CastMemberType type) {
        final Instant now = InstantUtils.now();
        return new CastMember(CastMemberID.unique(), name, type, now, now);
    }

    public CastMember update(final String name, final CastMemberType type) {
        this.name = name;
        this.type = type;
        this.updatedAt = Instant.now();
        selfValidate();
        return this;
    }

    private void selfValidate() {
        final Notification notification = Notification.create();
        validate(notification);

        if (notification.hasErrors()) {
            throw new NotificationException("Failed to create an aggregate CastMember", notification);
        }
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new CastMemberValidator(handler, this).validate();
    }

    public String getName() {
        return name;
    }

    public CastMemberType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
