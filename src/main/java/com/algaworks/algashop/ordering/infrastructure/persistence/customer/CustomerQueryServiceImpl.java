package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.application.customer.query.CustomerFilter;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.application.customer.query.CustomerSummaryOutput;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final EntityManager entityManager;

    private static final String findByIdAsOutputJPQL = """
            SELECT new com.algaworks.algashop.ordering.application.customer.query.CustomerOutput(
                c.id,
                c.firstName,
                c.lastName,
                c.email,
                c.document,
                c.phone,
                c.birthDate,
                c.loyaltyPoints,
                c.registeredAt,
                c.archivedAt,
                c.promotionNotificationsAllowed,
                c.archived,
                new com.algaworks.algashop.ordering.application.commons.AddressData(
                    c.address.street,
                    c.address.number,
                    c.address.complement,
                    c.address.neighborhood,
                    c.address.city,
                    c.address.state,
                    c.address.zipCode
                )
            )
            FROM CustomerPersistenceEntity c
            WHERE c.id = :id""";

    @Override
    public CustomerOutput findById(UUID customerId) {
        try {
            TypedQuery<CustomerOutput> query = entityManager.createQuery(findByIdAsOutputJPQL, CustomerOutput.class);
            query.setParameter("id", customerId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw CustomerNotFoundException.because(customerId);
        }
    }

    @Override
    public Page<CustomerSummaryOutput> filter(CustomerFilter filter) {
        Long countTotalQueryResults = countTotalQueryResults(filter);

        if (countTotalQueryResults.equals(0L)) {
            var pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            return new PageImpl<>(new ArrayList<>(), pageRequest, countTotalQueryResults);
        }

        return filterQuery(filter, countTotalQueryResults);
    }

    private Long countTotalQueryResults(CustomerFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<CustomerPersistenceEntity> root = criteriaQuery.from(CustomerPersistenceEntity.class);

        Predicate[] predicates = toPredicates(builder, root, filter);

        criteriaQuery.select(builder.count(root));
        criteriaQuery.where(predicates);

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);

        return query.getSingleResult();
    }

    private Page<CustomerSummaryOutput> filterQuery(CustomerFilter filter, Long totalQueryResults) {
        var builder = entityManager.getCriteriaBuilder();
        var criteriaQuery = builder.createQuery(CustomerSummaryOutput.class);

        var rootCustomer = criteriaQuery.from(CustomerPersistenceEntity.class);

        criteriaQuery.select(
                builder.construct(CustomerSummaryOutput.class,
                        rootCustomer.get("id"),
                        rootCustomer.get("firstName"),
                        rootCustomer.get("lastName"),
                        rootCustomer.get("email"),
                        rootCustomer.get("document"),
                        rootCustomer.get("phone"),
                        rootCustomer.get("birthDate"),
                        rootCustomer.get("loyaltyPoints"),
                        rootCustomer.get("registeredAt"),
                        rootCustomer.get("archivedAt"),
                        rootCustomer.get("promotionNotificationsAllowed"),
                        rootCustomer.get("archived"))
        );

        Predicate[] predicates = toPredicates(builder, rootCustomer, filter);
        Order sortOrder = toSortOrder(builder, rootCustomer, filter);

        criteriaQuery.where(predicates);
        if (sortOrder != null) {
            criteriaQuery.orderBy(sortOrder);
        }

        TypedQuery<CustomerSummaryOutput> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(filter.getSize() * filter.getPage());
        typedQuery.setMaxResults(filter.getSize());

        var pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        return new PageImpl<>(typedQuery.getResultList(), pageRequest, totalQueryResults);
    }

    private Order toSortOrder(CriteriaBuilder builder, Root<CustomerPersistenceEntity> rootCustomer,
                              CustomerFilter filter) {

        String propertyName = filter.getSortByPropertyOrDefault().getPropertyName();

        if (filter.getSortDirectionOrDefault() == Sort.Direction.ASC) {
            return builder.asc(rootCustomer.get(propertyName));
        }

        if (filter.getSortDirectionOrDefault() == Sort.Direction.DESC) {
            return builder.desc(rootCustomer.get(propertyName));
        }

        return null;
    }

    private Predicate[] toPredicates(CriteriaBuilder builder, Root<CustomerPersistenceEntity> root,
                                     CustomerFilter filter) {

        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(filter.getFirstName())) {
            predicates.add(builder.like(builder.lower(root.get("firstName")), "%" + filter.getFirstName().toLowerCase() + "%"));
        }

        if (Objects.nonNull(filter.getEmail())) {
            predicates.add(builder.like(builder.lower(root.get("email")), "%" + filter.getEmail().toLowerCase() + "%"));

        }

        return predicates.toArray(new Predicate[]{});
    }

}
