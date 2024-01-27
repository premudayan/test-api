package com.nextgendynamics.crm.contactemail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nextgendynamics.crm.contact.Contact;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"contact_id", "emailType"}))
@EntityListeners(AuditingEntityListener.class)
public class ContactEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column( nullable = false)
    @Email
    @Size(min = 5, max = 100)
    private String emailAddress;
    @Column( nullable = false)
    private String emailType; // work, personal).
    private Boolean isPrimary;
    private Boolean optInStatus;
    private Boolean isVerified;
    private LocalDate dateVerified;
    private String source;
    private LocalDate lastEmailSent;
    private String emailCampaignHistory;
    private String notes;
    private Boolean isActive;
    private LocalDate statusDate;

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
        System.out.println("Entering onPrePersist() ");
        if ( contact.getContactEmails().isEmpty()  ){
            isPrimary = true;
        }else if ( isPrimary == false || isPrimary == null) {
            isPrimary = false;
        }
        if ( Objects.isNull(isActive) ) {
            isActive = true;
            statusDate = LocalDate.now();
        }
        if ( Objects.isNull(optInStatus) ) {
            optInStatus = true;
        }

    }
}
