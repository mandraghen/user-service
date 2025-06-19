package com.smorabito.customer.dto;

import com.smorabito.customer.domain.model.EducationLevel;
import com.smorabito.customer.domain.model.Gender;
import com.smorabito.customer.domain.model.MaritalStatus;
import com.smorabito.customer.domain.model.Region;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class CustomerDto extends BasicEntityDto {
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
    private EducationLevel educationLevel;
    private String childrenDescription;
    private Region preferredRegion;
    private MaritalStatus maritalStatus;
    private Gender gender;
    @AssertTrue
    private boolean privacy;
    private AddressDto address;
}
