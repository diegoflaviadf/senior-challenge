package br.com.senior.challenge.resources.repositories.projections;

import br.com.senior.challenge.entities.SalesOrder;
import br.com.senior.challenge.entities.SalesOrderItem;
import br.com.senior.challenge.entities.enums.OrderStatus;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;
import java.util.Set;

@Projection(
        name = "salesOrderProjection",
        types = {SalesOrder.class})
public interface SalesOrderProjection {

    String getReference();

    String getComments();

    OrderStatus getStatus();

    BigDecimal getDiscount();

    BigDecimal getTotal();

    Set<SalesOrderItem> getItens();
}
