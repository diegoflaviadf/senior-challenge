package br.com.senior.challenge.resources.rules;

import br.com.senior.challenge.entities.Item;
import br.com.senior.challenge.entities.SalesOrder;
import br.com.senior.challenge.entities.SalesOrderItem;
import br.com.senior.challenge.entities.enums.ItemType;
import br.com.senior.challenge.entities.enums.OrderStatus;
import br.com.senior.challenge.exceptions.BusinessException;
import br.com.senior.challenge.exceptions.NotFoundException;
import br.com.senior.challenge.resources.assembler.SalesOrderResourceAssembler;
import br.com.senior.challenge.resources.repositories.SalesOrderRepository;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static br.com.senior.challenge.entities.enums.OrderStatus.OPENNED;
import static br.com.senior.challenge.entities.enums.OrderStatus.isValid;

/**
 * Serviço para tratativa de regras de negócio relacionados ao Pedido
 */
@Component
public class SalesOrderRules {

    private final SalesOrderRepository repository;
    private final SalesOrderResourceAssembler assembler;
    private final PagedResourcesAssembler<SalesOrder> pagedAssembler;

    public SalesOrderRules(SalesOrderRepository repository,
                           SalesOrderResourceAssembler assembler,
                           PagedResourcesAssembler<SalesOrder> pagedAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    /**
     * Retorna uma lista paginada {@link PagedModel} de uma representação {@link EntityModel} de um {@link SalesOrder}
     *
     * @param predicate Query adicional
     * @param pageable Paginação
     * @return {@link PagedModel} de uma representação {@link EntityModel} de um {@link SalesOrder}
     */
    public PagedModel<EntityModel<SalesOrder>> findAll(Predicate predicate, Pageable pageable) {
        return pagedAssembler.toModel(repository.findAll(predicate, pageable));
    }

    /**
     * Retorna uma representação de um {@link SalesOrder}. Caso não encontre será lançado um {@link NotFoundException}
     *
     * @param id ID de um {@link SalesOrder}
     * @return Representação {@link EntityModel} de um {@link SalesOrder}
     */
    public EntityModel<SalesOrder> findById(UUID id) {
        SalesOrder order = this.repository.findById(id).orElseThrow(() -> new NotFoundException(id));
        return assembler.toModel(order);
    }

    /**
     * Cria e salva um {@link SalesOrder}
     *
     * @param order {@link SalesOrder} para salvar
     * @return Representação {@link EntityModel} de um {@link SalesOrder}
     */
    public EntityModel<SalesOrder> createAndSave(SalesOrder order) {
        order.setStatus(OPENNED);
        return assembler.toModel(recalculateAndSave(order));
    }

    /**
     * Atualiza e salva um {@link SalesOrder}.
     * Caso não encontre será lançado um {@link NotFoundException}
     * Caso o pedido não esteja OPENNED será lançado {@link BusinessException}.
     *
     * @param id ID de um {@link SalesOrder}
     * @param orderToUpdate {@link SalesOrder} para salvar
     * @return Representação {@link EntityModel} de um {@link SalesOrder}
     */
    public EntityModel<SalesOrder> updateAndSave(UUID id, SalesOrder orderToUpdate) {
        SalesOrder salesOrder = this.repository.findById(id)
                .map(record -> {
                    orderToUpdate.setId(id);
                    orderToUpdate.setStatus(record.getStatus());
                    if (!isDiscountAllowed(record)) {
                        orderToUpdate.setDiscount(record.getDiscount());
                    }
                    return record;
                }).orElseThrow(() -> new NotFoundException(id));
        validateOpenned(salesOrder);
        return assembler.toModel(recalculateAndSave(orderToUpdate));
    }

    /**
     * Deleta um {@link SalesOrder}. Caso não encontre será lançado um {@link NotFoundException}
     *
     * @param id ID de um {@link SalesOrder}
     * @return Representação {@link EntityModel} de um {@link SalesOrder}
     */
    public EntityModel<SalesOrder> delete(UUID id) {
        SalesOrder order = this.repository.findById(id).orElseThrow(() -> new NotFoundException(id));
        repository.delete(order);
        return assembler.toModel(order);
    }

    /**
     * Altera o status de um Pedido para FULLFILLED um {@link OrderStatus}.
     * Caso não encontre um {@link SalesOrder} será lançado um {@link NotFoundException}
     * Caso não seja possível será lançado um {@link BusinessException)
     *
     * @param id ID de um {@link OrderStatus}
     * @return Representação {@link EntityModel} de um {@link OrderStatus}
     */
    public EntityModel<SalesOrder> fulfill(@PathVariable UUID id) {
        return changeStatus(id, OrderStatus.FULLFILLED);
    }

    /**
     * Altera o status de um Pedido para CANCEL um {@link OrderStatus}.
     * Caso não encontre um {@link SalesOrder} será lançado um {@link NotFoundException}
     * Caso não seja possível será lançado um {@link BusinessException)
     *
     * @param id ID de um {@link OrderStatus}
     * @return Representação {@link EntityModel} de um {@link OrderStatus}
     */
    public EntityModel<SalesOrder> cancel(@PathVariable UUID id) {
        return changeStatus(id, OrderStatus.CANCELED);
    }

    /**
     * Altera o status de um {@link SalesOrder}, conforme flag. Caso não seja possível será lançado um {@link BusinessException)
     *
     * @param id ID de um {@link Item}
     * @param flag {@link Boolean}
     * @return Representação {@link EntityModel} de um {@link Item}
     */
    private EntityModel<SalesOrder> changeStatus(@PathVariable UUID id, OrderStatus status) {
        SalesOrder order = this.repository.findById(id).orElseThrow(() -> new NotFoundException(id));
        if (isValid(order.getStatus(), status)) {
            order.setStatus(status);
            return assembler.toModel(repository.save(order));
        }
        throw new BusinessException("state.transitioning.notValid", order.getStatus(),  status);
    }

    /**
     * Valida se o pedido {@link SalesOrder} está com status {@link OrderStatus} OPENNED.
     * Caso o pedido não esteja OPENNED será lançado {@link BusinessException}
     *
     * @param order {@link SalesOrder} para verificar status
     */
    public void validateOpenned(SalesOrder order){
        if (!order.getStatus().equals(OrderStatus.OPENNED)){
            throw new BusinessException("salesOrder.status.only.openned");
        }
    }

    /**
     * Recalcula e salva e um pedido {@link SalesOrder}
     *
     * @param salesOrder {@link SalesOrder}
     * @return {@link SalesOrder}
     */
    public SalesOrder recalculateAndSave(SalesOrder salesOrder) {
        return repository.save(recalculate(salesOrder));
    }

    /**
     * Recalcula e um pedido {@link SalesOrder}
     *
     * @param salesOrder {@link SalesOrder}
     * @return {@link SalesOrder}
     */
    public SalesOrder recalculate(SalesOrder salesOrder) {
        if (salesOrder.getId() != null) repository.findById(salesOrder.getId()).ifPresent(order -> salesOrder.setItens(order.getItens()));
        updateTotal(salesOrder);
        applyDiscount(salesOrder);
        return salesOrder;
    }

    /**
     * Atualiza o valor total de um {@link SalesOrder}
     *
     * @param order {@link SalesOrder}
     */
    private void updateTotal(SalesOrder order) {
        final Set<SalesOrderItem> itens = order.getItens();
        BigDecimal itensAmount = BigDecimal.ZERO;
        if (!itens.isEmpty()) {
            itensAmount = itens.stream().map(SalesOrderItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        order.setTotal(itensAmount);
    }

    /**
     * Aplica um desconto no pedido {@link SalesOrder}
     *
     * @param order {@link SalesOrder}
     */
    private void applyDiscount(SalesOrder order) {
        if (!isDiscountAllowed(order) || order.getDiscount() == null) {
            return;
        }
        final Set<SalesOrderItem> products = order.getItensByType(ItemType.PRODUCT);
        if (!products.isEmpty()) {
            final BigDecimal productsAmount = products.stream().map(SalesOrderItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal amountToDiscount = productsAmount.multiply(order.getDiscount()).scaleByPowerOfTen(-2);
            order.setTotal(order.getTotal().subtract(amountToDiscount));
        }
    }

    private boolean isDiscountAllowed(SalesOrder order) {
        return order.getStatus() != null && order.getStatus().equals(OPENNED);
    }
}
