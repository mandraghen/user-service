package com.smorabito.customer.integration.controller;

import com.smorabito.customer.domain.model.Address;
import com.smorabito.customer.domain.model.Customer;
import com.smorabito.customer.domain.repository.CustomerRepository;
import com.smorabito.customer.dto.CustomerDto;
import com.smorabito.customer.integration.TestContainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static com.smorabito.customer.integration.data.DbDataDto.EXISTING_ADDRESS;
import static com.smorabito.customer.integration.data.DbDataDto.EXISTING_CUSTOMER;
import static com.smorabito.customer.integration.data.DbDataDto.NEW_COMPLETE_CUSTOMER_WITH_EXISTING_ADDRESS;
import static com.smorabito.customer.integration.data.DbDataDto.NEW_COMPLETE_CUSTOMER_WITH_EXISTING_RELATIONS2;
import static com.smorabito.customer.integration.data.DbDataDto.NEW_COMPLETE_CUSTOMER_WITH_NEW_RELATIONS;
import static com.smorabito.customer.integration.data.DbDataDto.NEW_CUSTOMER;
import static com.smorabito.customer.integration.data.DbDataDto.NO_EMAIL_CUSTOMER;
import static com.smorabito.customer.integration.data.DbDataDto.OTHER_ADDRESS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIT extends TestContainers {

    private static final String CUSTOMER_API_PATH = "/customer";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void getExistingCustomer_shouldReturnCustomer() {
        ResponseEntity<CustomerDto> response = restTemplate
                .getForEntity(CUSTOMER_API_PATH + "/" + EXISTING_CUSTOMER.getId(), CustomerDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CustomerDto customerDto = response.getBody();
        assertThat(customerDto).isNotNull();
        assertThat(customerDto.getId()).isEqualTo(EXISTING_CUSTOMER.getId());
        assertThat(customerDto.getFirstName()).isEqualTo(EXISTING_CUSTOMER.getFirstName());
        assertThat(customerDto.getPhoneNumber()).isEqualTo(EXISTING_CUSTOMER.getPhoneNumber());
    }

    @Test
    void getNonExistingCustomer_shouldReturnNotFound() {
        long nonExistingId = 999L;
        ResponseEntity<CustomerDto> response = restTemplate
                .getForEntity(CUSTOMER_API_PATH + "/" + nonExistingId, CustomerDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void createExistingCustomer_shouldReturnConflict() {
        ResponseEntity<CustomerDto> response = restTemplate
                .postForEntity(CUSTOMER_API_PATH, EXISTING_CUSTOMER, CustomerDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        var customerDto = response.getBody();

        assertThat(customerDto).isNull();
        assertThat(customerRepository.existsById(EXISTING_CUSTOMER.getId())).isTrue();
    }

    @Test
    void createNewCustomerWithExistingAddress_shouldPersistNewAddress() {
        ResponseEntity<CustomerDto> response = restTemplate
                .postForEntity(CUSTOMER_API_PATH, NEW_COMPLETE_CUSTOMER_WITH_EXISTING_ADDRESS, CustomerDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomerDto customerDto = response.getBody();
        assertThat(customerDto).isNotNull();
        assertThat(customerDto.getId()).isNotNull();

        Customer saved = customerRepository.findFullById(customerDto.getId()).orElseThrow();

        assertCustomerMatches(saved, NEW_COMPLETE_CUSTOMER_WITH_EXISTING_ADDRESS.toBuilder()
                .address(EXISTING_ADDRESS)
                .build());
    }

    @Test
    void createNewCustomerWithNewRelations_shouldPersist() {
        ResponseEntity<CustomerDto> response = restTemplate
                .postForEntity(CUSTOMER_API_PATH, NEW_COMPLETE_CUSTOMER_WITH_NEW_RELATIONS, CustomerDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomerDto customerDto = response.getBody();
        assertThat(customerDto).isNotNull();
        assertThat(customerDto.getId()).isNotNull();

        Customer saved = customerRepository.findFullById(customerDto.getId()).orElseThrow();

        assertCustomerMatches(saved, NEW_COMPLETE_CUSTOMER_WITH_NEW_RELATIONS);
    }

    private void assertCustomerMatches(Customer actual, CustomerDto expected) {
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());


        Address address = actual.getAddress();
        assertThat(address).isNotNull();
        assertThat(address.getId()).isNotNull();
        if (expected.getAddress().getId() != null) {
            assertThat(address.getId()).isNotEqualTo(expected.getAddress().getId());
        }
        assertThat(address.getStreet()).isEqualTo(expected.getAddress().getStreet());
        assertThat(address.getCity()).isEqualTo(expected.getAddress().getCity());
        assertThat(address.getProvince()).isEqualTo(expected.getAddress().getProvince());
        assertThat(address.getPostalCode()).isEqualTo(expected.getAddress().getPostalCode());
        assertThat(address.getCountry()).isEqualTo(expected.getAddress().getCountry());
    }

    @Test
    void createCustomer_withNullRelations_shouldPersist() {
        ResponseEntity<CustomerDto> response = restTemplate
                .postForEntity(CUSTOMER_API_PATH, NEW_CUSTOMER, CustomerDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomerDto customerDto = response.getBody();
        assertThat(customerDto).isNotNull();

        Optional<Customer> savedOpt = customerRepository.findFullById(customerDto.getId());
        assertThat(savedOpt).isPresent();

        Customer saved = savedOpt.get();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAddress()).isNull();
    }

    @Test
    void createCustomer_withNullEmail_shouldReturnError() {
        ResponseEntity<CustomerDto> response = restTemplate
                .postForEntity(CUSTOMER_API_PATH, NO_EMAIL_CUSTOMER, CustomerDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateExistingCustomer_shouldPersist() {
        CustomerDto updateDto = EXISTING_CUSTOMER.toBuilder()
                .firstName("Updated Name")
                .address(OTHER_ADDRESS)
                .build();

        ResponseEntity<CustomerDto> updateResponse = restTemplate
                .exchange(CUSTOMER_API_PATH + "/" + updateDto.getId(), HttpMethod.PUT, new HttpEntity<>(updateDto),
                        CustomerDto.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CustomerDto returned = updateResponse.getBody();
        assertThat(returned).isNotNull();
        assertThat(returned.getFirstName()).isEqualTo(updateDto.getFirstName());

        checkRelations(returned.getId(), updateDto);
    }

    @Test
    void updateNewCustomer_shouldPersist() {
        ResponseEntity<CustomerDto> updateResponse = restTemplate
                .exchange(CUSTOMER_API_PATH + "/" + NEW_COMPLETE_CUSTOMER_WITH_EXISTING_RELATIONS2.getId(),
                        HttpMethod.PUT, new HttpEntity<>(NEW_COMPLETE_CUSTOMER_WITH_EXISTING_RELATIONS2),
                        CustomerDto.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CustomerDto returned = updateResponse.getBody();
        assertThat(returned).isNotNull();
        assertThat(returned.getId()).isNotNull();

        //check relations
        checkRelations(returned.getId(), NEW_COMPLETE_CUSTOMER_WITH_EXISTING_RELATIONS2);
    }

    private void checkRelations(Long id, CustomerDto updateDto) {
        Optional<Customer> savedOpt = customerRepository.findFullById(id);
        assertThat(savedOpt).isPresent();
        Customer updated = savedOpt.get();

        assertThat(updated.getAddress()).isNotNull();
        // address id should be new unless it was already associated to the customer
        assertThat(updated.getAddress().getId()).isNotEqualTo(updateDto.getAddress().getId());
    }

    @Test
    void deleteExistingCustomer_shouldReturnOk() {
        //create a customer to delete
        Customer customerToDelete = Customer.builder()
                .email("todelete@delete.com")
                .firstName("To Delete")
                .phoneNumber("123456789")
                .privacy(true)
                .build();
        Customer saved = customerRepository.save(customerToDelete);

        // When deleting the customer
        ResponseEntity<Void> response = restTemplate
                .exchange(CUSTOMER_API_PATH + "/" + saved.getId(), HttpMethod.DELETE, null, Void.class);

        // Then the response should be No Content
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // And the customer should no longer exist in the repository
        assertThat(customerRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void deleteNonExistingCustomer_shouldReturnOk() {
        // Given a non-existing customer ID
        long nonExistingId = 999L;

        // When trying to delete the non-existing customer
        ResponseEntity<Void> response = restTemplate
                .exchange(CUSTOMER_API_PATH + "/" + nonExistingId, HttpMethod.DELETE, null, Void.class);

        // Then the response should be Not Found
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(customerRepository.existsById(nonExistingId)).isFalse();
    }
}
