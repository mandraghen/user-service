package com.smorabito.customer.integration.data;

import com.smorabito.customer.dto.AddressDto;
import com.smorabito.customer.dto.CustomerDto;

public class DbDataDto {
    public static final CustomerDto EXISTING_CUSTOMER;
    public static final AddressDto EXISTING_ADDRESS;
    public static final CustomerDto NEW_CUSTOMER;
    public static final CustomerDto NO_EMAIL_CUSTOMER;
    public static final CustomerDto NEW_COMPLETE_CUSTOMER_WITH_EXISTING_ADDRESS;
    public static final CustomerDto NEW_COMPLETE_CUSTOMER_WITH_NEW_RELATIONS;
    public static final AddressDto OTHER_ADDRESS;
    public static final CustomerDto NEW_COMPLETE_CUSTOMER_WITH_EXISTING_RELATIONS2;

    static {
        EXISTING_CUSTOMER = CustomerDto.builder()
                .id(1L)
                .firstName("Salvo")
                .email("salvo@salvo.it")
                .phoneNumber("321321231")
                .privacy(true)
                .build();

        NEW_CUSTOMER = CustomerDto.builder()
                .firstName("John Doe")
                .email("john.doe@doe.com")
                .phoneNumber("123456789")
                .privacy(true)
                .build();

        NO_EMAIL_CUSTOMER = NEW_CUSTOMER.toBuilder()
                .email("")
                .build();

        EXISTING_ADDRESS = AddressDto.builder()
                .id(1L)
                .street("via Piave")
                .city("Casorezzo")
                .postalCode("20003")
                .country("Italia")
                .build();

        NEW_COMPLETE_CUSTOMER_WITH_EXISTING_ADDRESS = CustomerDto.builder()
                .firstName("Jane Doe")
                .email("jane.doe@doe.com")
                .phoneNumber("987654321")
                .privacy(true)
                .address(EXISTING_ADDRESS)
                .build();

        NEW_COMPLETE_CUSTOMER_WITH_NEW_RELATIONS = CustomerDto.builder()
                .firstName("Jane Doe2")
                .email("jane.doe2@doe.com")
                .phoneNumber("987654321")
                .privacy(true)
                .address(AddressDto.builder()
                        .street("123 Main St")
                        .city("Springfield")
                        .province("IL")
                        .postalCode("62701")
                        .country("USA")
                        .build())
                .build();

        OTHER_ADDRESS = AddressDto.builder()
                .id(2L)
                .street("via Po")
                .city("Milano")
                .province("MI")
                .postalCode("20100")
                .country("Italia")
                .build();

        NEW_COMPLETE_CUSTOMER_WITH_EXISTING_RELATIONS2 = CustomerDto.builder()
                .id(99L)
                .firstName("Jim Doe")
                .email("jim.doe@doe.com")
                .phoneNumber("654433222")
                .privacy(true)
                .address(AddressDto.builder()
                        .id(EXISTING_ADDRESS.getId())
                        .build())
                .build();
    }
}
