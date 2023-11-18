package com.ioliveira.admin.catalogo.infrastructure.api.controllers;

import com.ioliveira.admin.catalogo.application.castmember.create.CreateCastMemberCommand;
import com.ioliveira.admin.catalogo.application.castmember.create.CreateCastMemberOutput;
import com.ioliveira.admin.catalogo.application.castmember.create.CreateCastMemberUseCase;
import com.ioliveira.admin.catalogo.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import com.ioliveira.admin.catalogo.infrastructure.api.CastMemberAPI;
import com.ioliveira.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;
import com.ioliveira.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import com.ioliveira.admin.catalogo.infrastructure.castmember.presenters.CastMemberPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final CreateCastMemberUseCase createCastMemberUseCase;
    private final GetCastMemberByIdUseCase getCastMemberByIdUseCase;

    public CastMemberController(
            final CreateCastMemberUseCase createCastMemberUseCase,
            final GetCastMemberByIdUseCase getCastMemberByIdUseCase) {

        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
        this.getCastMemberByIdUseCase = Objects.requireNonNull(getCastMemberByIdUseCase);
    }


    @Override
    public ResponseEntity<?> createCastMember(final CreateCastMemberRequest request) {

        final CreateCastMemberCommand command = CreateCastMemberCommand.with(request.name(), request.type());

        final CreateCastMemberOutput output = this.createCastMemberUseCase.execute(command);

        return ResponseEntity
                .created(URI.create("/cast_members/" + output.id()))
                .body(output);
    }

    @Override
    public CastMemberResponse getById(final String id) {

        return CastMemberPresenter
                .present(this.getCastMemberByIdUseCase.execute(id));
    }
}
