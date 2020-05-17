package br.com.senior.challenge.resources.processors;

import br.com.senior.challenge.controllers.SalesOrderController;
import br.com.senior.challenge.controllers.SalesOrderItemController;
import br.com.senior.challenge.entities.SalesOrder;
import br.com.senior.challenge.entities.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import static br.com.senior.challenge.entities.enums.OrderStatus.isValid;
import static br.com.senior.challenge.controllers.utils.ControllerUtils.applyBasePath;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Adiciona links na representação de um pedido
 */
@Component
public class SalesOrderProcessor implements RepresentationModelProcessor<EntityModel<SalesOrder>> {

    private final RepositoryRestConfiguration configuration;

    public SalesOrderProcessor(final RepositoryRestConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public EntityModel<SalesOrder> process(EntityModel<SalesOrder> model) {
        SalesOrderController controller = methodOn(SalesOrderController.class);
        String basePath = configuration.getBasePath().toString();

        final SalesOrder content = model.getContent();

        if (content != null) {
            model.add(applyBasePath(
                    linkTo(controller.findById(content.getId())).withSelfRel(),
                    basePath));

            model.add(applyBasePath(
                    linkTo(methodOn(SalesOrderItemController.class).findByOrderId(content.getId(), null, Pageable.unpaged())).withRel("itens"),
                    basePath));

            if (isValid(content.getStatus(), OrderStatus.FULLFILLED)) {
                model.add(applyBasePath( //
                        linkTo(controller.fulfill(content.getId()))
                                .withRel("fullfill"),
                        basePath));
            }

            if (isValid(content.getStatus(), OrderStatus.CANCELED)) {
                model.add(applyBasePath( //
                        linkTo(controller.cancel(content.getId()))
                                .withRel("cancel"),
                        basePath));
            }
        }

        return model;
    }
}
