package com.nextgendynamics.crm.contactaddress;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nextgendynamics.crm.contact.Contact;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"contact_id", "addressType"}))
@EntityListeners(AuditingEntityListener.class)
public class ContactAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  addressType; // billing, shipping, home).
    private String streetAddress;
    private String streetAddressLine2;
    private String city;
    private String stateCode;
    private String state;
    private String zipCode;
    private String postalCode;
    private String countryCode;
    private String country;
    private Long latitude;
    private Long longitude;
    private Boolean isPrimary;
    private Boolean optInStatus;
    private String verificationStatus;
    private String source; // form submission, CRM import).
    private LocalDate dateVerified;
    private LocalDate lastMailSent;
    private String mailHistory;
    private Boolean isActive;
    private LocalDate statusDate;
    private String notes;

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

    @ManyToOne
    @JsonIgnore
    private Contact contact;

    @PrePersist
    private void onPrePersist() {
        if ( contact.getContactAddresses().isEmpty()  ){
            isPrimary = true;
        }else if ( isPrimary == false || isPrimary == null) {
            isPrimary = false;
        }

        if (isActive == null ) {
            this.isActive = true;
            this.statusDate = LocalDate.now();
        } else if (this.isActive == isActive ) {
            this.isActive = isActive;
            this.statusDate = LocalDate.now();
        }
    }
}
