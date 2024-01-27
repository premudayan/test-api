package com.nextgendynamics.crm.contact;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContactSearchCriteria {
    private String CustomerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String emailAddress;
    private String emailType;
    private String phoneNumber;
    private String phoneType;
    private String addressType;
    private String streetAddress;
    private String city;
    private String zipCode;
    private String postalCode;
    private String state;
    private String country;
    private String jobTitle;
    private String university;
    private String ssn;
    private String industry;
    private LocalDate dateOfBirth;
    private LocalDate dateOfBirthStart;
    private LocalDate dateOfBirthEnd;
    private String gender;
    private String employeeId;
    private String company;
    private String department;
    private String race;
    private String contactType;
    private String preferredContactMethod;
    private String statusCode;
    private LocalDate customerSince;
    private LocalDate customerSinceStart;
    private LocalDate customerSinceEnd;
    private String preferredLanguage;
    private String languagesSpoken;

}
