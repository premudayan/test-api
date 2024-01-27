package com.nextgendynamics.crm.contactemail;

import java.util.List;

public interface ContactEmailService {

    List<ContactEmail> findAllContactEmailsByContactId(Long contactId);
    ContactEmail findContactEmailById(Long id);
    ContactEmail createContactEmail(Long contactId, ContactEmail contactEmail);
    List<ContactEmail> createContactEmail(Long contactId, List<ContactEmail> contactEmails);
    ContactEmail updateContactEmail(Long contactId, Long id, Boolean upsert, ContactEmail contactEmail);
    String deleteContactEmail(Long id, Long contactId, String emailType );

}
