package com.algaworks.algashop.ordering.application.customer.query;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.existingCustomer;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomerQueryServiceTestIT {

    private final CustomerQueryService queryService;
    private final Customers customers;

    @Test
    void shouldFilterByPages() {
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Marcio", "Santos")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Andrade", "Silva")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Carla", "Teste")).build());

        var filter = CustomerFilter.pagination(2, 0);

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getNumberOfElements()).isEqualTo(2);
    }

    @Test
    void shouldFilterBYFirstName() {
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Marcio", "Santos")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Andrade", "Silva")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Carla", "Teste")).build());

        var filter = CustomerFilter.builder().firstName("andrade").build();

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent())
                .extracting(CustomerSummaryOutput::getFirstName)
                .containsOnly("Andrade");
    }

    @Test
    void shouldFilterByEmail() {
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Marcio", "Santos")).email(Email.of("teste@gmail.com")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Andrade", "Silva")).email(Email.of("andrade@gmail.com")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Carla", "Teste")).email(Email.of("carla@gmail.com")).build());

        var filter = CustomerFilter.builder().email("teste").build();

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent())
                .extracting(CustomerSummaryOutput::getEmail)
                .containsExactlyInAnyOrder("teste@gmail.com");
    }

    @Test
    void shouldFilterByMultipleParams() {
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Marcio", "Santos")).email(Email.of("teste@gmail.com")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Andrade", "Silva")).email(Email.of("andrade@gmail.com")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Carla", "Teste")).email(Email.of("carla@gmail.com")).build());

        var filter = CustomerFilter.builder().firstName("andrade").email("andrade@gmail.com").build();

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("Andrade");
        assertThat(page.getContent().getFirst().getEmail()).isEqualTo("andrade@gmail.com");
    }

    @Test
    void shouldCustomerByFirstNameDesc() {
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Marcio", "Santos")).email(Email.of("teste@gmail.com")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Andrade", "Silva")).email(Email.of("andrade@gmail.com")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Carla", "Teste")).email(Email.of("carla@gmail.com")).build());

        var filter = CustomerFilter.builder().build();
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.DESC);

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("Marcio");
    }

    @Test
    void shouldOrderByFirstNameAsc() {
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Marcio", "Santos")).email(Email.of("teste@gmail.com")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Andrade", "Silva")).email(Email.of("andrade@gmail.com")).build());
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Carla", "Teste")).email(Email.of("carla@gmail.com")).build());

        var filter = CustomerFilter.builder().build();
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.ASC);

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("Andrade");
    }

    @Test
    void givenCustomerNonExistent_should_ReturnEmptyPage() {
        customers.add(existingCustomer().id(CustomerId.of()).fullName(FullName.of("Carla", "Teste")).email(Email.of("carla@gmail.com")).build());

        var filter = CustomerFilter.builder().firstName("TESTE").build();
        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.isEmpty()).isTrue();
        assertThat(page.getTotalElements()).isEqualTo(0);
    }

}