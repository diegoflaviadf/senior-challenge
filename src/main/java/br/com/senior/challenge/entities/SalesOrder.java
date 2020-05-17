package br.com.senior.challenge.entities;

import br.com.senior.challenge.entities.enums.ItemType;
import br.com.senior.challenge.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.senior.challenge.controllers.handlers.ErrorConstraints.FIELD_NOT_NULL;

@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties("itens")
@Entity
@Table(name = "SALES_ORDER",
        uniqueConstraints = {
                @UniqueConstraint(name = EntityConstraints.UK_SALES_ORDER_REF, columnNames = {SalesOrder.FIELD_DS_REF})
        },
        indexes = {
                @Index(name = "IDX_SALES_ORDER_REF", columnList = "DS_REF")
        }
)
public class SalesOrder extends AbstractBaseEntity {

    public static final String FIELD_DS_REF = "DS_REF";

    @NotNull(message = FIELD_NOT_NULL)
    @ToString.Include
    @Column(name = FIELD_DS_REF, length = 64, nullable = false)
    private String reference;

    @Column(name = "DS_COMMENTS", length = 256)
    private String comments;

    @Column(name = "CD_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Digits(integer = 5, fraction = 2, message = "field.digits")
    @Column(name = "VL_DISCOUNT", precision = 5, scale = 2)
    private BigDecimal discount;

    @Column(name = "VL_TOTAL", precision = 12, scale = 4)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal total;

    @OneToMany(mappedBy = "order")
    @JsonIgnore
    private Set<SalesOrderItem> itens = new HashSet<>();

    /**
     * Retorna uma lista de itens do pedido conforme tipo
     *
     * @param type Tipo do item {@link ItemType}
     * @return {@link Set<SalesOrderItem>}
     */
    public Set<SalesOrderItem> getItensByType(ItemType type) {
        return getItens().stream().filter(item -> item.getItem().getType().equals(type))
                .collect(Collectors.toSet());
    }

}
