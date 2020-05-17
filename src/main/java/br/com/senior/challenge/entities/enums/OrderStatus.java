package br.com.senior.challenge.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Enum para identificar o status de um pedido
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderStatus {

    OPENNED,
    FULLFILLED,
    CANCELED;

    public static boolean isValid(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OPENNED) {
            return newStatus == FULLFILLED || newStatus == CANCELED;
        } else if (currentStatus == FULLFILLED) {
            return false;
        } else if (currentStatus == CANCELED) {
            return false;
        } else {
            throw new RuntimeException("Unrecognized situation.");
        }
    }


}
