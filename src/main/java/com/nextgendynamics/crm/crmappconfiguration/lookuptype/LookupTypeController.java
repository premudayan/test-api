package com.nextgendynamics.crm.crmappconfiguration.lookuptype;

import com.nextgendynamics.crm.exception.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/lookuptype")
public class LookupTypeController {
    private final LookupTypeService lookupTypeService;
    public LookupTypeController(LookupTypeService lookupTypeService) {
        this.lookupTypeService = lookupTypeService;
    }

    @GetMapping
    public ResponseEntity<List<LookupType>> getAllLookupTypes(){

        List<LookupType> lookupTypes = lookupTypeService.findAll();
        if( lookupTypes != null )
            return new ResponseEntity<>(lookupTypes, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LookupType> getLookupTypeById(@PathVariable Long id){

        LookupType lookupType = lookupTypeService.findById(id);
        if( lookupType != null )
            return new ResponseEntity<>(lookupType, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/findByName/{lookupTypeName}")
    public ResponseEntity<LookupType> getLookupTypeByName(@PathVariable String lookupTypeName){

        LookupType lookupType = lookupTypeService.findByTypeName(lookupTypeName);
        if( lookupType != null )
            return new ResponseEntity<>(lookupType, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createLookupType(@RequestBody LookupType lookupType){
        try {
            LookupType savedLookupType = lookupTypeService.createLookupType(lookupType);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedLookupType.getId()).toUri();
            return ResponseEntity.created(location).build();

        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>("Duplicate key violation: The lookup type already exists.", HttpStatus.CONFLICT);
        } catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateLookupType(@PathVariable Long id,  @RequestBody LookupType lookupType ){
        try {
            LookupType savedLookupType = lookupTypeService.updateLookupType(id, lookupType);
            if(  savedLookupType != null ) {
                return new ResponseEntity<>("Lookup Type updated successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Unable to update Lookup Type!", HttpStatus.EXPECTATION_FAILED );
            }
        }  catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLookupType(@PathVariable Long id ){
        try {
            lookupTypeService.deleteLookupType(id);
            return new ResponseEntity<>("Lookup Type with id " + id + " deleted successfully", HttpStatus.OK);
        }  catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }

    }

}
