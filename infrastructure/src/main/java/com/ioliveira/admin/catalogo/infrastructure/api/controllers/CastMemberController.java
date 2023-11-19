package com.ioliveira.admin.catalogo.infrastructure.api.controllers;

import com.ioliveira.admin.catalogo.application.castmember.create.CreateCastMemberCommand;
import com.ioliveira.admin.catalogo.application.castmember.create.CreateCastMemberOutput;
import com.ioliveira.admin.catalogo.application.castmember.create.CreateCastMemberUseCase;
import com.ioliveira.admin.catalogo.application.castmember.delete.DeleteCastMemberUseCase;
import com.ioliveira.admin.catalogo.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import com.ioliveira.admin.catalogo.application.castmember.update.UpdateCastMemberCommand;
import com.ioliveira.admin.catalogo.application.castmember.update.UpdateCastMemberUseCase;
import com.ioliveira.admin.catalogo.infrastructure.api.CastMemberAPI;
import com.ioliveira.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;
import com.ioliveira.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import com.ioliveira.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import com.ioliveira.admin.catalogo.infrastructure.castmember.presenters.CastMemberPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final CreateCastMemberUseCase createCastMemberUseCase;
    private final GetCastMemberByIdUseCase getCastMemberByIdUseCase;
    private final UpdateCastMemberUseCase updateCastMemberUseCase;
    private final DeleteCastMemberUseCase deleteCastMemberUseCase;

    public CastMemberController(
            final CreateCastMemberUseCase createCastMemberUseCase,
            final GetCastMemberByIdUseCase getCastMemberByIdUseCase,
            final UpdateCastMemberUseCase updateCastMemberUseCase, final DeleteCastMemberUseCase deleteCastMemberUseCase) {

        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
        this.getCastMemberByIdUseCase = Objects.requireNonNull(getCastMemberByIdUseCase);
        this.updateCastMemberUseCase = Objects.requireNonNull(updateCastMemberUseCase);
        this.deleteCastMemberUseCase = Objects.requireNonNull(deleteCastMemberUseCase);
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

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateCastMemberRequest input) {

        final UpdateCastMemberCommand command = UpdateCastMemberCommand.with(id, input.name(), input.type());

        return ResponseEntity.ok(this.updateCastMemberUseCase.execute(command));
    }

    @Override
    public void deleteById(final String id) {
        this.deleteCastMemberUseCase.execute(id);
    }
}
