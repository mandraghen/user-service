package com.smorabito.customer.unit.controller;

import com.smorabito.customer.controller.CustomerController;
import com.smorabito.customer.dto.CustomerDto;
import com.smorabito.customer.dto.Scope;
import com.smorabito.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;
    @InjectMocks
    private CustomerController customerController;

    @Test
    void getEmployee_whenFound_shouldReturnOk() {
        CustomerDto dto = CustomerDto.builder()
                .id(1L)
                .firstName("John")
                .build();
        when(customerService.get(1L, Scope.FULL)).thenReturn(Optional.of(dto));

        ResponseEntity<CustomerDto> response = customerController.get(1L, Scope.FULL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
        verify(customerService).get(1L, Scope.FULL);
    }

    @Test
    void getEmployee_whenNotFound_shouldReturnNotFound() {
        when(customerService.get(1L, null)).thenReturn(Optional.empty());

        ResponseEntity<CustomerDto> response = customerController.get(1L, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(customerService).get(1L, null);
    }

    @Test
    void createEmployee_whenSuccess_shouldReturnOk() {
        CustomerDto dto = CustomerDto.builder()
                .firstName("John")
                .build();
        when(customerService.create(dto)).thenReturn(Optional.of(dto));

        ResponseEntity<CustomerDto> response = customerController.create(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
        verify(customerService).create(dto);
    }

    @Test
    void createEmployee_whenFailure_shouldReturnBadRequest() {
        CustomerDto dto = CustomerDto.builder().build();
        when(customerService.create(dto)).thenReturn(Optional.empty());

        ResponseEntity<CustomerDto> response = customerController.create(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(customerService).create(dto);
    }

    @Test
    void updateEmployee_shouldReturnOk() {
        CustomerDto dto = CustomerDto.builder()
                .firstName("John")
                .build();
        CustomerDto updated = CustomerDto.builder()
                .id(1L)
                .firstName("John")
                .build();
        when(customerService.update(1L, dto)).thenReturn(updated);

        ResponseEntity<CustomerDto> response = customerController.update(1L, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updated);
        verify(customerService).update(1L, dto);
    }

    @Test
    void deleteEmployee_shouldReturnNoContent() {
        doNothing().when(customerService).delete(1L);

        ResponseEntity<Void> response = customerController.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(customerService).delete(1L);
    }
}