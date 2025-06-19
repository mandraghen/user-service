package com.smorabito.customer.mapper;

import com.smorabito.customer.domain.model.Customer;
import com.smorabito.customer.dto.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = {AddressMapper.class})
public interface CustomerMapper extends GenericScopedMapper<Customer, CustomerDto> {

    @Mapping(target = "address", ignore = true)
    @Named("customerBasic")
    @Override
    CustomerDto entityToBasicDto(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "address", qualifiedByName = "newEntity")
    @Named("customerNewEntity")
    Customer toNewEntity(CustomerDto dto);

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "address", qualifiedByName = "updateEntity")
    @Named("customerUpdateEntity")
    Customer toUpdateEntity(CustomerDto dto);
}
