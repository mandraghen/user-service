package com.smorabito.customer.domain.repository;

import com.smorabito.customer.domain.model.Customer;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface CustomerRepository extends ScopedRepository<Customer, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    @Query("SELECT e FROM Customer e " +
            "LEFT JOIN FETCH e.address " +
            "WHERE e.id = :id")
    Optional<Customer> findFullById(Long id);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Optional<Customer> findBasicById(Long id);

    boolean existsByEmail(String email);
}
