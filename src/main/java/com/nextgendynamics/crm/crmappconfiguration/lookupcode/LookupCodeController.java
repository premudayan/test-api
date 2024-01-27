package com.nextgendynamics.crm.crmappconfiguration.lookupcode;

import com.nextgendynamics.crm.crmappconfiguration.lookuptype.LookupTypeService;
import com.nextgendynamics.crm.exception.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/lookupcode")
public class LookupCodeController {
    private final LookupCodeService lookupCodeService;
    private final LookupTypeService lookupTypeService;

    public LookupCodeController(LookupCodeService lookupCodeService, LookupTypeService lookupTypeService) {
        this.lookupCodeService = lookupCodeService;
        this.lookupTypeService = lookupTypeService;
    }
    @GetMapping
    public ResponseEntity<List<LookupCode>> findAllLookupCode(){
        List<LookupCode> lookupCodes =  lookupCodeService.findAll();
        if ( lookupCodes != null ){
            return new ResponseEntity<>(lookupCodes, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LookupCode> findLookupCodeById(@PathVariable Long id){
        LookupCode lookupCode =  lookupCodeService.findById(id);
        if ( lookupCode != null ){
            return new ResponseEntity<>(lookupCode, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/findByType")
    public ResponseEntity<?> findLookupCodeByLookupTypeName(
            @RequestParam(required = false) Long lookupTypeId,
            @RequestParam(required = false) String lookupTypeName
            ){
        List<LookupCode> lookupCodes;
        String errorMessage= null;

        if (lookupTypeId == null && lookupTypeName == null) {
            errorMessage = "Please provide either 'lookupTypeId' and/or 'lookupTypeName' parameters";
            return new ResponseEntity<>(errorMessage,  HttpStatus.BAD_REQUEST);
        } else if ( lookupTypeId != null ){
            lookupCodes =  lookupCodeService.findByLookupTypeId(lookupTypeId);
        } else if ( lookupTypeName != null){
            lookupCodes =  lookupCodeService.findByLookupTypeName(lookupTypeName) ;
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if ( lookupCodes != null ){
            return new ResponseEntity<>(lookupCodes, HttpStatus.OK);
        } else {
            errorMessage = "No data found for the given criteria.";
            return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findByTypeAndCode")
    public ResponseEntity<LookupCode> findLookupCodeByTypeNameAndCodeName(
            @RequestParam String lookupTypeName,
            @RequestParam String lookupCodeName
    ){
        System.out.println("Controller >>GET LookupCode for lookupTypeName:"+ lookupTypeName + " lookupCodeName:"+ lookupCodeName);
        LookupCode lookupCode =  lookupCodeService.findByTypeAndCode(lookupTypeName, lookupCodeName);
        if ( lookupCode != null ){
            return new ResponseEntity<>(lookupCode, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createLookupCode( @RequestBody LookupCode lookupCode) {
        try{
            System.out.println("Controller >>POST LookupCode for lookupTypeName:"+ lookupCode.getLookupTypeName() + " lookupCodeName:"+ lookupCode.getLookupCodeName());
            LookupCode savedLookupCode =  lookupCodeService.createLookupCode(lookupCode);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedLookupCode.getId()).toUri();
            return ResponseEntity.created(location).build();
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>("Duplicate key violation: The lookup type/code combination already exists.", HttpStatus.CONFLICT);
        } catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addAll")
    public ResponseEntity<String> createLookupCodes( @RequestBody List<LookupCode> lookupCodes) {
        try{
            List<LookupCode> savedLookupCodes =  lookupCodeService.createLookupCodes(lookupCodes);
            return ResponseEntity.ok("Added the codes");
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>("Duplicate key violation: The lookup type/code combination already exists.", HttpStatus.CONFLICT);
        } catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateLookupCode(@PathVariable Long id, @RequestBody LookupCode lookupCode ) {
        try {
            LookupCode savedLookupCode = lookupCodeService.updateLookupCode(id, lookupCode);
            if ( lookupCode != null ){
                return new ResponseEntity<>( "Lookup Code updated successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }  catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLookupCode(@PathVariable Long id){
        LookupCode lookupCode = lookupCodeService.deleteLookupCode(id);
        if ( lookupCode != null ){
            return new ResponseEntity<>("Deleted successfully", HttpStatus.OK );
        } else {
            return new ResponseEntity<>("Unable to delete lookup code id "+ id, HttpStatus.NOT_FOUND );
        }
    }

    @DeleteMapping("/deleteByTypeAndCode")
    public ResponseEntity<?> deleteLookupCodeByTypeNameAndCodeName(@RequestParam String  lookupTypeName, @RequestParam String  lookupCodeName){
        System.out.println("Controller >>DELETE LookupCode for lookupTypeName:"+lookupTypeName + " lookupCodeName:"+ lookupCodeName );

        LookupCode lookupCode = lookupCodeService.deleteByTypeAndCode(lookupTypeName, lookupCodeName);
        if ( lookupCode != null){
            return new ResponseEntity<>("Deleted lookup code " + lookupCodeName + " for type " + lookupTypeName , HttpStatus.OK );
        } else {
            return new ResponseEntity<>("Delete failed - lookup code " + lookupCodeName + " - type " + lookupTypeName, HttpStatus.NOT_FOUND);
        }
    }
}
