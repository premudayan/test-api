package com.nextgendynamics.crm.crmappconfiguration.lookuptype;

import com.nextgendynamics.crm.exception.CustomException;
import com.nextgendynamics.crm.exception.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LookupTypeServiceImpl implements LookupTypeService {

    @Autowired
    private LookupTypeRepository lookupTypeRepository;

    @Override
    public List<LookupType> findAll() {
        return lookupTypeRepository.findAll();
    }

    @Override
    public LookupType findById(Long lookupTypeId) {
        return lookupTypeRepository.findById(lookupTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Lookup Type record not found with id " + lookupTypeId  ));
    }

    @Override
    public LookupType findByTypeName(String lookupTypeName) {
        return lookupTypeRepository.findByLookupTypeName(lookupTypeName.toUpperCase().trim());
    }

    @Override
    public LookupType createLookupType(LookupType lookupType) {
        try {
            if ( lookupType.getLookupTypeName().toUpperCase().trim() != null ){
                LookupType existingLookupType = lookupTypeRepository.findByLookupTypeName(lookupType.getLookupTypeName().toUpperCase().trim());
                if ( existingLookupType != null ){
                    throw new DuplicateKeyException("Duplicate key violation: The lookup type already exists.");
                }
                lookupType.setIsActive(true);
                lookupType.setLookupTypeName(lookupType.getLookupTypeName().toUpperCase().trim());
                lookupType.setDescription(lookupType.getDescription().trim());
                LookupType savedLookupType =  lookupTypeRepository.save(lookupType);
                return savedLookupType;
            } else {
                throw  new CustomException( "Lookup Type Name cannot be blank");
            }
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateKeyException("Duplicate key violation: The lookup type already exists.");
        } catch (Exception e){
            System.out.println(e.getMessage().toString());
            throw  new RuntimeException( "An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public LookupType updateLookupType(Long lookupTypeId, LookupType updatedLookupType) {
        try {
            LookupType existingLookupType = lookupTypeRepository.findById(lookupTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lookup Type record not found with id " + lookupTypeId  ));
            if ( existingLookupType != null ){
                if ( updatedLookupType.getDescription() != null)
                    existingLookupType.setDescription(updatedLookupType.getDescription().trim());
                if ( updatedLookupType.getIsActive() != null)
                    existingLookupType.setIsActive(updatedLookupType.getIsActive());
                if ( updatedLookupType.getCustomColumn1() != null)
                    existingLookupType.setCustomColumn1(updatedLookupType.getCustomColumn1());
                if ( updatedLookupType.getCustomColumn2() != null)
                    existingLookupType.setCustomColumn2(updatedLookupType.getCustomColumn2());
                if ( updatedLookupType.getCustomColumn3() != null)
                    existingLookupType.setCustomColumn3(updatedLookupType.getCustomColumn3());

                LookupType savedLookupType = lookupTypeRepository.save(existingLookupType);
                return savedLookupType;
            }
            throw  new CustomException( "Unable to locate Lookup Type for id " + lookupTypeId );
        } catch (Exception e){
            System.out.println(e.getMessage().toString());
            throw  new RuntimeException( "An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public String deleteLookupType(Long lookupTypeId) {
        try {
            if ( lookupTypeRepository.existsById(lookupTypeId) ) {
                lookupTypeRepository.deleteById(lookupTypeId);
                return "success";
            } else {
                throw new CustomException( "Delete failed. Lookup Type with id "+ lookupTypeId + " does not exist." );
            }
        } catch (Exception e){
            throw  new RuntimeException( "Error: " + e.getMessage());
        }
    }
}