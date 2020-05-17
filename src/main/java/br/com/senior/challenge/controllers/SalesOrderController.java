package br.com.senior.challenge.controllers;

import br.com.senior.challenge.entities.SalesOrder;
import br.com.senior.challenge.resources.repositories.SalesOrderRepository;
import br.com.senior.challenge.resources.rules.SalesOrderRules;
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

@Api(tags = "Pedidos")
@RestController
@RequestMapping
public class SalesOrderController {

    private final SalesOrderRules rules;

    public SalesOrderController(SalesOrderRules rules) {
        this.rules = rules;
    }

    @ApiOperation("Lista todos os pedidos cadastrados")
    @GetMapping("/sales-order")
    public ResponseEntity<PagedModel<EntityModel<SalesOrder>>> findAll(@QuerydslPredicate(root = SalesOrder.class, bindings = SalesOrderRepository.class) Predicate predicate, Pageable pageable) {
        return ResponseEntity.ok(rules.findAll(predicate, pageable));
    }

    @ApiOperation("Retorna um pedido")
    @GetMapping("/sales-order/{id}")
    public ResponseEntity<EntityModel<SalesOrder>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(rules.findById(id));
    }

    @ApiOperation("Cadastra um pedido")
    @PostMapping("/sales-order")
    public ResponseEntity<EntityModel<SalesOrder>> create(@Valid @RequestBody SalesOrder order) {
        return ResponseEntity.ok(rules.createAndSave(order));
    }

    @ApiOperation("Atualiza um pedido")
    @PutMapping("/sales-order/{id}")
    public ResponseEntity<EntityModel<SalesOrder>> update(@PathVariable UUID id, @Valid @RequestBody SalesOrder orderToUpdate) {
        return ResponseEntity.ok(rules.updateAndSave(id, orderToUpdate));
    }

    @ApiOperation("Exclui um pedido")
    @DeleteMapping("/sales-order/{id}")
    public ResponseEntity<EntityModel<SalesOrder>> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(rules.delete(id));
    }

    @ApiOperation("Altera o status de um pedido para completo")
    @PostMapping("/sales-order/{id}/fulfill")
    public ResponseEntity<EntityModel<SalesOrder>> fulfill(@PathVariable UUID id) {
        return ResponseEntity.ok(rules.fulfill(id));
    }

    @ApiOperation("Altera o status de um pedido para cancelado")
    @PostMapping("/sales-order/{id}/cancel")
    public ResponseEntity<EntityModel<SalesOrder>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(rules.cancel(id));
    }

}
