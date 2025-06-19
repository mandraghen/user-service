package com.smorabito.customer.domain.repository;

import com.smorabito.customer.domain.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    // Custom query methods can be defined here if needed
}
