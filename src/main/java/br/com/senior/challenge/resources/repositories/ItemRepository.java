package br.com.senior.challenge.resources.repositories;

import br.com.senior.challenge.entities.Item;
import br.com.senior.challenge.entities.QItem;
import br.com.senior.challenge.resources.repositories.projections.ItemProjection;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "itens", path = "item", excerptProjection = ItemProjection.class, exported = false)
public interface ItemRepository extends
        CrudRepository<Item, UUID>,
        PagingAndSortingRepository<Item, UUID>,
        QuerydslPredicateExecutor<Item>,
        QuerydslBinderCustomizer<QItem> {

    @Override
    default void customize(QuerydslBindings bindings, QItem item) {
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }

}
