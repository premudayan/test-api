package com.nextgendynamics.crm.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/contacts")
public class ContactController {
    private final ContactService contactService;
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts(){
        return new ResponseEntity<>(contactService.findAllContacts(), HttpStatus.OK );
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Contact>> findContactsWithSearchFilters(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String contactType,
            @RequestParam(required = false) String ssn,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String race,
            @RequestParam(required = false) String preferredContactMethod,
            @RequestParam(required = false) String statusCode,
            @RequestParam(required = false) String preferredLanguage,
            @RequestParam(required = false) String languagesSpoken,
            @RequestParam(required = false) String university,
            @RequestParam(required = false) String emailAddress,
            @RequestParam(required = false) String emailType,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String phoneType,
            @RequestParam(required = false) String addressType,
            @RequestParam(required = false) String streetAddress,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String zipCode,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(required = false) LocalDate dateOfBirthStart,
            @RequestParam(required = false) LocalDate dateOfBirthEnd,
            @RequestParam(required = false) LocalDate customerSince,
            @RequestParam(required = false) LocalDate customerSinceStart,
            @RequestParam(required = false) LocalDate customerSinceEnd,
            @RequestParam (required = false, defaultValue = "0") int pageNumber,
            @RequestParam (required = false, defaultValue = "10") int pageSize,
            @RequestParam (required = false, defaultValue = "id") String sortString  ){
        System.out.println("Controller First:" + firstName );
        System.out.println("Controller Last:" + lastName );
        ContactSearchCriteria searchCriteria = new ContactSearchCriteria();
        searchCriteria.setFirstName(StringUtils.isBlank(firstName)? null : firstName);
        searchCriteria.setLastName(StringUtils.isBlank(lastName)? null : lastName);
        searchCriteria.setMiddleName(StringUtils.isBlank(middleName)? null : middleName);
        searchCriteria.setGender(StringUtils.isBlank(gender)? null : gender);
        searchCriteria.setCustomerId(StringUtils.isBlank(customerId)? null : customerId);
        searchCriteria.setJobTitle(StringUtils.isBlank(jobTitle)? null : jobTitle);
        searchCriteria.setCompany(StringUtils.isBlank(company)? null : company);
        searchCriteria.setContactType(StringUtils.isBlank(contactType)? null : contactType);
        searchCriteria.setSsn(StringUtils.isBlank(ssn)? null : ssn);
        searchCriteria.setIndustry(StringUtils.isBlank(industry)? null : industry);
        searchCriteria.setEmployeeId(StringUtils.isBlank(employeeId)? null : employeeId);
        searchCriteria.setDepartment(StringUtils.isBlank(department)? null : department);
        searchCriteria.setRace(StringUtils.isBlank(race)? null : race);
        searchCriteria.setPreferredContactMethod(StringUtils.isBlank(preferredContactMethod)? null : preferredContactMethod);
        searchCriteria.setStatusCode(StringUtils.isBlank(statusCode)? null : statusCode);
        searchCriteria.setPreferredLanguage(StringUtils.isBlank(preferredLanguage)? null : preferredLanguage);
        searchCriteria.setLanguagesSpoken(StringUtils.isBlank(languagesSpoken)? null : languagesSpoken);
        searchCriteria.setUniversity(StringUtils.isBlank(university)? null : university);
        searchCriteria.setEmailAddress(StringUtils.isBlank(emailAddress)? null : emailAddress);
        searchCriteria.setEmailType(StringUtils.isBlank(emailType)? null : emailType);
        searchCriteria.setPhoneNumber(StringUtils.isBlank(phoneNumber)? null : phoneNumber);
        searchCriteria.setPhoneType(StringUtils.isBlank(phoneType)? null : phoneType);
        searchCriteria.setAddressType(StringUtils.isBlank(addressType)? null : addressType);
        searchCriteria.setStreetAddress(StringUtils.isBlank(streetAddress)? null : streetAddress);
        searchCriteria.setCity(StringUtils.isBlank(city)? null : city);
        searchCriteria.setState(StringUtils.isBlank(state)? null : state);
        searchCriteria.setZipCode(StringUtils.isBlank(zipCode)? null : zipCode);
        searchCriteria.setPostalCode(StringUtils.isBlank(postalCode)? null : postalCode);
        searchCriteria.setCountry(StringUtils.isBlank(country)? null : country);
        searchCriteria.setDateOfBirth(dateOfBirth == null ? null : dateOfBirth);
        searchCriteria.setDateOfBirthStart(dateOfBirthStart == null ? null : dateOfBirthStart);
        searchCriteria.setDateOfBirthEnd(dateOfBirthEnd == null ? null : dateOfBirthEnd);
        searchCriteria.setCustomerSince( customerSince == null ? null : customerSince);
        searchCriteria.setCustomerSinceStart( customerSinceStart == null ? null : customerSinceStart);
        searchCriteria.setCustomerSinceEnd( customerSinceEnd == null ? null : customerSinceEnd);

        System.out.println("Controller2 First:" + firstName );
        System.out.println("Controller2 Last:" + lastName );

        Page<Contact> contacts = contactService.findAllContactsWithSearchFilters(searchCriteria, pageNumber,pageSize, sortString);
        return new ResponseEntity<>(contacts, HttpStatus.OK );
    }

    @PostMapping("/complexsearch")
    public ResponseEntity<Page<Contact>> findContactsWithSearchFilters(
            @RequestParam (required = false, defaultValue = "0") int pageNumber,
            @RequestParam (required = false, defaultValue = "10") int pageSize,
            @RequestParam (required = false, defaultValue = "id") String sortString,
            @RequestBody ContactSearchCriteria searchCriteria ){

        Page<Contact> contacts = contactService.findAllContactsWithSearchFilters(searchCriteria, pageNumber,pageSize, sortString);
        return new ResponseEntity<>(contacts, HttpStatus.OK );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        //Predicate<? super Contact> predicate = contact -> contact.getId().equals(id);
        Contact contact = contactService.findContactById(id);
        if (contact == null ){
            throw new ContactNotFoundException("id:"+id);
        }
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    @GetMapping("/limited/{id}")
    public ResponseEntity<Contact> getContactByIdLimited(@PathVariable Long id) {
        //Predicate<? super Contact> predicate = contact -> contact.getId().equals(id);
        Contact contact = contactService.findContactById(id);
        if (contact == null ){
            throw new ContactNotFoundException("id:"+id);
        }
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

/*
    private MappingJacksonValue includeFields(Contact contact, String[] incFields){
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(contact);
        SimpleBeanPropertyFilter filter;

        filter = SimpleBeanPropertyFilter.filterOutAllExcept(incFields);
        FilterProvider filters = new SimpleFilterProvider().addFilter("ContactFilter", filter);
        mappingJacksonValue.setFilters(filters);
        return mappingJacksonValue;
    }

    private MappingJacksonValue excludeFields(Contact contact, String[] exFields) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("ContactFilter",
                SimpleBeanPropertyFilter.serializeAllExcept(exFields));

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(contact);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }

    private MappingJacksonValue excludeFields(EntityModel em, String[] exFields) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("ContactFilter",
                SimpleBeanPropertyFilter.serializeAllExcept(exFields));

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(em);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }


    private MappingJacksonValue excludeFields(List<Contact> contacts, String[] exFields) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("ContactFilter",
                SimpleBeanPropertyFilter.serializeAllExcept(exFields));

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(contacts);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }
*/

    @PostMapping
    public ResponseEntity<String> addContact( @RequestBody Contact contact){
        try{
            Contact savedContact = contactService.createContact(contact);
            URI location = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(savedContact.getId()).toUri();
            return ResponseEntity.created(location).build();
            //return new ResponseEntity<>("Contact added successfully", HttpStatus.CREATED);

        } catch ( Exception e){
            System.out.println("calling addContact **************- Error:" + e.getMessage());
            return new ResponseEntity<>("Unable to add contact. Error: " + e.getMessage(), HttpStatus.EXPECTATION_FAILED );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContact(@PathVariable Long id, @RequestBody Contact updatedContact) {
        try {
            contactService.updateContact(id, updatedContact);
            return new ResponseEntity<>("Contact updated successfully", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Unable to update contact. Error: " + e.getMessage(), HttpStatus.EXPECTATION_FAILED );
        }

    }

    @PutMapping("/updateAll")
    public ResponseEntity<String>  updateAllContacts(@RequestBody Contact updatedContact){
        System.out.println("Rating:" + updatedContact.getContactRating());
        System.out.println("Contact Type:" + updatedContact.getContactType());

        if(  contactService.updateAllContacts( updatedContact) ) {
            return new ResponseEntity<>("Contact updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unable to update contact ", HttpStatus.EXPECTATION_FAILED );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable Long id){
        boolean isDeleted = contactService.deleteContactById(id);
        if(isDeleted)
            return new ResponseEntity<>("Contact deleted successfully", HttpStatus.OK);
        return new ResponseEntity<>("Contact record not found", HttpStatus.NOT_FOUND);
    }

}
