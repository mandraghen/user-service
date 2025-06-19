package com.smorabito.customer.mapper;

import com.smorabito.customer.domain.model.Address;
import com.smorabito.customer.dto.AddressDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface AddressMapper extends GenericScopedMapper<Address, AddressDto> {

    @BeanMapping(ignoreByDefault = true)
    @InheritConfiguration(name = "entityToIdOnlyDto")
    @Mapping(target = "street")
    @Mapping(target = "city")
    @Mapping(target = "province")
    @Mapping(target = "postalCode")
    @Mapping(target = "country")
    @Named("addressBasic")
    @Override
    AddressDto entityToBasicDto(Address address);
}