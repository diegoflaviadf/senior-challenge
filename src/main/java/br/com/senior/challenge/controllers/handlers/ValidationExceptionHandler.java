package br.com.senior.challenge.controllers.handlers;

import br.com.senior.challenge.entities.EntityConstraints;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;

import static br.com.senior.challenge.config.DefaultMessageSourceConfig.DEFAULT_LOCALE;

/**
 * Cria um {@link ControllerAdvice} para tratar mensagens de validação
 */
@ControllerAdvice
@Component
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Mensagem padrão para campo com formato inválido
     */
    public static final String FIELD_INVALID_FORMAT = "field.invalid.format";

    private final MessageSource messageSource;

    public ValidationExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception) {
        String key = ConstraintHandler.with(EntityConstraints.class).getKey(exception.getMostSpecificCause().getLocalizedMessage());
        final String message = messageSource.getMessage(key, null, DEFAULT_LOCALE);
        return new ResponseEntity(new ApiError(message), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RollbackException.class)
    public ResponseEntity<ApiError> handleRollbackException(RollbackException exception) {
        if (exception.getCause() instanceof ConstraintViolationException) {
            final ConstraintViolationException cause = (ConstraintViolationException) exception.getCause();
            return handleConstraintViolationException(cause);
        } else {
            return new ResponseEntity(new ApiError(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Trata mensagens com formato inválido
     *
     * @param exception Exception de formato inválido {@link InvalidFormatException}
     * @return {@link ResponseEntity} mensagem tratada
     */
    public ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException exception) {
        final ApiError errors = new ApiError();
        final JsonMappingException.Reference reference = exception.getPath().get(0);
        final String message = messageSource.getMessage(FIELD_INVALID_FORMAT, null, DEFAULT_LOCALE);
        final String helper = getFieldHelper(reference.getFrom().getClass().getName(), reference.getFieldName());
        errors.addError(reference.getFieldName(), message, helper);
        return new ResponseEntity(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Trata mensagens com violação de integridad
     *
     * @param exception Exception de violação de integridade {@link ConstraintViolationException}
     * @return {@link ResponseEntity} mensagem tratada
     */
    private ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException exception) {
        final ApiError errors = new ApiError();
        exception.getConstraintViolations().forEach(constraintViolation -> {
            final String field = constraintViolation.getPropertyPath().toString();
            final String message = constraintViolation.getMessage();
            String customMessage = replaceAttributes(getMessage(message, field), constraintViolation);
            String helper = getFieldHelper(constraintViolation.getLeafBean().getClass().getName(), field);
            errors.addError(field, customMessage, helper);
        });
        return new ResponseEntity(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Retorna uma mensagem tratada para um campo
     *
     * @param message Mensagem
     * @param field   Campo
     * @return Mensagem tratada para um campo
     */
    private String getMessage(String message, String field) {
        final String key = message + "." + field;
        String customMessage = messageSource.getMessage(key, null, DEFAULT_LOCALE);
        if (customMessage.equals(key)) {
            customMessage = messageSource.getMessage(message, null, DEFAULT_LOCALE);
        }
        return customMessage;
    }

    /**
     * Retorna uma ajuda para um campo e mensagem
     *
     * @param message Mensagem
     * @param field   Campo
     * @return Ajuda tratada para um campo
     */
    private String getFieldHelper(String message, String field) {
        final String key = message + "." + field + ".helper";
        final String customMessage = messageSource.getMessage(key, null, DEFAULT_LOCALE);
        if (customMessage.equals(key)) {
            return null;
        }
        return customMessage;
    }

    /**
     * Sobrescereve atributos entre chaves ({att}) da mensagem
     *
     * @param message             Mensagem
     * @param constraintViolation Violação de integridade {@link ConstraintViolation}
     * @return Ajuda tratada para um campo
     */
    private String replaceAttributes(String message, ConstraintViolation constraintViolation) {
        String newMessage = message;
        final Map<String, Object> attributes = constraintViolation.getConstraintDescriptor().getAttributes();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            newMessage = newMessage.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return newMessage;
    }

}
