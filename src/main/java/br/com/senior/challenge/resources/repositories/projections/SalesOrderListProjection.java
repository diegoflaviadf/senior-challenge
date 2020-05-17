package br.com.senior.challenge.resources.repositories.projections;

import br.com.senior.challenge.entities.SalesOrder;
import br.com.senior.challenge.entities.enums.OrderStatus;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;

@Projection(
        name = "salesOrderListProjection",
        types = {SalesOrder.class})
public interface SalesOrderListProjection {

    String getReference();

    String getComments();

    OrderStatus getStatus();

    BigDecimal getDiscount();

    BigDecimal getTotal();
}
