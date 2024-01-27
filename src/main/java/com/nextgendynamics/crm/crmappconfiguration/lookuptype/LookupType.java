package com.nextgendynamics.crm.crmappconfiguration.lookuptype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nextgendynamics.crm.crmappconfiguration.lookupcode.LookupCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
public class LookupType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull(message = "The Type Name cannot be empty")
    @NotEmpty(message = "The Type Name cannot be empty")
    private String lookupTypeName;

    private String description;
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

    @OneToMany(mappedBy = "lookupType", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<LookupCode> lookupCodes;

}
