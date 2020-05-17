package br.com.senior.challenge.resources.assembler;

import br.com.senior.challenge.controllers.SalesOrderController;
import br.com.senior.challenge.entities.SalesOrder;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class SalesOrderResourceAssembler extends SimpleIdentifiableRepresentationModelAssembler<SalesOrder> {

    SalesOrderResourceAssembler() {
        super(SalesOrderController.class);
    }

    @Override
    public void addLinks(EntityModel<SalesOrder> resource) {
        // Noop
    }

}
