package com.nextgendynamics.crm.contactphone;

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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"contact_id", "phoneType"}))
@EntityListeners(AuditingEntityListener.class)
public class ContactPhone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String countryCode;
    private String phoneNumber;
    private String formattedPhoneNumber;
    private String phoneType;
    private Boolean isPrimary;
    private String callOptInoutStatus;  //Opt-In , Opt-Out
    private String verificationStatus;
    private String source;
    private LocalDate lastCallDate;
    private String lastCallOutcome; //Specify the outcome of the last phone call (e.g., answered, voicemail).
    private String callHistory;
    private String smsOptInOutStatus;  //Opt-In , Opt-Out
    private String lastSmsSent;
    private String smsHistory;
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
        if ( contact.getContactPhones().isEmpty()  ){
            isPrimary = true;
        }else if ( isPrimary == false || isPrimary == null) {
            isPrimary = false;
        }
    }

    public ContactPhone() {
        this.isActive = true;
        this.statusDate = LocalDate.now();
    }
}
