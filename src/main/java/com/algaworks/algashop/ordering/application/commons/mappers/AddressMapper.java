package com.algaworks.algashop.ordering.application.commons.mappers;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "street",      expression = "java(address.street())")
    @Mapping(target = "number",      expression = "java(address.number())")
    @Mapping(target = "complement",  expression = "java(address.complement())")
    @Mapping(target = "neighborhood",    expression = "java(address.neighborhood())")
    @Mapping(target = "city",        expression = "java(address.city())")
    @Mapping(target = "state",       expression = "java(address.state())")
    @Mapping(target = "zipCode",     source = "zipCode")
    AddressData toData(Address address);

    default String map(ZipCode zip) {
        return zip == null ? null : zip.value(); // ajuste se for getValue() / asString()
    }

}
