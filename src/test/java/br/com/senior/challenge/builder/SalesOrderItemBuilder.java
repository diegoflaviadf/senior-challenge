package br.com.senior.challenge.builder;

import br.com.senior.challenge.entities.Item;
import br.com.senior.challenge.entities.SalesOrder;
import br.com.senior.challenge.entities.SalesOrderItem;
import br.com.senior.challenge.entities.enums.OrderStatus;

import java.math.BigDecimal;

public class SalesOrderItemBuilder {

    private final SalesOrderItem salesOrderItem;

    private SalesOrderItemBuilder(SalesOrderItem salesOrderItem) {
        this.salesOrderItem = salesOrderItem;
    }

    public static SalesOrderItemBuilder newSalesOrder() {
        return new SalesOrderItemBuilder(new SalesOrderItem());
    }

    public SalesOrderItemBuilder withSalesOrder(SalesOrder value) {
        salesOrderItem.setOrder(value);
        return this;
    }

    public SalesOrderItemBuilder withItem(Item value) {
        salesOrderItem.setItem(value);
        return this;
    }

    public SalesOrderItemBuilder withQuantity(BigDecimal value) {
        salesOrderItem.setQuantity(value);
        return this;
    }

    public SalesOrderItemBuilder withPrice(BigDecimal value) {
        salesOrderItem.setPrice(value);
        return this;
    }

    public SalesOrderItem build() {
        return salesOrderItem;
    }

}
