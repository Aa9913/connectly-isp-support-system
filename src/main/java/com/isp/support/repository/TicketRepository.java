package com.isp.support.repository;

import com.isp.support.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface TicketRepository extends MongoRepository < Ticket, String > {
    java.util.List < Ticket > findAllByOrderByCreatedAtDesc();
    java.util.List < Ticket > findByCustomerIdOrderByCreatedAtDesc(String customerId);
    java.util.List < Ticket > findByAssignedProviderIdOrderByUpdatedAtDesc(String providerId);
    long countByStatus(TicketStatus status);
}
