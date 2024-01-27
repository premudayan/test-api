package com.nextgendynamics.crm.crmappconfiguration.lookuptype;

import java.util.List;

public interface LookupTypeService {

    List<LookupType> findAll();
    LookupType findById(Long lookupTypeId);
    LookupType findByTypeName(String lookupTypeName);
    LookupType createLookupType(LookupType lookupType);
    LookupType updateLookupType(Long lookupTypeId, LookupType lookupType);
    String deleteLookupType(Long lookupTypeId);

}
