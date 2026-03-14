package com.algaworks.algashop.ordering.infrastructure.listener.customer;

import com.algaworks.algashop.ordering.core.application.AbstractApplicationIT;
import com.algaworks.algashop.ordering.core.ports.in.customer.ForAddingLoyaltyPoints;
import com.algaworks.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers;
import com.algaworks.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers.NotifyNewRegistrationInput;
import com.algaworks.algashop.ordering.core.domain.model.commons.Email;
import com.algaworks.algashop.ordering.core.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerRegisteredEvent;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import com.algaworks.algashop.ordering.infrastructure.adapters.in.listener.customer.CustomerEventListener;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomerEventListenerTestIT extends AbstractApplicationIT {

    private final ApplicationEventPublisher applicationEventPublisher;

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @MockitoBean
    private ForAddingLoyaltyPoints loyaltyPointsApplicationService;

    @MockitoSpyBean
    private ForNotifyingCustomers notificationApplicationService;

    @Test
    public void shouldListenOrderReadyEvent() {
        applicationEventPublisher.publishEvent(
                OrderReadyEvent.of(
                        OrderId.of(),
                        CustomerId.of(),
                        OffsetDateTime.now()
                )
        );

        Mockito.verify(customerEventListener).listen(Mockito.any(OrderReadyEvent.class));

        Mockito.verify(loyaltyPointsApplicationService).addLoyaltyPoints(
                Mockito.any(UUID.class),
                Mockito.any(String.class)
        );
    }

    @Test
    public void shouldListenCustomerRegisteredEvent() {
        applicationEventPublisher.publishEvent(
                CustomerRegisteredEvent.of(
                        CustomerId.of(),
                        OffsetDateTime.now(),
                        FullName.of("John", "Doe"),
                        Email.of("john.doe@email.com")
                )
        );

        Mockito.verify(customerEventListener).listen(Mockito.any(CustomerRegisteredEvent.class));

        Mockito.verify(notificationApplicationService)
                .notifyNewRegistration(Mockito.any(NotifyNewRegistrationInput.class));
    }

}