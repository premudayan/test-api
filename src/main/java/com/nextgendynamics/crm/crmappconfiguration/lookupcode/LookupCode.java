package com.nextgendynamics.crm.crmappconfiguration.lookupcode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nextgendynamics.crm.crmappconfiguration.lookuptype.LookupType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"lookupTypeName", "lookupCodeName"}))
@EntityListeners(AuditingEntityListener.class)
public class LookupCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String lookupTypeName;

    @Column(nullable = false)
    @NotNull(message = "The Code Name cannot be empty")
    @NotEmpty(message = "The Code Name cannot be empty")
    private String lookupCodeName;

    @Column(nullable = false)
    @NotNull(message = "The Lookup Value cannot be empty")
    @NotEmpty(message = "The Lookup Value cannot be empty")
    private String lookupValue;

    private Boolean isDefault;
    private String description;
    private int sortOrder;
    private Boolean isActive;
    private String customColumn1;
    private String customColumn2;
    private String customColumn3;

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
    private LookupType lookupType;
}
