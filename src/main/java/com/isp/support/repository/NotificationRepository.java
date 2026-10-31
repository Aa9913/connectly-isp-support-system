package com.isp.support.repository;

import com.isp.support.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository < Notification, String > {
    java.util.List < Notification > findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(RecipientType type, String recipientId);
    long countByRecipientTypeAndRecipientIdAndReadFalse(RecipientType type, String recipientId);
    void deleteByTicketId(String ticketId);
}
