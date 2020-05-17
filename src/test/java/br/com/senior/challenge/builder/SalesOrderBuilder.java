package br.com.senior.challenge.builder;

import br.com.senior.challenge.entities.SalesOrder;
import br.com.senior.challenge.entities.enums.OrderStatus;

import java.math.BigDecimal;

public class SalesOrderBuilder {

    private final SalesOrder salesOrder;

    private SalesOrderBuilder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }

    public static SalesOrderBuilder newSalesOrder() {
        return new SalesOrderBuilder(new SalesOrder());
    }

    public SalesOrderBuilder withReference(String value) {
        salesOrder.setReference(value);
        return this;
    }

    public SalesOrderBuilder withComments(String value) {
        salesOrder.setComments(value);
        return this;
    }

    public SalesOrderBuilder withStatus(OrderStatus value) {
        salesOrder.setStatus(value);
        return this;
    }

    public SalesOrderBuilder withDiscount(BigDecimal value) {
        salesOrder.setDiscount(value);
        return this;
    }

    public SalesOrder build() {
        return salesOrder;
    }

}
