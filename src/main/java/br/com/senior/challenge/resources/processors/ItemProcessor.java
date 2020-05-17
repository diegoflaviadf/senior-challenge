package br.com.senior.challenge.resources.processors;

import br.com.senior.challenge.controllers.ItemController;
import br.com.senior.challenge.entities.Item;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import static br.com.senior.challenge.controllers.utils.ControllerUtils.applyBasePath;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Adiciona links na representação de um item
 */
@Component
public class ItemProcessor implements RepresentationModelProcessor<EntityModel<Item>> {

    private final RepositoryRestConfiguration configuration;

    public ItemProcessor(RepositoryRestConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public EntityModel<Item> process(EntityModel<Item> model) {
        ItemController controller = methodOn(ItemController.class);
        String basePath = configuration.getBasePath().toString();

        final Item content = model.getContent();

        if (content != null) {
            model.add(applyBasePath(
                    linkTo(controller.findById(content.getId())).withSelfRel(),
                    basePath));

            if (content.getActive().equals(Boolean.TRUE)){
                model.add(applyBasePath( //
                        linkTo(controller.deactivate(content.getId()))
                                .withRel("deactivate"),
                        basePath));
            }

            if (content.getActive().equals(Boolean.FALSE)) {
                model.add(applyBasePath( //
                        linkTo(controller.activate(content.getId()))
                                .withRel("activate"),
                        basePath));
            }
        }

        return model;
    }
}
