package br.com.senior.challenge.resources.rules;

import br.com.senior.challenge.entities.Item;
import br.com.senior.challenge.exceptions.BusinessException;
import br.com.senior.challenge.exceptions.NotFoundException;
import br.com.senior.challenge.resources.assembler.ItemResourceAssembler;
import br.com.senior.challenge.resources.repositories.ItemRepository;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Serviço para tratativa de regras de negócio relacionados ao Item
 */
@Service
public class ItemRules {

    private final ItemRepository repository;
    private final ItemResourceAssembler assembler;
    private final PagedResourcesAssembler<Item> pagedAssembler;

    public ItemRules(ItemRepository repository, ItemResourceAssembler assembler, PagedResourcesAssembler<Item> pagedAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    /**
     * Retorna uma lista paginada {@link PagedModel} de uma representação {@link EntityModel} de {@link Item}
     *
     * @param predicate Query adicional
     * @param pageable  Paginação
     * @return {@link PagedModel} de uma representação {@link EntityModel} de um {@link Item}
     */
    public PagedModel<EntityModel<Item>> findAll(Predicate predicate, Pageable pageable) {
        return pagedAssembler.toModel(repository.findAll(predicate, pageable));
    }

    /**
     * Retorna uma representação {@link EntityModel} de um {@link Item}. Caso não encontre será lançado um {@link NotFoundException}
     *
     * @param id ID de um {@link Item}
     * @return {@link EntityModel} de um {@link Item}
     */
    public EntityModel<Item> findById(UUID id) {
        Item item = repository.findById(id).orElseThrow(() -> new NotFoundException(id));
        return assembler.toModel(item);
    }

    /**
     * Cria e salva um {@link Item}. Caso não encontre será lançado um {@link NotFoundException}
     *
     * @param item {@link Item} para salvar
     * @return Representação {@link EntityModel} de um {@link Item}
     */
    public EntityModel<Item> createAndSave(Item item) {
        item.setActive(true);
        return assembler.toModel(repository.save(item));
    }

    /**
     * Atualiza e salva um {@link Item}. Caso não encontre será lançado um {@link NotFoundException}
     *
     * @param id           ID de um {@link Item}
     * @param itemToUpdate {@link Item} para salvar
     * @return Representação {@link EntityModel} de um {@link Item}
     */
    public EntityModel<Item> updateAndSave(UUID id, Item itemToUpdate) {
        repository.findById(id)
                .map(record -> {
                    itemToUpdate.setId(id);
                    itemToUpdate.setActive(record.getActive());
                    return record;
                }).orElseThrow(() -> new NotFoundException(id));
        return assembler.toModel(repository.save(itemToUpdate));
    }

    /**
     * Deleta um {@link Item}
     *
     * @param id ID de um {@link Item}
     * @return Representação {@link EntityModel} de um {@link Item}
     */
    public EntityModel<Item> delete(UUID id) {
        Item item = this.repository.findById(id).orElseThrow(() -> new NotFoundException(id));
        repository.delete(item);
        return assembler.toModel(item);
    }

    /**
     * Desativa um {@link Item}}. Caso não seja possível será lançado um {@link BusinessException)
     *
     * @param id ID de um {@link Item}
     * @return Representação {@link EntityModel} de um {@link Item}
     */
    public EntityModel<Item> deactivateAndSave(UUID id) {
        return changeActiveAndSave(id, false);
    }

    /**
     * Ativa um {@link Item}}. Caso não seja possível será lançado um {@link BusinessException)
     *
     * @param id ID de um {@link Item}
     * @return Representação {@link EntityModel} de um {@link Item}
     */
    public EntityModel<Item> activateAndSave(UUID id) {
        return changeActiveAndSave(id, true);
    }

    /**
     * Altera o status de um {@link Item}, conforme flag. Caso não seja possível será lançado um {@link BusinessException)
     *
     * @param id   ID de um {@link Item}
     * @param flag {@link Boolean}
     * @return Representação {@link EntityModel} de um {@link Item}
     */
    private EntityModel<Item> changeActiveAndSave(UUID id, Boolean flag) {
        Item item = this.repository.findById(id).orElseThrow(() -> new NotFoundException(id));
        if (!item.getActive().equals(flag)) {
            item.setActive(flag);
            return assembler.toModel(repository.save(item));
        }
        throw new BusinessException("state.transitioning.notValid", item.getActive(), flag);
    }
}
