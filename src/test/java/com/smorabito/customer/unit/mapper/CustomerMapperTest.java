package com.smorabito.customer.unit.mapper;

import com.smorabito.customer.domain.model.Address;
import com.smorabito.customer.domain.model.Customer;
import com.smorabito.customer.dto.AddressDto;
import com.smorabito.customer.dto.CustomerDto;
import com.smorabito.customer.dto.Scope;
import com.smorabito.customer.mapper.AddressMapper;
import com.smorabito.customer.mapper.CustomerMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerMapperTest {

    @Mock
    private Address address;
    @Mock
    private AddressMapper addressMapper;
    @InjectMocks
    private CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    void toDto_whenNull_shouldReturnNull() {
        CustomerDto result = customerMapper.toDto(null);

        assertThat(result).isNull();
    }

    @Test
    void toDto_whenBasicScope_shouldMapBasicFields() {
        Customer customer = Customer.builder()
                .id(1L)
                .version(2L)
                .firstName("John")
                .build();

        CustomerDto result = customerMapper.toDto(customer, Scope.BASIC);

        compareEmployees(result, customer);
        assertThat(result.getAddress()).isNull();
    }

    @Test
    void toDto_whenFullScope_shouldMapAllFields() {
        Customer customer = Customer.builder()
                .id(1L)
                .version(2L)
                .firstName("John")
                .email("john@example.com")
                .address(address)
                .build();

        when(addressMapper.toDto(address)).thenReturn(AddressDto.builder().build());
        CustomerDto result = customerMapper.toDto(customer, Scope.FULL);

        compareEmployees(result, customer);
        assertThat(result.getAddress()).isNotNull();
    }

    @Test
    void toNewEntity_whenComplete_shouldMapAllFields() {
        CustomerDto dto = CustomerDto.builder()
                .firstName("John")
                .email("john@example.com")
                .address(AddressDto.builder().build())
                .build();

        Customer result = customerMapper.toNewEntity(dto);

        compareEmployees(result, dto);
    }

    @Test
    void toUpdateEntity_shouldSetIdAndMapFields() {
        CustomerDto dto = CustomerDto.builder()
                .firstName("John")
                .email("john@example.com")
                .build();

        Customer result = customerMapper.toUpdateEntity(dto);

        compareEmployees(result, dto);
    }

    private void compareEmployees(CustomerDto actual, Customer expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());
        assertThat(actual.getVersion()).isEqualTo(expected.getVersion());
    }

    private void compareEmployees(Customer actual, CustomerDto expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());
        assertThat(actual.getVersion()).isEqualTo(expected.getVersion());
    }
}