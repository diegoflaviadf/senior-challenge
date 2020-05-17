package br.com.senior.challenge.controllers.handlers;

import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

/**
 * Handler para tratar mensagens de Violação de Integridade
 */
@AllArgsConstructor
class ConstraintHandler {

    /**
     * Prefixo para as mensagens de erro de integridade
     */
    public static final String PREFIX = "constraint.violation";

    private Class<?> clazz;

    private ConstraintHandler(){
        //Utility Class
    }

    /**
     * Adiciona uma classe com as definições das constraints
     *
     * @param clazz Classe com Constraints
     * @return Novo {@link ConstraintHandler}
     */
    public static ConstraintHandler with(Class<?> clazz) {
        return new ConstraintHandler(clazz);
    }

    /**
     * Retorna a chave de uma mensagem
     *
     * @param message Mensagem de erro de constraint violation
     * @return Chave de erro
     */
    public String getKey(String message) {
        final Field[] declaredFields = clazz.getDeclaredFields();
        final Optional<Field> field = Arrays.stream(declaredFields)
                .filter(f -> message.toLowerCase().contains(f.getName().toLowerCase()))
                .findFirst();
        return field.map(value -> PREFIX + "." + value.getName()).orElse(PREFIX);
    }

}
