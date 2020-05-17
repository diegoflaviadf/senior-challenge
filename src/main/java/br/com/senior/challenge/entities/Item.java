package br.com.senior.challenge.entities;

import br.com.senior.challenge.entities.enums.ItemType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

import static br.com.senior.challenge.controllers.handlers.ErrorConstraints.FIELD_NOT_NULL;
import static br.com.senior.challenge.controllers.handlers.ErrorConstraints.FIELD_SIZE;

@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties("salesOrderItems")
@Entity
@Table(name = "ITEM",
        uniqueConstraints = {
                @UniqueConstraint(name = EntityConstraints.UK_ITEM_NAME, columnNames = {Item.DS_NAME})
        },
        indexes = {
                @Index(name = "IDX_ITEM_NAME", columnList = Item.DS_NAME)
        }

)
public class Item extends AbstractBaseEntity {

    public static final String DS_NAME = "DS_NAME";

    @NotNull(message = FIELD_NOT_NULL)
    @Size(min = 10, max = 128, message = "field.size")
    @ToString.Include
    @Column(name = DS_NAME, length = 128, nullable = false)
    private String name;

    @NotNull(message = FIELD_NOT_NULL)
    @ToString.Include
    @Column(name = "CD_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @NotBlank(message = FIELD_NOT_NULL)
    @Size(min = 8, max = 256, message = FIELD_SIZE)
    @Column(name = "DS_DESCRIPTION", length = 256, nullable = false)
    private String description;

    @Column(name = "FG_ACTIVE", nullable = false)
    private Boolean active;

    @JsonIgnoreProperties({"order", "item"})
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<SalesOrderItem> salesOrderItems;

}
