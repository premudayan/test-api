package com.nextgendynamics.crm.crmappconfiguration.lookupcode;

import java.util.List;

public interface LookupCodeService {

    List<LookupCode> findAll();
    LookupCode findById(Long id);
    List<LookupCode> findByLookupTypeName(String lookupTypeName);
    List<LookupCode> findByLookupTypeId(Long lookupTypeId);
    LookupCode findByTypeAndCode(String lookupTypeName, String lookupCodeName);
    LookupCode updateLookupCode(Long lookupCodeId, LookupCode lookupCode);
    LookupCode createLookupCode(LookupCode lookupCode);
    List<LookupCode> createLookupCodes(List<LookupCode> lookupCodes);
    LookupCode deleteLookupCode(Long lookupCodeId);
    LookupCode deleteByTypeAndCode(String lookupTypeName, String lookupCodeName);
    
}
