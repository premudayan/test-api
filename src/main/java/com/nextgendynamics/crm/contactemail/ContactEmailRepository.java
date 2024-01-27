package com.nextgendynamics.crm.contactemail;

import com.nextgendynamics.crm.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactEmailRepository extends JpaRepository<ContactEmail, Long> {

    ContactEmail findFirstByContactIdAndEmailType( Long contactId, String emailType );
    long countByContactId(Long contactId);
    long countByContactIdAndIsPrimaryIsTrue(Long contactId);
    ContactEmail findFirstByContactIdAndIsPrimaryIsTrue(Long contactId);
    @Modifying
    @Query("UPDATE ContactEmail  SET isPrimary = false WHERE contact.id = :contactId AND isPrimary = true AND id != :id")
    void updateNonPrimary(@Param("contactId") Long contactId, @Param("id") Long id);

}
