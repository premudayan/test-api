package com.nextgendynamics.crm.contact;

import com.nextgendynamics.crm.contact.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ContactService {
    List<Contact> findAllContacts();
    Page<Contact> findAllContactsWithSearchFilters(ContactSearchCriteria searchCriteria, int offset, int pageSize, String sortString);
    Contact findContactById(Long id);
    boolean updateContact(Long id, Contact contact);
    boolean updateAllContacts( Contact contact);
    Contact createContact(Contact contact);
    boolean deleteContactById(Long id);

}
