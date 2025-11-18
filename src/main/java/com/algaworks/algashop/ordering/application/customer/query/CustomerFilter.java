package com.algaworks.algashop.ordering.application.customer.query;

import com.algaworks.algashop.ordering.application.commons.SorteablePageFilter;
import lombok.*;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CustomerFilter extends SorteablePageFilter<CustomerFilter.SortType> {

    private String firstName;
    private String email;;

    public static CustomerFilter pagination(int size, int page) {
        var filter = new CustomerFilter();
        filter.setPage(page);
        filter.setSize(size);
        return filter;
    }

    @Override
    public SortType getSortByPropertyOrDefault() {
        return getSortByProperty() == null ? SortType.REGISTERED_AT : getSortByProperty();
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return getSortDirection() == null ? Sort.Direction.ASC : getSortDirection();
    }

    @Getter
    @RequiredArgsConstructor
    public enum SortType {
        REGISTERED_AT("registeredAt"),
        FIRST_NAME("firstName");

        private final String propertyName;
    }

}
