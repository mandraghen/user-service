package com.smorabito.customer.service;

import com.smorabito.customer.domain.model.Customer;
import com.smorabito.customer.domain.repository.CustomerRepository;
import com.smorabito.customer.dto.AddressDto;
import com.smorabito.customer.dto.CustomerDto;
import com.smorabito.customer.dto.Scope;
import com.smorabito.customer.mapper.CustomerMapper;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public Optional<CustomerDto> create(CustomerDto customerDto) {
        if (customerDto == null ||
                customerDto.getId() != null && customerRepository.existsById(customerDto.getId()) ||
                customerRepository.existsByEmail(customerDto.getEmail())) {
            log.debug("Customer already exists by email or id or is null: {}", customerDto);
            return Optional.empty();
        }

        Customer customer = customerMapper.toNewEntity(customerDto);
        customer = customerRepository.save(customer);
        return Optional.of(customerMapper.toDto(customer));
    }

    public Optional<CustomerDto> get(Long id, Scope scope) {
        return customerRepository.find(id, scope)
                .map(customer -> customerMapper.toDto(customer, scope));
    }

    @Transactional
    public CustomerDto update(Long id, @NonNull CustomerDto customerDto) {
        customerRepository.findBasicById(id).ifPresentOrElse(existingCustomer -> {
            customerDto.setId(existingCustomer.getId());
            if (customerDto.getVersion() == null) {
                customerDto.setVersion(existingCustomer.getVersion());
            }
            updateAddressRelation(existingCustomer, customerDto);
        }, () -> {
            customerDto.setId(null);
            if (customerDto.getAddress() != null) {
                customerDto.getAddress().setId(null);
            }
        });

        Customer updated = customerMapper.toUpdateEntity(customerDto);
        return customerMapper.toDto(customerRepository.save(updated));
    }

    private void updateAddressRelation(Customer existingCustomer, CustomerDto customerDto) {
        Long addressId = existingCustomer.getAddressId();
        AddressDto addressDto = customerDto.getAddress();
        //if there is an existing address
        if(addressId != null && addressDto != null) {
            //update existing address
            addressDto.setId(addressId);
            addressDto.setVersion(existingCustomer.getAddress().getVersion());
        } else if (addressId == null && addressDto != null) {
            //create a new address
            addressDto.setId(null);
            addressDto.setVersion(null);
        }
    }

    public void delete(Long id) {
        if (id == null) {
            return;
        }
        customerRepository.deleteById(id);
    }
}