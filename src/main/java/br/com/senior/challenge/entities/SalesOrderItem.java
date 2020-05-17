package br.com.senior.challenge.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static br.com.senior.challenge.controllers.handlers.ErrorConstraints.FIELD_DIGITS;
import static br.com.senior.challenge.controllers.handlers.ErrorConstraints.FIELD_NOT_NULL;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"order", "type"})
@Entity
@Table(name = "SALES_ORDER_ITEM")
public class SalesOrderItem extends AbstractBaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_SALES_ORDER", foreignKey = @ForeignKey(name = EntityConstraints.FK_SALES_ORDER_SALES_ORDER_ITEM), nullable = false)
    private SalesOrder order;

    @NotNull(message = FIELD_NOT_NULL)
    @JsonIgnoreProperties({"salesOrderItem", "price"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ITEM", foreignKey = @ForeignKey(name = EntityConstraints.FK_SALES_ORDER_ITEM), nullable = false)
    private Item item;

    @NotNull(message = FIELD_NOT_NULL)
    @Column(name = "QT_ITEM", precision = 8, scale = 4, nullable = false)
    private BigDecimal quantity;

    @NotNull(message = FIELD_NOT_NULL)
    @Digits(integer = 8, fraction = 4, message = FIELD_DIGITS)
    @Column(name = "VL_PRICE", precision = 8, scale = 4, nullable = false)
    private BigDecimal price;

    /**
     * Retorna o total do item do pedido
     *
     * @return Total do item
     */
    public BigDecimal getTotalAmount() {
        BigDecimal unityValue = getPrice();
        if (unityValue == null) {
            return BigDecimal.ZERO;
        }
        return getQuantity().multiply(unityValue).setScale(4, RoundingMode.UP);
    }

}
