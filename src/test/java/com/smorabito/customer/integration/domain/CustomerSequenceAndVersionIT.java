package com.smorabito.customer.integration.domain;

import com.smorabito.customer.domain.model.Customer;
import com.smorabito.customer.domain.repository.CustomerRepository;
import com.smorabito.customer.integration.TestContainers;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CustomerSequenceAndVersionIT extends TestContainers {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testSequenceGenerator() {
        Customer customer1 = customerRepository.save(Customer.builder()
                .firstName("Alice")
                .email("alice@email.com")
                .privacy(true)
                .build());
        Customer customer2 = customerRepository.save(Customer.builder()
                .firstName("Bob")
                .email("emp1@email.com")
                .privacy(true)
                .build());

        assertThat(customer1.getId()).isNotNull();
        assertThat(customer2.getId()).isNotNull();
        assertThat(customer2.getId()).isGreaterThan(customer1.getId());
    }

    @Test
    void testVersionField() {
        Customer customer = customerRepository.save(Customer.builder()
                .firstName("Charlie")
                .email("charlie@email.com")
                .privacy(true)
                .build());
        Long initialVersion = customer.getVersion();

        customer.setFirstName("Charlie Updated");
        customer = customerRepository.save(customer);

        assertThat(customer.getVersion()).isEqualTo(initialVersion + 1);
    }

    @Test
    void testVersionConflictThrowsException() {
        // Save initial employee
        Customer customer = customerRepository.save(Customer.builder()
                .firstName("Daisy")
                .email("daisy@email.com")
                .privacy(true)
                .build());

        // Simulate two concurrent loads
        Customer e1 = customerRepository.findById(customer.getId()).orElseThrow();
        Customer e2 = customerRepository.findById(customer.getId()).orElseThrow();

        // First update succeeds
        e1.setFirstName("Daisy 1");
        customerRepository.save(e1);

        // Second update should fail due to version conflict
        e2.setFirstName("Daisy 2");
        assertThatThrownBy(() -> customerRepository.saveAndFlush(e2))
                .isInstanceOfAny(OptimisticLockingFailureException.class, OptimisticLockException.class);
    }
}
