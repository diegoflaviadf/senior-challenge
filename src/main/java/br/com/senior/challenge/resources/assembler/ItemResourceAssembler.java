package br.com.senior.challenge.resources.assembler;

import br.com.senior.challenge.controllers.ItemController;
import br.com.senior.challenge.entities.Item;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class ItemResourceAssembler extends SimpleIdentifiableRepresentationModelAssembler<Item> {

    ItemResourceAssembler() {
        super(ItemController.class);
    }

    @Override
    public void addLinks(EntityModel<Item> resource) {
        //Noop
    }
}
