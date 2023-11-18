package com.ioliveira.admin.catalogo.infrastructure.api.controllers;

import com.ioliveira.admin.catalogo.infrastructure.api.CastMemberAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CastMemberController implements CastMemberAPI {

    @Override
    public ResponseEntity<?> createGenre(final Object input) {
        return null;
    }
}
