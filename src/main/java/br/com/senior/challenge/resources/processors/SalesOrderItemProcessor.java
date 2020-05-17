package br.com.senior.challenge.resources.processors;

import br.com.senior.challenge.controllers.ItemController;
import br.com.senior.challenge.controllers.SalesOrderController;
import br.com.senior.challenge.controllers.SalesOrderItemController;
import br.com.senior.challenge.entities.SalesOrder;
import br.com.senior.challenge.entities.SalesOrderItem;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import static br.com.senior.challenge.controllers.utils.ControllerUtils.applyBasePath;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Adiciona links na representação de um item do pedido
 */
@Component
public class SalesOrderItemProcessor implements RepresentationModelProcessor<EntityModel<SalesOrderItem>> {

    private final RepositoryRestConfiguration configuration;

    public SalesOrderItemProcessor(RepositoryRestConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public EntityModel<SalesOrderItem> process(EntityModel<SalesOrderItem> model) {
        final SalesOrderItem content = model.getContent();
        String basePath = configuration.getBasePath().toString();

        if (content != null) {
            final SalesOrder order = content.getOrder();
            model.add(applyBasePath(
                    linkTo(methodOn(SalesOrderItemController.class).findByOrderIdAndId(order.getId(), content.getId())).withSelfRel(),
                    basePath));

            model.add(applyBasePath(
                    linkTo(methodOn(ItemController.class).findById(content.getItem().getId())).withRel("item"),
                    basePath));

            model.add(applyBasePath(
                    linkTo(methodOn(SalesOrderController.class).findById(order.getId())).withRel("sales-order"),
                    basePath));
        }

        return model;
    }
}
