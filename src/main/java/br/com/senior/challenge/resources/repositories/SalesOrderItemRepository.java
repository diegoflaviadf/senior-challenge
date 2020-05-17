package br.com.senior.challenge.resources.repositories;

import br.com.senior.challenge.entities.QSalesOrderItem;
import br.com.senior.challenge.entities.SalesOrderItem;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "sales-order-itens", path = "sales-order-itens", exported = false)
public interface SalesOrderItemRepository extends
        CrudRepository<SalesOrderItem, UUID>,
        PagingAndSortingRepository<SalesOrderItem, UUID>,
        QuerydslPredicateExecutor<SalesOrderItem>,
        QuerydslBinderCustomizer<QSalesOrderItem> {

    Page<SalesOrderItem> findByOrderId(Predicate predicate, Pageable pageable);

    SalesOrderItem findByOrderIdAndId(UUID orderId, UUID id);

    @Override
    default void customize(QuerydslBindings bindings, QSalesOrderItem orderItem) {
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }

}
