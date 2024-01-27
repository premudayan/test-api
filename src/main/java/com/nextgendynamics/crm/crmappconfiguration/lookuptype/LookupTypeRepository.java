package com.nextgendynamics.crm.crmappconfiguration.lookuptype;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LookupTypeRepository extends JpaRepository<LookupType, Long> {
    LookupType findByLookupTypeName(String lookupName);
}
