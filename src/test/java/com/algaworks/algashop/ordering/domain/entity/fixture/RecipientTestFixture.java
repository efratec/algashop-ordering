package com.algaworks.algashop.ordering.domain.entity.fixture;

import com.algaworks.algashop.ordering.domain.valueobject.Document;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.valueobject.Recipient;

public class RecipientTestFixture {

    public static final String FIRST_NAME = "TESTE";
    public static final String LAST_NAME = "DA SILVA";
    public static final String PHONE = "82981667744";
    public static final String DOCUMENT = "TESTANDO";

    public static Recipient aRecipient() {
        return Recipient
                .builder()
                .fullName(FullName.of(FIRST_NAME, LAST_NAME))
                .phone(Phone.of(PHONE))
                .document(Document.of(DOCUMENT))
                .build();
    }

}
