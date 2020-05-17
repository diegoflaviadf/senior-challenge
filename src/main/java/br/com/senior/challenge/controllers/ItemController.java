package br.com.senior.challenge.controllers;

import br.com.senior.challenge.entities.Item;
import br.com.senior.challenge.resources.repositories.ItemRepository;
import br.com.senior.challenge.resources.rules.ItemRules;
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

@Api(tags = "Produtos e Serviços")
@RestController
@RequestMapping
public class ItemController {

    private final ItemRules rules;

    public ItemController(ItemRules rules) {
        this.rules = rules;
    }

    @ApiOperation("Lista todos os produtos e serviços cadastrados")
    @GetMapping("/item")
    public ResponseEntity<PagedModel<EntityModel<Item>>> findAll(@QuerydslPredicate(root = Item.class, bindings = ItemRepository.class) Predicate predicate, Pageable pageable) {
        return ResponseEntity.ok(rules.findAll(predicate, pageable));
    }

    @ApiOperation("Retorna um produto ou serviço")
    @GetMapping("/item/{id}")
    public ResponseEntity<EntityModel<Item>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(rules.findById(id));
    }

    @ApiOperation("Cadastra um produto ou serviço")
    @PostMapping("/item")
    public ResponseEntity<EntityModel<Item>> create(@Valid @RequestBody Item item) {
        return ResponseEntity.ok(rules.createAndSave(item));
    }

    @ApiOperation("Atualiza um produto ou serviço")
    @PutMapping("/item/{id}")
    public ResponseEntity<EntityModel<Item>> update(@PathVariable UUID id, @Valid @RequestBody Item itemToUpdate) {
        return ResponseEntity.ok(rules.updateAndSave(id, itemToUpdate));
    }

    @ApiOperation("Exclui um produto ou serviço")
    @DeleteMapping("/item/{id}")
    public ResponseEntity<EntityModel<Item>> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(rules.delete(id));
    }

    @ApiOperation("Desativa um produto ou serviço")
    @PostMapping("/item/{id}/deactivate")
    public ResponseEntity<EntityModel<Item>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(rules.deactivateAndSave(id));
    }

    @ApiOperation("Ativa um produto ou serviço")
    @PostMapping("/item/{id}/activate")
    public ResponseEntity<EntityModel<Item>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(rules.activateAndSave(id));
    }

}
