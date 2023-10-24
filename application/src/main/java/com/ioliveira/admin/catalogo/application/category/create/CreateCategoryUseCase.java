package com.ioliveira.admin.catalogo.application.category.create;

import com.ioliveira.admin.catalogo.application.UseCase;
import com.ioliveira.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class CreateCategoryUseCase extends UseCase<CreateCategoryCommand, Either<Notification, CreateCategoryOutput>> {
}
