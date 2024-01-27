package com.nextgendynamics.crm.crmappconfiguration.lookupcode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LookupCodeRepository extends JpaRepository<LookupCode, Long> {
    LookupCode findByLookupTypeNameAndLookupCodeName(String lookupTypeName, String lookupCodeName);
    List<LookupCode> findByLookupTypeName(String lookupTypeName);
    void deleteByLookupTypeName (String lookupTypeName );

}
