package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.entity.AddressTestDataBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShippingInfoTest {

    @Test
    void givenValidData_whenCreate_thenShouldCreateShippingInfo() {
        var fullName = FullName.of("Anonymos", "Anonymos");
        var document = Document.of("123.456.789-00");
        var phone = Phone.of("99999-9999");
        var address = AddressTestDataBuilder.aAddress().build();

        var shippingInfoExpected = ShippingInfo.of(fullName, document, phone, address);

        assertEquals(fullName, shippingInfoExpected.fullName());
        assertEquals(document, shippingInfoExpected.document());
        assertEquals(phone, shippingInfoExpected.phone());
        assertEquals(address, shippingInfoExpected.address());
    }

    @Test
    void givenNullFullName_whenCreate_thenShouldThrowException() {
        var document = new Document("123.456.789-00");
        var phone = new Phone("99999-9999");
        var address = AddressTestDataBuilder.aAddress().build();

        assertThrows(NullPointerException.class, () ->
                ShippingInfo.of(null, document, phone, address)
        );
    }

    @Test
    void givenNullDocument_whenCreate_thenShouldThrowException() {
        var fullname = FullName.of("Anonymos", "Anonymos");
        var phone = new Phone("99999-9999");
        var address = AddressTestDataBuilder.aAddress().build();

        assertThrows(NullPointerException.class, () ->
                ShippingInfo.of(fullname, null, phone, address)
        );
    }

    @Test
    void givenNullPhone_whenCreate_thenShouldThrowException() {
        var fullname = FullName.of("Anonymos", "Anonymos");
        var document = Document.of("123.456.789-00");
        var address = AddressTestDataBuilder.aAddress().build();

        assertThrows(NullPointerException.class, () ->
                ShippingInfo.of(fullname, document, null, address)
        );
    }

    @Test
    void givenNullAddress_whenCreate_thenShouldThrowException() {
        FullName fullName = FullName.of("Efraim", "Tenório");
        Document document = new Document("123.456.789-00");
        Phone phone = new Phone("99999-9999");

        assertThrows(NullPointerException.class, () ->
                new ShippingInfo(fullName, document, phone, null)
        );
    }

}
