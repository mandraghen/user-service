package com.smorabito.customer.domain.model;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Table(
        indexes = {@Index(name = "customer_email_index", columnList = "email", unique = true)}
)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "defaultCache")
public class Customer extends BasicEntity {
    @SequenceGenerator(name = "customer_id_seq", sequenceName = "customer_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq")
    @Column(updatable = false)
    private Long id;

    private String firstName;

    private String lastName;

    @NotBlank
    @Email
    private String email;

    private String phoneNumber;

    @Past
    private LocalDate birthDate;

    private String birthPlace;

    private String profession;

    private String sector;

    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    private String childrenDescription;

    //this could be extended in the future to support regions filtered by country
    @Enumerated(EnumType.STRING)
    private Region preferredRegion;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @AssertTrue
    private boolean privacy;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "defaultCache")
    private Address address;

    @Setter(AccessLevel.NONE)
    @Column(name = "address_id", insertable = false, updatable = false)
    private Long addressId;

    public void setAddress(Address address) {
        if (Objects.nonNull(address)) {
            if (Objects.nonNull(address.getCustomer()) && address.getCustomer() != this) {
                address.getCustomer().setAddress(null);
            }
            address.setCustomer(this);
        }
        this.address = address;
    }
}
