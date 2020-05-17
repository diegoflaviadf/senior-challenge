package br.com.senior.challenge.resources.assembler;

import br.com.senior.challenge.controllers.SalesOrderItemController;
import br.com.senior.challenge.entities.SalesOrderItem;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class SalesOrderItemResourceAssembler extends SimpleIdentifiableRepresentationModelAssembler<SalesOrderItem> {

    SalesOrderItemResourceAssembler() {
        super(SalesOrderItemController.class);
    }

    @Override
    public void addLinks(EntityModel<SalesOrderItem> resource) {
        // Noop
    }
}
