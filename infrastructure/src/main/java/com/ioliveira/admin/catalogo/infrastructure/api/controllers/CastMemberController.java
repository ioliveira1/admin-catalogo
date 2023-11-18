package com.ioliveira.admin.catalogo.infrastructure.api.controllers;

import com.ioliveira.admin.catalogo.application.castmember.create.CreateCastMemberCommand;
import com.ioliveira.admin.catalogo.application.castmember.create.CreateCastMemberOutput;
import com.ioliveira.admin.catalogo.application.castmember.create.CreateCastMemberUseCase;
import com.ioliveira.admin.catalogo.infrastructure.api.CastMemberAPI;
import com.ioliveira.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final CreateCastMemberUseCase createCastMemberUseCase;

    public CastMemberController(final CreateCastMemberUseCase createCastMemberUseCase) {
        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
    }


    @Override
    public ResponseEntity<?> createGenre(final CreateCastMemberRequest request) {

        final CreateCastMemberCommand command = CreateCastMemberCommand.with(request.name(), request.type());

        final CreateCastMemberOutput output = createCastMemberUseCase.execute(command);

        return ResponseEntity
                .created(URI.create("/cast_members/" + output.id()))
                .body(output);
    }
}
