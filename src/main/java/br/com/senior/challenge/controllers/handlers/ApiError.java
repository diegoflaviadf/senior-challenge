package br.com.senior.challenge.controllers.handlers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO para exibir mensagens de erro
 */
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp = LocalDateTime.now();

    private final List<Error> messages = new ArrayList<>(0);

    /**
     * Cria um {@link ApiError} com uma mensagem, inicialmente
     *
     * @param message Mensagem de erro
     */
    public ApiError(String message) {
        addError(message);
    }

    /**
     * Adiciona uma mensagem de erro
     *
     * @param message Mensagem de erro
     */
    public void addError(String message) {
        addError(null, message);
    }

    /**
     * Adiciona uma mensagem de erro ligada a um campo
     *
     * @param field Campo ligado à mensagem de erro
     * @param message Mensagem de erro
     */
    public void addError(String field, String message) {
        addError(field, message, null);
    }

    /**
     * Adiciona uma mensagem de erro ligada a um campo com uma ajuda
     *
     * @param field Campo ligado à mensagem de erro
     * @param message Mensagem de erro
     * @param helper Ajuda relacionado à mensagem e ao campo
     */
    public void addError(String field, String message, String helper) {
        addError(new Error(field, message, helper));
    }

    /**
     * Adiciona um {@link Error}
     *
     * @param error Erro
     */
    public void addError(Error error) {
        messages.add(error);
    }

    /**
     * POJO para exibir um erro
     */
    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Error {

        private final String field;
        private final String message;
        private final String helper;

    }

}
