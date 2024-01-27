package com.nextgendynamics.crm.contact;

import com.nextgendynamics.crm.contactaddress.ContactAddress;
import com.nextgendynamics.crm.contactemail.ContactEmail;
import com.nextgendynamics.crm.contactphone.ContactPhone;
import com.nextgendynamics.crm.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Join;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService{

    @Autowired
    private  ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public List<Contact> findAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public Page<Contact> findAllContactsWithSearchFilters(ContactSearchCriteria searchCriteria,
                                                          int pageNumber, int pageSize, String sortString) {
        System.out.println("First:" + searchCriteria.getFirstName() );
        System.out.println("Last:" + searchCriteria.getLastName() );

        Specification<Contact> filters = getFilters( searchCriteria );
        pageNumber = (pageNumber <= 0) ? 0 : pageNumber ;
        Map<String, Sort.Direction> sorting = parseSortString(sortString);
        Sort sort = buildSort(sorting);
        Page<Contact> contacts = contactRepository.findAll(filters, PageRequest.of(pageNumber,pageSize, sort));
        return contacts;
    }

    private Specification<Contact> getFilters( ContactSearchCriteria searchCriteria ) {
        Specification<Contact> filters = Specification.where(
                likeIgnoreCase("firstName", searchCriteria.getFirstName()))
                .and(likeIgnoreCase("lastName", searchCriteria.getLastName()))
                .and(likeIgnoreCase("middleName", searchCriteria.getMiddleName()))
                .and(likeIgnoreCase("gender", searchCriteria.getGender()))
                .and(likeIgnoreCase("customerId", searchCriteria.getCustomerId()))
                .and(likeIgnoreCase("jobTitle", searchCriteria.getJobTitle()))
                .and(likeIgnoreCase("company", searchCriteria.getCompany()))
                .and(likeIgnoreCase("contactType", searchCriteria.getContactType()))
                .and(likeIgnoreCase("ssn", searchCriteria.getSsn()))
                .and(likeIgnoreCase("industry", searchCriteria.getIndustry()))
                .and(likeIgnoreCase("employeeId", searchCriteria.getEmployeeId()))
                .and(likeIgnoreCase("department", searchCriteria.getDepartment()))
                .and(likeIgnoreCase("race", searchCriteria.getRace()))
                .and(likeIgnoreCase("preferredContactMethod", searchCriteria.getPreferredContactMethod()))
                .and(likeIgnoreCase("statusCode", searchCriteria.getStatusCode()))
                .and(likeIgnoreCase("preferredLanguage", searchCriteria.getPreferredLanguage()))
                .and(likeIgnoreCase("languagesSpoken", searchCriteria.getLanguagesSpoken()))
                .and(likeIgnoreCase("university", searchCriteria.getUniversity()))

                .and(likeDate("dateOfBirth", searchCriteria.getDateOfBirth()))
                .and(likeDate("dateOfBirthStart", searchCriteria.getDateOfBirthStart()))
                .and(likeDate("dateOfBirthEnd", searchCriteria.getDateOfBirthEnd()))
                .and(likeDate("customerSince", searchCriteria.getCustomerSince()))
                .and(likeDate("customerSinceStart", searchCriteria.getCustomerSinceStart()))
                .and(likeDate("customerSinceEnd", searchCriteria.getCustomerSinceEnd()))

                .and(likeIgnoreCaseForEmail("emailAddress", searchCriteria.getEmailAddress()))
                .and(likeIgnoreCaseForEmail("emailType", searchCriteria.getEmailType()))
                .and(likeIgnoreCaseForPhone("phoneNumber", searchCriteria.getPhoneNumber()))
                .and(likeIgnoreCaseForPhone("phoneType", searchCriteria.getPhoneType()))
                .and(likeIgnoreCaseForAddress("addressType", searchCriteria.getAddressType()))
                .and(likeIgnoreCaseForAddress("streetAddress", searchCriteria.getStreetAddress()))
                .and(likeIgnoreCaseForAddress("city", searchCriteria.getCity()))
                .and(likeIgnoreCaseForAddress("state", searchCriteria.getState()))
                .and(likeIgnoreCaseForAddress("zipCode", searchCriteria.getZipCode()))
                .and(likeIgnoreCaseForAddress("postalCode", searchCriteria.getPostalCode()))
                .and(likeIgnoreCaseForAddress("country", searchCriteria.getCountry()))
                ;
        return  filters;
    }

    private Specification<Contact> likeDate(String attribute, LocalDate value) {

        if (Objects.isNull(value) )
            return null;

        System.out.println("Attribute:" + attribute + " Value:"+ value);

        if (  attribute.endsWith("Start") ) {
            return  (root, query, builder) -> builder.greaterThanOrEqualTo(root.get(attribute.replace("Start", "")), value );
        } else if (  attribute.endsWith("End") ){
            return (root, query, builder) -> builder.lessThanOrEqualTo(root.get(attribute.replace("End", "")), value );
        } else {
            return (root, query, builder) -> builder.equal(root.get(attribute), value );
        }
    }

    private Specification<Contact> likeIgnoreCase(String attribute, String value) {
        final String cleanedValue = (StringUtils.isBlank(value) || value.replace('*', '%').trim().equals("%")) ? null :
                value.replace('*', '%');
        if (Objects.isNull(cleanedValue) )
            return null;
        System.out.println("Attribute:" + attribute + " Value:"+ value);

        if (  cleanedValue.contains("%") ) {
            return StringUtils.isBlank(cleanedValue) ? null :
                (root, query, builder) -> builder.like(builder.lower(root.get(attribute)), cleanedValue.toLowerCase() );
        } else {
            return StringUtils.isBlank(cleanedValue) ? null :
                    (root, query, builder) -> builder.equal(builder.lower(root.get(attribute)), cleanedValue.toLowerCase() );
        }
    }

    private Specification<Contact> likeIgnoreCaseForEmail(String attribute, String value) {
        final String cleanedValue = (StringUtils.isBlank(value) || value.replace('*', '%').trim().equals("%")) ? null :
                value.replace('*', '%');
        if (Objects.isNull(cleanedValue) )
            return null;

        if ( cleanedValue.contains("%") ) {
            return StringUtils.isBlank(cleanedValue) ? null :
                    (root, query, builder) -> {
                            Join<ContactEmail, Contact> contactEmailContactJoin = root.join("contactEmails");
                            return builder.like(builder.lower(contactEmailContactJoin.get(attribute)), cleanedValue.toLowerCase() );
                            };
        } else {
            return StringUtils.isBlank(cleanedValue) ? null :
                    (root, query, builder) -> {
                            Join<ContactEmail, Contact> contactEmailContactJoin = root.join("contactEmails");
                            return builder.equal(builder.lower(contactEmailContactJoin.get(attribute)), cleanedValue.toLowerCase() );
                        };
        }
    }

    private Specification<Contact> likeIgnoreCaseForPhone(String attribute, String value) {
        final String cleanedValue = (StringUtils.isBlank(value) || value.replace('*', '%').trim().equals("%")) ? null :
                value.replace('*', '%');
        if (Objects.isNull(cleanedValue) )
            return null;

        if ( cleanedValue.contains("%") ) {
            return StringUtils.isBlank(cleanedValue) ? null :
                    (root, query, builder) -> {
                        Join<ContactPhone, Contact> contactPhoneContactJoin = root.join("contactPhones");
                        return builder.like(builder.lower(contactPhoneContactJoin.get(attribute)), cleanedValue.toLowerCase() );
                    };
        } else {
            return StringUtils.isBlank(cleanedValue) ? null :
                    (root, query, builder) -> {
                        Join<ContactPhone, Contact> contactPhoneContactJoin = root.join("contactPhones");
                        return builder.equal(builder.lower(contactPhoneContactJoin.get(attribute)), cleanedValue.toLowerCase() );
                    };
        }
    }

    private Specification<Contact> likeIgnoreCaseForAddress(String attribute, String value) {
        final String cleanedValue = (StringUtils.isBlank(value) || value.replace('*', '%').trim().equals("%")) ? null :
                value.replace('*', '%');
        if (Objects.isNull(cleanedValue) )
            return null;

        if ( cleanedValue.contains("%") ) {
            return StringUtils.isBlank(cleanedValue) ? null :
                    (root, query, builder) -> {
                        Join<ContactAddress, Contact> contactAddressContactJoin = root.join("contactAddresses");
                        return builder.like(builder.lower(contactAddressContactJoin.get(attribute)), cleanedValue.toLowerCase() );
                    };
        } else {
            return StringUtils.isBlank(cleanedValue) ? null :
                    (root, query, builder) -> {
                        Join<ContactAddress, Contact> contactAddressContactJoin = root.join("contactAddresses");
                        return builder.equal(builder.lower(contactAddressContactJoin.get(attribute)), cleanedValue.toLowerCase() );
                    };
        }
    }

    private Map<String, Sort.Direction> parseSortString(String sortString) {
        Map<String, Sort.Direction> sorting = new HashMap<>();
        if (sortString != null && !sortString.isEmpty()) {
            List<String> sortEntries = Arrays.asList(sortString.split(","));
            sorting = sortEntries.stream()
                    .map(entry -> entry.trim().split("\\s+"))
                    .collect(Collectors.toMap(
                            parts -> parts[0],
                            parts -> parts.length > 1 && parts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC
                    ));
        }
        return sorting;
    }

    private Sort buildSort(Map<String, Sort.Direction> sorting) {
        Sort.Order[] orders = sorting.entrySet().stream()
                .map(entry -> new Sort.Order(entry.getValue(), entry.getKey()))
                .toArray(Sort.Order[]::new);
        return Sort.by(orders);
    }

    @Override
    public Contact findContactById(Long id) {
        return contactRepository.findById(id).orElse(null);
    }

    @Override
    public boolean updateContact(Long id, Contact updatedContact) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + id));

        // Ignore null properties from the updatedContact while copying
        BeanUtils.copyProperties(updatedContact, existingContact, getNullPropertyNames(updatedContact) );
        contactRepository.save(existingContact);
        return true;
    }

    // Get property names with null values from the updated object
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    @Override
    public boolean updateAllContacts(Contact contact) {
        List<Contact> contacts = contactRepository.findAll();
        for (Contact existingContact : contacts) {
            BeanUtils.copyProperties(contact, existingContact, getNullPropertyNames(contact));
        }
        contactRepository.saveAll(contacts);
        return true;
    }

    @Override
    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public boolean deleteContactById(Long id) {
        if ( contactRepository.existsById(id)){
            contactRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
