package com.nextgendynamics.crm.contact;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nextgendynamics.crm.contactaddress.ContactAddress;
import com.nextgendynamics.crm.contactemail.ContactEmail;
import com.nextgendynamics.crm.contactphone.ContactPhone;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String CustomerId;
    @Column(nullable = false, length = 50)
    @Size(min = 2, message = "First name must have at least 2 characters")
    private String firstName;
    @Column(length = 50)
    private String middleName;
    @Column(nullable = false, length = 50)
    @Size(min = 2, message = "Last name must have at least 2 characters")
    private String lastName;
    @Column(length = 10)
    private String namePrefix;
    @Column(length = 30)
    private String nameSuffix;
    @JsonIgnore
    private String passcode;
    private Long primaryEmailId;
    private Long primaryPhoneId;
    private Long primaryAddressId;
    @Column(length = 50)
    private String jobTitle;
    @Column(length = 100)
    private String university;
    @Column(length = 11)
    private String ssn;
    @Column(length = 50)
    private String industry;
    @Past(message = "Birth date cannot be a future date")
    private LocalDate dateOfBirth;
    @Column(length = 20)
    private String gender;
    @Column(length = 20)
    private String employeeId;
    private Long companyId;
    @Column(length = 100)
    private String company;
    private Long departmentId;
    @Column(length = 20)
    private String department;
    @Column(length = 30)
    private String race;
    @Column(length = 20)
    private String contactType;
    @Column(length = 20)
    private String preferredContactMethod;
    private String notes;
    @Column(length = 20)
    private String statusCode;
    private LocalDate statusDate;
    private LocalDate customerSince;
    @Column(length = 20)
    private String preferredLanguage;
    @Column(length = 30)
    private String referralSource;
    private String customerInteractions;
    @Column(length = 20)
    private String customerRating;
    private String socialMediaProfiles;
    private String socialMediaHandles;
    private String customerFeedback;
    private String customerPreferences;
    private String preferredCommunicationTime;
    private String legalEntityType;
    private String subscriptionDetails;
    private LocalDate renewalDate;
    private String customerPortalAccess;
    private String industryChallenges;
    private String contactRole;
    private String linkedContacts;
    private String contactRating;
    private String educationQualifications;
    private LocalDate workAnniversary;
    private String interestsHobbies;
    @Column(length = 50)
    private String languagesSpoken;
    private String meetingPreferences;
    private String projectInvolvement;
    private String industryCertifications;
    private String travelPreferences;
    private String linkedInRecommendations;
    private String eventAttendanceHistory;
    private String contactImage;
    private String emergencyContactInformation;
    private String customFields;

    @CreatedBy
    @Column( nullable = false, updatable = false )
    private Integer createdBy;

    @CreatedDate
    @Column( nullable = false,  updatable = false )
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(insertable = false)
    private Integer lastModifiedBy;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @OneToMany(mappedBy = "contact")
    private List<ContactEmail> contactEmails;

    @OneToMany(mappedBy = "contact")
    private List<ContactPhone> contactPhones;

    @OneToMany(mappedBy = "contact")
    private List<ContactAddress> contactAddresses;

    @PrePersist
    private void onPersist() {
        if (CustomerId != null) {
            this.CustomerId = String.format("%010d", id);
        }
        if (statusCode == null ) {
            this.statusCode = "A";
            this.statusDate = LocalDate.now();
        } else if (this.statusCode.equalsIgnoreCase(statusCode) ) {
            this.statusCode = statusCode.toUpperCase();
            this.statusDate = LocalDate.now();
        }
    }


}
