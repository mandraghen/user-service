package com.smorabito.customer.unit.service;

import com.smorabito.customer.domain.model.Customer;
import com.smorabito.customer.domain.repository.CustomerRepository;
import com.smorabito.customer.dto.CustomerDto;
import com.smorabito.customer.dto.Scope;
import com.smorabito.customer.mapper.CustomerMapper;
import com.smorabito.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @InjectMocks
    private CustomerService customerService;

    @Test
    void create_whenDtoIsNull_shouldReturnEmpty() {
        Optional<CustomerDto> result = customerService.create(null);

        assertThat(result).isEmpty();
        verifyNoInteractions(customerRepository, customerMapper);
    }

    @Test
    void create_whenIdExists_shouldReturnEmpty() {
        CustomerDto dto = CustomerDto.builder()
                .id(1L)
                .firstName("Test")
                .email("test@example.com")
                .build();
        when(customerRepository.existsById(1L)).thenReturn(true);

        Optional<CustomerDto> result = customerService.create(dto);

        assertThat(result).isEmpty();
        verify(customerRepository).existsById(1L);
        verifyNoMoreInteractions(customerRepository, customerMapper);
    }

    @Test
    void create_whenEmailExists_shouldReturnEmpty() {
        CustomerDto dto = CustomerDto.builder()
                .firstName("Test")
                .email("test@example.com")
                .build();
        when(customerRepository.existsByEmail("test@example.com")).thenReturn(true);

        Optional<CustomerDto> result = customerService.create(dto);

        assertThat(result).isEmpty();
        verify(customerRepository).existsByEmail("test@example.com");
        verifyNoMoreInteractions(customerRepository, customerMapper);
    }

    @Test
    void create_whenValid_shouldReturnDto() {
        CustomerDto dto = CustomerDto.builder()
                .firstName("John Doe")
                .email("test@example.com")
                .build();
        Customer entity = Customer.builder()
                .firstName("John Doe")
                .email("test@example.com")
                .build();
        Customer saved = Customer.builder()
                .id(1L)
                .firstName("John Doe")
                .email("test@example.com")
                .build();

        when(customerRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(customerMapper.toNewEntity(dto)).thenReturn(entity);
        when(customerRepository.save(entity)).thenReturn(saved);
        when(customerMapper.toDto(saved)).thenReturn(dto);

        Optional<CustomerDto> result = customerService.create(dto);

        assertThat(result).contains(dto);
        verify(customerRepository).existsByEmail("test@example.com");
        verify(customerMapper).toNewEntity(dto);
        verify(customerRepository).save(entity);
        verify(customerMapper).toDto(saved);
    }

    @Test
    void get_whenNullIdAndNoScope_shouldReturnEmpty() {
        when(customerRepository.find(null, null)).thenReturn(Optional.empty());

        Optional<CustomerDto> result = customerService.get(null, null);

        assertThat(result).isEmpty();
        verify(customerRepository).find(null, null);
        verifyNoInteractions(customerMapper);
    }

    @Test
    void get_whenIdNotFoundAndNoScope_shouldReturnEmpty() {
        when(customerRepository.find(1L, null)).thenReturn(Optional.empty());

        Optional<CustomerDto> result = customerService.get(1L, null);

        assertThat(result).isEmpty();
        verify(customerRepository).find(1L, null);
        verifyNoInteractions(customerMapper);
    }

    @Test
    void get_whenIdFoundAndNoScope_shouldReturnBasicDto() {
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("John Doe")
                .build();
        CustomerDto dto = CustomerDto.builder()
                .id(1L)
                .firstName("John Doe")
                .build();

        when(customerRepository.find(1L, null)).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(customer, null)).thenReturn(dto);

        Optional<CustomerDto> result = customerService.get(1L, null);

        assertThat(result).contains(dto);
        verify(customerRepository).find(1L, null);
        verify(customerMapper).toDto(customer, null);
    }

    @Test
    void get_whenIdFoundAndFullScope_shouldReturnFullDto() {
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("John Doe")
                .build();
        CustomerDto dto = CustomerDto.builder()
                .id(1L)
                .firstName("John Doe")
                .build();

        when(customerRepository.find(1L, Scope.FULL)).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(customer, Scope.FULL)).thenReturn(dto);

        Optional<CustomerDto> result = customerService.get(1L, Scope.FULL);

        assertThat(result).contains(dto);
        verify(customerRepository).find(1L, Scope.FULL);
        verify(customerMapper).toDto(customer, Scope.FULL);
    }

    @Test
    void get_whenIdFoundAndBasicScope_shouldReturnBasicDto() {
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("John Doe")
                .build();
        CustomerDto dto = CustomerDto.builder()
                .id(1L)
                .firstName("John Doe")
                .build();

        when(customerRepository.find(1L, Scope.BASIC)).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(customer, Scope.BASIC)).thenReturn(dto);

        Optional<CustomerDto> result = customerService.get(1L, Scope.BASIC);

        assertThat(result).contains(dto);
        verify(customerRepository).find(1L, Scope.BASIC);
        verify(customerMapper).toDto(customer, Scope.BASIC);
    }

    @Test
    void get_whenIdFoundAndIdOnlyScope_shouldReturnIdOnlyDto() {
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("John Doe")
                .build();
        CustomerDto dto = CustomerDto.builder()
                .id(1L)
                .firstName("John Doe")
                .build();

        when(customerRepository.find(1L, Scope.ID_ONLY)).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(customer, Scope.ID_ONLY)).thenReturn(dto);

        Optional<CustomerDto> result = customerService.get(1L, Scope.ID_ONLY);

        assertThat(result).contains(dto);
        verify(customerRepository).find(1L, Scope.ID_ONLY);
        verify(customerMapper).toDto(customer, Scope.ID_ONLY);
    }

    @Test
    void update_whenIdIsNull_shouldThrowException() {
        CustomerDto dto = CustomerDto.builder().firstName("Test").build();

        when(customerRepository.findBasicById(null)).thenThrow(NullPointerException.class);

        assertThatThrownBy(() -> customerService.update(null, dto))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void update_whenEmployeeNotFound_shouldSetIdToNull() {
        CustomerDto dto = CustomerDto.builder()
                .id(1L)
                .firstName("John Doe")
                .email("test@example.com")
                .build();

        Customer mappedCustomer = Customer.builder()
                .firstName(dto.getFirstName())
                .email(dto.getEmail())
                .build();

        Customer persistedCustomer = mappedCustomer.toBuilder().id(2L).build();

        when(customerRepository.findBasicById(1L)).thenReturn(Optional.empty());
        when(customerMapper.toUpdateEntity(dto.toBuilder().id(null).build())).thenReturn(mappedCustomer);

        when(customerRepository.save(any(Customer.class))).thenReturn(persistedCustomer);
        when(customerMapper.toDto(persistedCustomer)).thenReturn(dto.toBuilder().id(2L).build());

        CustomerDto result = customerService.update(1L, dto);

        assertThat(result.getId()).isNotNull();
        verify(customerRepository).findBasicById(1L);
        verify(customerMapper).toUpdateEntity(dto.toBuilder().id(null).build());
        verify(customerRepository).save(any(Customer.class));
        verify(customerMapper).toDto(any(Customer.class));
    }

    @Test
    void update_whenEmployeeFound_shouldUpdateEmployee() {
        CustomerDto dto = CustomerDto.builder()
                .firstName("John Doe")
                .email("test@example.com")
                .version(1L)
                .build();
        Customer existingCustomer = Customer.builder()
                .id(1L)
                .firstName("Old Name")
                .email("old@example.com")
                .version(1L)
                .build();
        Customer updatedCustomer = Customer.builder()
                .id(1L)
                .firstName("John Doe")
                .email("test@example.com")
                .version(1L)
                .build();

        when(customerRepository.findBasicById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerMapper.toUpdateEntity(dto)).thenReturn(updatedCustomer);
        when(customerRepository.save(updatedCustomer)).thenReturn(updatedCustomer);
        when(customerMapper.toDto(updatedCustomer)).thenReturn(dto);

        CustomerDto result = customerService.update(1L, dto);

        assertThat(result).isEqualTo(dto);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getVersion()).isEqualTo(1);
        verify(customerRepository).findBasicById(1L);
        verify(customerMapper).toUpdateEntity(dto);
        verify(customerRepository).save(updatedCustomer);
        verify(customerMapper).toDto(updatedCustomer);
    }

    @Test
    void delete_shouldCallRepository() {
        customerService.delete(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    void delete_whenIdIsNull_shouldNotCallRepository() {
        customerService.delete(null);
        verify(customerRepository, never()).deleteById(any());
    }
}