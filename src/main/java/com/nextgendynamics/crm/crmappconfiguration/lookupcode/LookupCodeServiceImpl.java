package com.nextgendynamics.crm.crmappconfiguration.lookupcode;

import com.nextgendynamics.crm.crmappconfiguration.lookuptype.LookupType;
import com.nextgendynamics.crm.crmappconfiguration.lookuptype.LookupTypeRepository;
import com.nextgendynamics.crm.exception.CustomException;
import com.nextgendynamics.crm.exception.DuplicateKeyException;
import com.nextgendynamics.crm.exception.ResourceNotFoundException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames={"lookupCache"})
public class LookupCodeServiceImpl implements LookupCodeService{

    private final CacheManager cacheManager;
    @Autowired
    private LookupCodeRepository lookupCodeRepository;
    @Autowired
    private LookupTypeRepository lookupTypeRepository;

    public LookupCodeServiceImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public List<LookupCode> findAll() {
        return lookupCodeRepository.findAll();
    }

    @Override
    public LookupCode findById(Long id) {
        return lookupCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lookup Code record not found with id " + id  ));
    }

    @Override
    public List<LookupCode> findByLookupTypeName(String lookupTypeName) {
        LookupType lookupType = lookupTypeRepository.findByLookupTypeName(lookupTypeName);
        return lookupType.getLookupCodes();
    }

    @Override
    public List<LookupCode> findByLookupTypeId(Long lookupTypeId) {
        LookupType lookupType = lookupTypeRepository.findById(lookupTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Lookup Code record not found with Lookup Type id " + lookupTypeId ));
        return lookupType.getLookupCodes();
    }

    @Override
    @Cacheable( key="#lookupTypeName +':'+ #lookupCodeName" )
    public LookupCode findByTypeAndCode(String lookupTypeName, String lookupCodeName) {
        //(value="lookupCache", key = "#lookupTypeName +':'+ #lookupCodeName" )
        System.out.println("Service >>Calling findByTypeAndCode - lookupTypeName:"+ lookupTypeName + " lookupCodeName:"+ lookupCodeName);
        LookupCode lookupCode = lookupCodeRepository.findByLookupTypeNameAndLookupCodeName(
                lookupTypeName.toUpperCase().trim(), lookupCodeName.toUpperCase().trim() );
        if (lookupCode != null ) {
            return lookupCode;
        } else {
            throw new CustomException( "Lookup Code "+ lookupTypeName + " > " +lookupCodeName +" does not exist." );
        }
    }

    @Override
    public LookupCode createLookupCode(LookupCode lookupCode) {
        try {
            if ( lookupCode.getLookupCodeName() != null
                    && lookupCode.getLookupTypeName() != null
            ) {
                LookupCode existingLookupCode = lookupCodeRepository.findByLookupTypeNameAndLookupCodeName(
                        lookupCode.getLookupTypeName().toUpperCase().trim(),
                        lookupCode.getLookupCodeName().toUpperCase().trim() );
                if ( existingLookupCode != null ){
                    throw new DuplicateKeyException("Duplicate key violation: The lookup type/code combination already exists.");
                }

                if ( lookupCode.getIsDefault() == null)
                    lookupCode.setIsDefault(false);
                System.out.println("Service >>Calling createLookupCode - lookupTypeName:"+ lookupCode.getLookupTypeName() + " lookupCodeName:"+ lookupCode.getLookupCodeName());
                LookupType lookupType = lookupTypeRepository.findByLookupTypeName(lookupCode.getLookupTypeName().toUpperCase().trim());
                lookupCode.setLookupType(lookupType);
                lookupCode.setLookupValue(lookupCode.getLookupValue().toUpperCase().trim());
                lookupCode.setIsActive(true);
                LookupCode savedlookupCode = lookupCodeRepository.save(lookupCode);
                return savedlookupCode;
            }
            throw new CustomException("Lookup Type Name and/or Code Name cannot be null.");
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateKeyException("Duplicate key violation: The lookup type/code combination already exists.");
        } catch (Exception e){
            System.out.println(e.getMessage().toString());
            throw  new RuntimeException( "An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public List<LookupCode> createLookupCodes(List<LookupCode> lookupCodes) {
        List<LookupCode> savedLookupCodes = new ArrayList<>();
        for (LookupCode lookupCode : lookupCodes) {
            LookupCode lookupCode1 = createLookupCode( lookupCode );
            savedLookupCodes.add(lookupCode1);
        }
        return savedLookupCodes;
    }

    @Override
    public LookupCode updateLookupCode(Long id, LookupCode updatedLookupCode) {
        LookupCode saved;
        try {
            LookupCode existingLookupCode = lookupCodeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Lookup Code record not found with Lookup Type id " + id ));
             if ( existingLookupCode != null ){
                if ( updatedLookupCode.getLookupValue() != null)
                    existingLookupCode.setLookupValue(updatedLookupCode.getLookupValue().toUpperCase().trim());
                if ( updatedLookupCode.getDescription() != null)
                    existingLookupCode.setDescription(updatedLookupCode.getDescription().trim());
                if ( updatedLookupCode.getSortOrder() > 0 )
                    existingLookupCode.setSortOrder(updatedLookupCode.getSortOrder());
                if ( updatedLookupCode.getIsActive() != null)
                    existingLookupCode.setIsActive(updatedLookupCode.getIsActive());
                if ( updatedLookupCode.getIsDefault() != null)
                    existingLookupCode.setIsDefault(updatedLookupCode.getIsDefault());
                if ( updatedLookupCode.getCustomColumn1() != null)
                    existingLookupCode.setCustomColumn1(updatedLookupCode.getCustomColumn1().trim());
                if ( updatedLookupCode.getCustomColumn2() != null)
                    existingLookupCode.setCustomColumn2(updatedLookupCode.getCustomColumn2().trim());
                if ( updatedLookupCode.getCustomColumn3() != null)
                    existingLookupCode.setCustomColumn3(updatedLookupCode.getCustomColumn3().trim());
                saved = lookupCodeRepository.save(existingLookupCode);
                evictCache(saved.getLookupTypeName() +":"+ saved.getLookupCodeName());
                return saved;
            }
            throw  new CustomException( "Unable to locate Lookup Code for id " + id );
        } catch (Exception e){
            throw  new RuntimeException( "Error in updateLookupCode(): " + e.getMessage());
        }
    }

    private void evictCache(String cacheKey) {
        cacheManager.getCache("lookupCache").evict(cacheKey);

    }

    @Override
    public LookupCode deleteLookupCode(Long id) {
        //(value = "lookupCacheeee", key = "#lookupCode.lookupTypeName +':'+ #lookupCode.lookupCodeName")
        try {
            LookupCode lookupCode = lookupCodeRepository.findById(id).orElse(null);
            if ( lookupCode != null ){
                lookupCodeRepository.deleteById(id);
                System.out.println("deleteing from db and cache ...");
                evictCache(lookupCode.getLookupTypeName() +":"+ lookupCode.getLookupCodeName());
                return lookupCode;
            } else {
                throw new CustomException( "Delete failed. Lookup Code with id "+ id + " does not exist." );
            }
        } catch (Exception e){
            throw  new RuntimeException( "Error: " + e.getMessage());
        }
    }

    @Override
    public LookupCode deleteByTypeAndCode(String lookupTypeName, String lookupCodeName) {
        //(value = "lookupCache", key = "#lookupCode.lookupTypeName +':'+ #lookupCode.lookupCodeName")
        try {
            System.out.println("Service >>Calling deleteByTypeAndCode - lookupTypeName:"+ lookupTypeName + " lookupCodeName:"+ lookupCodeName);
            LookupCode lookupCode = lookupCodeRepository.findByLookupTypeNameAndLookupCodeName(lookupTypeName.toUpperCase(), lookupCodeName.toUpperCase());
            if ( lookupCode != null ){
                System.out.println("ID:" + lookupCode.getId() + " Code:"+ lookupCode.getLookupCodeName());
                lookupCodeRepository.deleteById(lookupCode.getId());
                System.out.println("Deleteing from db and cache ...");
                evictCache(lookupCode.getLookupTypeName() +":"+ lookupCode.getLookupCodeName());
                return lookupCode;
            } else {
                throw new CustomException( "Delete failed. Lookup Code "+ lookupCodeName +" does not exist." );
            }
        } catch (Exception e){
            throw  new RuntimeException( "Error: " + e.getMessage());
        }
    }
}