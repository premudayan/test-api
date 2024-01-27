package com.nextgendynamics.crm.contactemail;

import com.nextgendynamics.crm.contact.Contact;
import com.nextgendynamics.crm.contact.ContactRepository;
import com.nextgendynamics.crm.exception.ResourceNotFoundException;
import com.nextgendynamics.crm.exception.CustomException;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ContactEmailServiceImpl implements ContactEmailService {
    private final Logger logger = LoggerFactory.getLogger(ContactEmailServiceImpl.class);
    private final ContactEmailRepository contactEmailRepository;
    private final ContactRepository contactRepository;
    @Autowired
    public ContactEmailServiceImpl(ContactEmailRepository contactEmailRepository, ContactRepository contactRepository) {
        this.contactEmailRepository = contactEmailRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public List<ContactEmail> findAllContactEmailsByContactId(Long contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));
        return  contact.getContactEmails();

    }

    @Override
    public ContactEmail findContactEmailById(Long id) {
        return contactEmailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email record not found with id: " + id));
    }

    @Override
    @Transactional
    public ContactEmail createContactEmail(Long contactId, ContactEmail contactEmail) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));
        validateEmail(contactEmail);
        checkDuplicateEmailType(contactId, contactEmail);

        try {
            contactEmail.setContact(contact);
            ContactEmail savedContactEmail = contactEmailRepository.save(contactEmail);
            updateOldPrimaryRecord( contact, savedContactEmail );

            return savedContactEmail;
        } catch (Exception e){
            logger.error("Error: {}", e.getMessage());
            throw  new RuntimeException( "An unexpected error occurred when creating the contact email. " + e.getMessage());
        }
    }

    @Override
    public List<ContactEmail> createContactEmail(Long contactId, List<ContactEmail> contactEmails) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));

        try {
            validateContactEmails(contactEmails);
            return  contactEmailRepository.saveAll(contactEmails);
        } catch (Exception e){
            logger.error("Error: {}", e.getMessage());
            throw  new RuntimeException( "An unexpected error occurred when creating the contact email. " + e.getMessage());
        }
    }

    private void validateEmail(ContactEmail contactEmail) {
        if (Objects.isNull(contactEmail.getEmailType().trim()) || Objects.isNull(contactEmail.getEmailAddress().trim())) {
            throw new CustomException("Email Type and Email Address cannot be blank");
        }
    }

    private void validateContactEmails(List<ContactEmail> contactEmails) {
        for (ContactEmail contactEmail : contactEmails) {
            if (StringUtils.isBlank(contactEmail.getEmailType()) || StringUtils.isBlank(contactEmail.getEmailAddress())) {
                throw new IllegalArgumentException("EmailType and EmailAddress cannot be blank");
            }
            // Add any other validation rules as needed
        }
    }
    private void checkDuplicateEmailType(Long contactId, ContactEmail contactEmail) {
        ContactEmail existingContactEmail = contactEmailRepository.findFirstByContactIdAndEmailType(
                contactId,
                contactEmail.getEmailType().trim().toUpperCase());

        if (Objects.nonNull(existingContactEmail)) {
            throw new CustomException("Duplicate key violation: The email type " + contactEmail.getEmailType() +
                    " already exists for contact id " + contactId);
        }
    }
    private void updateOldPrimaryRecord(Contact contact,ContactEmail savedContactEmail ) {
        if ( savedContactEmail.getIsPrimary() ){
            //update any other to false
            contactEmailRepository.updateNonPrimary(contact.getId(), savedContactEmail.getId() );
            contact.setPrimaryEmailId(savedContactEmail.getId());
            contactRepository.save(contact);
        }
    }

    @Override
    @Transactional
    public ContactEmail updateContactEmail(Long contactId, Long id, Boolean upsert, ContactEmail updatedContactEmail) {

        ContactEmail existingContactEmail = getExistingContactEmail(contactId, id, updatedContactEmail);
        if (Objects.nonNull(existingContactEmail)) {
            ContactEmail savedContactEmail = updateEmail( updatedContactEmail, existingContactEmail);
            return savedContactEmail;
        } else if ( upsert  && Objects.nonNull(updatedContactEmail.getEmailAddress())
                && Objects.nonNull(updatedContactEmail.getEmailType())  && Objects.nonNull( contactId )) {
            ContactEmail savedContactEmail = createContactEmail( contactId, updatedContactEmail);
            return savedContactEmail;
        }
        throw  new CustomException( "Unable to update Contact Email."  );
    }

    private ContactEmail getExistingContactEmail(Long contactId, Long id, ContactEmail updatedContactEmail) {
        ContactEmail existingContactEmail = (id != null) ?
                contactEmailRepository.findById(id).orElse(null) :
                (Objects.nonNull(updatedContactEmail.getId())) ?
                        contactEmailRepository.findById(updatedContactEmail.getId()).orElse(null) :
                (Objects.nonNull(updatedContactEmail.getEmailType()) && Objects.nonNull(contactId)) ?
                        contactEmailRepository.findFirstByContactIdAndEmailType(
                contactId, updatedContactEmail.getEmailType().toUpperCase()) : null;
        return existingContactEmail;
    }
    private ContactEmail updateEmail( ContactEmail updatedContactEmail, ContactEmail existingContactEmail) {
        // Update fields based on changes
        updateFields(existingContactEmail, updatedContactEmail);
        ContactEmail savedContactEmail = contactEmailRepository.save(existingContactEmail);
        updateOldPrimaryRecord( savedContactEmail.getContact(), savedContactEmail );

        return savedContactEmail;
    }

    private void updateFields(ContactEmail existingContactEmail, ContactEmail updatedContactEmail) {
        if ( Objects.nonNull(updatedContactEmail.getEmailAddress())) {
            existingContactEmail.setEmailAddress(updatedContactEmail.getEmailAddress());
        }
        if ( Objects.nonNull(updatedContactEmail.getIsPrimary())) {
            existingContactEmail.setIsPrimary(updatedContactEmail.getIsPrimary());
        }
        if ( Objects.nonNull(updatedContactEmail.getOptInStatus())) {
            existingContactEmail.setOptInStatus(updatedContactEmail.getOptInStatus());
        }
        if ( Objects.nonNull(updatedContactEmail.getIsVerified())) {
            existingContactEmail.setIsVerified(updatedContactEmail.getIsVerified());
            if ( updatedContactEmail.getIsVerified() ){
                existingContactEmail.setDateVerified( LocalDate.now());
            }
        }
        if ( Objects.nonNull(updatedContactEmail.getSource())) {
            existingContactEmail.setSource(updatedContactEmail.getSource());
        }
        if ( Objects.nonNull(updatedContactEmail.getNotes())) {
            existingContactEmail.setNotes(updatedContactEmail.getNotes());
        }
        if ( Objects.nonNull(updatedContactEmail.getIsActive())) {
            existingContactEmail.setIsActive(updatedContactEmail.getIsActive());
            if ( updatedContactEmail.getIsActive() != existingContactEmail.getIsActive()){
                existingContactEmail.setStatusDate( LocalDate.now());
            }
        }
    }

    @Override
    public String deleteContactEmail(Long id, Long contactId, String emailType ) {
        ContactEmail existingContactEmail = (id != null) ?
                contactEmailRepository.findById(id).orElse(null) :
                (Objects.nonNull(contactId) && Objects.nonNull(emailType)) ?
                        contactEmailRepository.findFirstByContactIdAndEmailType(contactId, emailType.toUpperCase()) :
                        null;
        if (Objects.nonNull(existingContactEmail)) {
            UpdatePrimaryEmailIdToNull(existingContactEmail);
            contactEmailRepository.deleteById(id);
            return "success";
        } else {
            return "Failed to delete. Unable to find the email to delete.";
        }
    }

    private void UpdatePrimaryEmailIdToNull(ContactEmail existingContactEmail) {
        if ( existingContactEmail.getIsPrimary() ){
            Contact contact = existingContactEmail.getContact();
            if ( contact != null){
                contact.setPrimaryEmailId(null);
                contactRepository.save(contact);
            }
        }
    }
}