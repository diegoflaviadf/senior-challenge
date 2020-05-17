package br.com.senior.challenge.resources.rules;

import br.com.senior.challenge.entities.Item;
import br.com.senior.challenge.entities.QSalesOrderItem;
import br.com.senior.challenge.entities.SalesOrder;
import br.com.senior.challenge.entities.SalesOrderItem;
import br.com.senior.challenge.exceptions.BusinessException;
import br.com.senior.challenge.exceptions.NotFoundException;
import br.com.senior.challenge.resources.assembler.SalesOrderItemResourceAssembler;
import br.com.senior.challenge.resources.repositories.SalesOrderItemRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Serviço para tratativa de regras de negócio relacionados ao Item do Pedido
 */
@Service
public class SalesOrderItemRules {

    private final SalesOrderItemRepository repository;
    private final SalesOrderItemResourceAssembler assembler;
    private final PagedResourcesAssembler<SalesOrderItem> pagedAssembler;
    private final SalesOrderRules orderRules;

    public SalesOrderItemRules(SalesOrderItemRepository repository, SalesOrderItemResourceAssembler assembler, PagedResourcesAssembler<SalesOrderItem> pagedAssembler, SalesOrderRules orderRules) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
        this.orderRules = orderRules;
    }

    /**
     * Retorna uma lista paginada {@link PagedModel} de uma representação {@link EntityModel} de {@link SalesOrderItem}
     *
     * @param id        ID do {@link SalesOrder}
     * @param predicate Query adicional
     * @param pageable  Paginação
     * @return Lista paginada
     */
    public PagedModel<EntityModel<SalesOrderItem>> findByOrderId(UUID id, Predicate predicate, Pageable pageable) {
        predicate = Expressions.asBoolean(QSalesOrderItem.salesOrderItem.order.id.eq(id)).and(predicate);
        return pagedAssembler.toModel(repository.findAll(predicate, pageable));
    }

    /**
     * Retorna uma representação {@link EntityModel} de {@link SalesOrderItem}.  Caso não encontre será lançado um {@link NotFoundException}
     *
     * @param orderId ID do {@link SalesOrder}
     * @param id      ID do {@link SalesOrderItem}
     * @return {@link EntityModel} de {@link SalesOrderItem}
     */
    public EntityModel<SalesOrderItem> findByOrderIdAndId(UUID orderId, UUID id) {
        Optional.ofNullable(repository.findByOrderIdAndId(orderId, id))
                .orElseThrow(() -> new NotFoundException(id));
        return assembler.toModel(repository.findByOrderIdAndId(orderId, id));
    }

    /**
     * Cria um {@link SalesOrderItem}.
     * Caso o pedido não esteja OPENNED será lançado {@link BusinessException}
     *
     * @param order     {@link SalesOrder} do {@link SalesOrderItem}
     * @param orderItem {@link SalesOrderItem} para salvar
     * @return Representação {@link EntityModel} de um {@link SalesOrderItem}
     */
    public SalesOrderItem create(SalesOrder order, SalesOrderItem orderItem) {
        orderRules.validateOpenned(order);
        orderItem.setOrder(order);
        orderRules.recalculate(order);
        return orderItem;
    }

    /**
     * Cria um {@link SalesOrder}.
     * Caso não encontre um {@link SalesOrder} será lançado um {@link NotFoundException}
     * Caso o item não esteja ativo será lançado {@link BusinessException}
     *
     * @param orderId   ID do {@link SalesOrder} do {@link SalesOrderItem}
     * @param orderItem {@link SalesOrderItem} para salvar
     * @return Representação {@link EntityModel} de um {@link SalesOrderItem}
     */
    public SalesOrderItem create(UUID orderId, SalesOrderItem orderItem) {
        SalesOrder order = orderRules.findById(orderId).getContent();
        validateActiveItem(orderItem.getItem());
        orderItem = create(order, orderItem);
        return orderItem;
    }

    /**
     * Cria e salva um {@link SalesOrderItem}
     *
     * @param orderId   ID de um {@link SalesOrder}
     * @param orderItem {@link SalesOrderItem} para salvar
     * @return Representação de um {@link SalesOrderItem}
     */
    public EntityModel<SalesOrderItem> createAndSave(UUID orderId, SalesOrderItem orderItem) {
        SalesOrderItem newOrderItem = create(orderId, orderItem);
        return assembler.toModel(repository.save(newOrderItem));
    }

    /**
     * Atualiza e um {@link SalesOrderItem}.
     * Caso o pedido não esteja OPENNED será lançado {@link BusinessException}.
     * Caso não encontre será lançado um {@link NotFoundException}
     *
     * @param orderId           ID de um {@link SalesOrder}
     * @param id                ID de um {@link SalesOrderItem}
     * @param orderItemToUpdate {@link SalesOrderItem} para salvar
     * @return Representação {@link EntityModel} de um {@link SalesOrderItem}
     */
    public SalesOrderItem update(UUID orderId, UUID id, SalesOrderItem orderItemToUpdate) {
        Optional.ofNullable(repository.findByOrderIdAndId(orderId, id))
                .map(record -> {
                    orderItemToUpdate.setId(id);
                    orderItemToUpdate.setOrder(record.getOrder());
                    return record;
                }).orElseThrow(() -> new NotFoundException(id));
        validateActiveItem(orderItemToUpdate.getItem());
        orderRules.validateOpenned(orderItemToUpdate.getOrder());
        orderRules.recalculate(orderItemToUpdate.getOrder());
        return orderItemToUpdate;
    }

    /**
     * Atualiza e salva um {@link SalesOrderItem}. Caso não encontre será lançado um {@link NotFoundException}
     *
     * @param orderId           ID de um {@link SalesOrder}
     * @param id                ID de um {@link SalesOrderItem}
     * @param orderItemToUpdate {@link SalesOrderItem} para salvar
     * @return Representação {@link EntityModel} de um {@link SalesOrderItem}
     */
    public EntityModel<SalesOrderItem> updateAndSave(UUID orderId, UUID id, SalesOrderItem orderItemToUpdate) {
        orderItemToUpdate = update(orderId, id, orderItemToUpdate);
        return assembler.toModel(repository.save(orderItemToUpdate));
    }

    /**
     * Deleta um {@link SalesOrder}.
     * Caso não encontre será lançado um {@link NotFoundException}.
     * Caso o pedido não esteja OPENNED será lançado {@link BusinessException}.
     *
     * @param orderId ID de um @link SalesOrder}
     * @param id      ID de um {@link SalesOrderItem}
     * @return Representação {@link EntityModel} de um {@link SalesOrderItem}
     */
    public EntityModel<SalesOrderItem> delete(UUID orderId, UUID id) {
        SalesOrderItem salesOrderItem = Optional.ofNullable(repository.findByOrderIdAndId(orderId, id))
                .orElseThrow(() -> new NotFoundException(id));
        orderRules.validateOpenned(salesOrderItem.getOrder());
        repository.delete(salesOrderItem);
        orderRules.recalculateAndSave(salesOrderItem.getOrder());
        return assembler.toModel(salesOrderItem);
    }

    /**
     * Valida se um item está ativo. Caso o item não esteja ativo será lançado {@link BusinessException}
     *
     * @param item {@link Item} para verificar se está ativo
     */
    private void validateActiveItem(Item item) {
        if (item.getActive().equals(Boolean.FALSE)) {
            throw new BusinessException("salesOrder.item.deactive");
        }
    }
}
