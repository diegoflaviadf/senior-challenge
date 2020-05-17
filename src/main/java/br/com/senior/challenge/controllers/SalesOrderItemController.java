package br.com.senior.challenge.controllers;

import br.com.senior.challenge.entities.SalesOrderItem;
import br.com.senior.challenge.resources.repositories.SalesOrderItemRepository;
import br.com.senior.challenge.resources.rules.SalesOrderItemRules;
import com.querydsl.core.types.Predicate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Api(tags = "Itens do Pedido")
@RestController
@RequestMapping
public class SalesOrderItemController {

    private final SalesOrderItemRules rules;

    public SalesOrderItemController(SalesOrderItemRules rules) {
        this.rules = rules;
    }

    @ApiOperation("Lista todos os produtos e serviços cadastrados para um pedido")
    @GetMapping("/sales-order/{id}/item")
    public ResponseEntity<PagedModel<EntityModel<SalesOrderItem>>> findByOrderId(@PathVariable UUID id, @QuerydslPredicate(root = SalesOrderItem.class, bindings = SalesOrderItemRepository.class) Predicate predicate, Pageable pageable) {
        return ResponseEntity.ok(rules.findByOrderId(id, predicate, pageable));
    }

    @ApiOperation("Retorna um produto ou serviço cadastrado para um pedido")
    @GetMapping("/sales-order/{order_id}/item/{id}")
    public ResponseEntity<EntityModel<SalesOrderItem>> findByOrderIdAndId(@PathVariable("order_id") UUID orderId, @PathVariable UUID id) {
        return ResponseEntity.ok(rules.findByOrderIdAndId(orderId, id));
    }

    @ApiOperation("Cadastra um produto ou serviço para um pedido")
    @PostMapping("/sales-order/{id}/item")
    public ResponseEntity<EntityModel<SalesOrderItem>> create(@PathVariable UUID id, @Valid @RequestBody SalesOrderItem orderItem) {
        return ResponseEntity.ok(rules.createAndSave(id, orderItem));
    }

    @ApiOperation("Atualiza um produto ou serviço para um pedido")
    @PutMapping("/sales-order/{order_id}/item/{id}")
    public ResponseEntity<EntityModel<SalesOrderItem>> update(@PathVariable("order_id") UUID orderId, @PathVariable UUID id, @Valid @RequestBody SalesOrderItem orderItemToUpdate) {
        return ResponseEntity.ok(rules.updateAndSave(orderId, id, orderItemToUpdate));
    }

    @ApiOperation("Exclui um produto ou serviço de um pedido")
    @DeleteMapping("/sales-order/{order_id}/item/{id}")
    public ResponseEntity<EntityModel<SalesOrderItem>> delete(@PathVariable("order_id") UUID orderId, @PathVariable UUID id) {
        return ResponseEntity.ok(rules.delete(orderId, id));
    }

}
