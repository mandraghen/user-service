package com.smorabito.customer.integration.domain;

import com.smorabito.customer.domain.model.Address;
import com.smorabito.customer.domain.model.Customer;
import com.smorabito.customer.domain.repository.AddressRepository;
import com.smorabito.customer.domain.repository.CustomerRepository;
import com.smorabito.customer.integration.TestContainers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerCascadeIT extends TestContainers {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void testAddressCascadePersist() {
        Address address = Address.builder().street("Main St").build();
        Customer customer = Customer.builder()
                .firstName("John")
                .email("john@email.com")
                .privacy(true)
                .address(address)
                .build();
        customer = customerRepository.save(customer);

        assertThat(customer.getAddress().getId()).isNotNull(); // Address is persisted
    }

    @Test
    @Transactional
    void testAddressCascadeMerge() {
        Address address = addressRepository.save(Address.builder().street("Old St").build());
        Customer customer = customerRepository.save(Customer.builder()
                .firstName("Jane")
                .email("jane@email.com")
                .privacy(true)
                .address(address)
                .build());

        address.setStreet("New St");
        customer.setAddress(address);
        customerRepository.save(customer);

        Address updated = addressRepository.findById(address.getId()).orElseThrow();
        assertThat(updated.getStreet()).isEqualTo("New St");
    }

    @Test
    void testAddressCascadeRemove() {
        Address address = Address.builder().street("ToDelete").build();
        Customer customer = customerRepository.save(Customer.builder()
                .firstName("Del")
                .email("del@email.com")
                .privacy(true)
                .address(address)
                .build());

        address = customer.getAddress();

        customerRepository.delete(customer);

        assertThat(addressRepository.findById(address.getId())).isEmpty();
    }

    @Test
    void testAddressCascadeDetach() {
        Address address = Address.builder().street("Detach St").build();
        Customer customer = customerRepository.save(Customer.builder()
                .firstName("Detach")
                .email("detach@email.com")
                .privacy(true)
                .address(address).build());

        em.detach(customer);

        assertThat(em.contains(address)).isFalse(); // Address is detached with Employee
    }

    @Test
    @Transactional
    void testAddressCascadeRefresh() {
        Address address = addressRepository.save(Address.builder().street("Refresh St").build());
        Customer customer = customerRepository.save(Customer.builder()
                .firstName("Refresh")
                .email("jane@email.com")
                .privacy(true)
                .address(address)
                .build());

        address.setStreet("Changed St");
        em.refresh(customer);

        assertThat(customer.getAddress().getStreet()).isEqualTo("Refresh St");
    }

    @Test
    @Transactional
    void testAddressOrphanRemoval() {
        Address address = addressRepository.save(Address.builder().street("Orphan St").build());
        Customer customer = customerRepository.save(Customer.builder()
                .firstName("Orphan")
                .email("orphan@email.com")
                .privacy(true)
                .address(address).build());

        // Remove the address from the employee
        customer.setAddress(null);
        customerRepository.saveAndFlush(customer);

        // Address should be deleted from the database
        assertThat(addressRepository.findById(address.getId())).isEmpty();
    }

    @Test
    @Transactional
    void testAddressReassignmentBetweenEmployees() {
        // Create and persist two employees, each with their own address
        Address address1 = addressRepository.save(Address.builder().street("First St").build());
        Address address2 = addressRepository.save(Address.builder().street("Second St").build());
        Customer customer1 = customerRepository.save(Customer.builder()
                .firstName("Emp1")
                .email("emp1@email.com")
                .privacy(true)
                .address(address1)
                .build());
        Customer customer2 = customerRepository.save(Customer.builder()
                .firstName("Emp2")
                .email("emp2@email.com")
                .privacy(true)
                .address(address2)
                .build());

        em.refresh(address1);
        // Reassign address1 from employee1 to employee2
        customer2.setAddress(address1);
        customerRepository.saveAndFlush(customer2);

        // Refresh entities
        em.refresh(customer1);
        em.refresh(customer2);

        // employee2 should now have address1
        assertThat(customer2.getAddress().getId()).isEqualTo(address1.getId());
        // employee1 should have no address
        assertThat(customer1.getAddress()).isNull();
        // address2 is deleted because of orphan removal
        assertThat(addressRepository.findById(address2.getId())).isNotPresent();
    }
}
