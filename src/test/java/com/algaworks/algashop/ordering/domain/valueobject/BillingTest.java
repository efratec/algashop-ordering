package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.entity.fixture.AddressTestFixture.aAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BillingTest {

    @Test
    void givenValidData_whenCreate_thenShouldCreateShippingInfo() {
        var fullName = FullName.of("Anonymos", "Anonymos");
        var document = Document.of("123.456.789-00");
        var phone = Phone.of("99999-9999");
        var address = aAddress().build();

        var billingExpected = Billing.of(fullName, document, phone, address);

        assertEquals(fullName, billingExpected.fullName());
        assertEquals(document, billingExpected.document());
        assertEquals(phone, billingExpected.phone());
        assertEquals(address, billingExpected.address());
    }

    @Test
    void givenNullFullName_whenCreate_thenShouldThrowException() {
        var document = Document.of("123.456.789-00");
        var phone = Phone.of("99999-9999");
        var address = aAddress().build();

        assertThrows(NullPointerException.class, () ->
                Billing.of(null, document, phone, address)
        );
    }

    @Test
    void givenNullDocument_whenCreate_thenShouldThrowException() {
        var fullname = FullName.of("Anonymos", "Anonymos");
        var phone = Phone.of("99999-9999");
        var address = aAddress().build();

        assertThrows(NullPointerException.class, () ->
                Billing.of(fullname, null, phone, address)
        );
    }

    @Test
    void givenNullPhone_whenCreate_thenShouldThrowException() {
        var fullname = FullName.of("Anonymos", "Anonymos");
        var document = Document.of("123.456.789-00");
        var address = aAddress().build();

        assertThrows(NullPointerException.class, () ->
                Billing.of(fullname, document, null, address)
        );
    }

    @Test
    void givenNullAddress_whenCreate_thenShouldThrowException() {
        var fullName = FullName.of("Efraim", "Tenório");
        var document = Document.of("123.456.789-00");
        var phone = Phone.of("99999-9999");

        assertThrows(NullPointerException.class, () ->
                Billing.of(fullName, document, phone, null)
        );
    }

}
