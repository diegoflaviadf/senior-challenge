package br.com.senior.challenge.resources.repositories.projections;

import br.com.senior.challenge.entities.Item;
import br.com.senior.challenge.entities.enums.ItemType;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;

@Projection(
        name = "itemProjection",
        types = {Item.class})
public interface ItemProjection {

    String getName();

    ItemType getType();

    String getDescription();

    BigDecimal getPrice();

    Boolean getActive();
}
