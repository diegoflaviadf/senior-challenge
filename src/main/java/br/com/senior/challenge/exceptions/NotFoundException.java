package br.com.senior.challenge.exceptions;

import java.util.UUID;

/**
 * Exception para registro não encontrado
 */
public class NotFoundException extends BusinessException {

    public NotFoundException(UUID id) {
        super("record.notFound", id.toString());
    }

}
