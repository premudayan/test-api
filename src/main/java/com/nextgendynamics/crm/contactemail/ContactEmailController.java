package com.nextgendynamics.crm.contactemail;

import com.nextgendynamics.crm.exception.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/contactemails")
public class ContactEmailController {
     private final ContactEmailService contactEmailService;

    public ContactEmailController(ContactEmailService contactEmailService) {
        this.contactEmailService = contactEmailService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactEmail> getContactEmailById(
            @PathVariable Long id ){
        ContactEmail contactEmail = contactEmailService.findContactEmailById(id);
        if ( contactEmail!= null){
            return new ResponseEntity<>(contactEmail, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/contacts/{id}")
    public ResponseEntity<List<ContactEmail>> getAllContactEmailsByContact(
            @PathVariable Long id) {
        List<ContactEmail> contactEmails = contactEmailService.findAllContactEmailsByContactId(id);
        if (contactEmails.stream().count() > 0) {
            return new ResponseEntity<>(contactEmails, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> addContactEmail(
            @RequestParam(name = "contactId") Long contactId,
            @RequestBody ContactEmail contactEmail){
        try {
            ContactEmail savedContactEmail = contactEmailService.createContactEmail(contactId, contactEmail);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .replaceQueryParam("contactId")
                    .buildAndExpand(savedContactEmail.getId()).toUri();
            return ResponseEntity.created(location).build();
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>("Duplicate key violation: The email type " + contactEmail.getEmailType() +
                    " already exists for contact id "+ contactId, HttpStatus.CONFLICT);
        } catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/saveAll")
    public ResponseEntity<List<ContactEmail>> addAllContactEmail(
            @RequestParam(name = "contactId") Long contactId,
            @RequestBody List<ContactEmail> contactEmails){
        try {
            List<ContactEmail> savedEmails = contactEmailService.createContactEmail(contactId, contactEmails);
            return new ResponseEntity<>(savedEmails, HttpStatus.OK);

        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (RuntimeException e){
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<ContactEmail> updateContactEmail(
            @RequestParam Long contactId,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Boolean upsert,
            @RequestBody ContactEmail contactEmail){

        upsert = (upsert != null) ? upsert : false;
        ContactEmail updatedEmail = contactEmailService.updateContactEmail(contactId, id, upsert, contactEmail);
        if(updatedEmail != null)
            return new ResponseEntity<>(updatedEmail, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteById")
    public ResponseEntity<String> deleteContactEmail(@RequestParam Long id){
        String deleted = contactEmailService.deleteContactEmail(id, null, null);
        if ( deleted.equals("success")){
            return new ResponseEntity<>("Contact Email deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(deleted, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteByType")
    public ResponseEntity<String> deleteContactEmail(@RequestParam Long contactId,
                                                     @RequestParam String emailType){
        String deleted = contactEmailService.deleteContactEmail(null, contactId, emailType);
        if ( deleted.equals("success") ){
            return new ResponseEntity<>("Contact Email deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(deleted, HttpStatus.NOT_FOUND);
        }
    }
}
