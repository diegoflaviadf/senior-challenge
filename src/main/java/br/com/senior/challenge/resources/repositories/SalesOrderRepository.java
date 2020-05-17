package br.com.senior.challenge.resources.repositories;

import br.com.senior.challenge.entities.QSalesOrder;
import br.com.senior.challenge.entities.SalesOrder;
import br.com.senior.challenge.resources.repositories.projections.SalesOrderListProjection;
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

@RepositoryRestResource(collectionResourceRel = "sales-order", path = "sales-order", excerptProjection = SalesOrderListProjection.class, exported = false)
public interface SalesOrderRepository extends
        CrudRepository<SalesOrder, UUID>,
        PagingAndSortingRepository<SalesOrder, UUID>,
        QuerydslPredicateExecutor<SalesOrder>,
        QuerydslBinderCustomizer<QSalesOrder> {

    @Override
    default void customize(QuerydslBindings bindings, QSalesOrder order) {
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }

}
