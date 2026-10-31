package com.isp.support.repository;

import com.isp.support.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface TicketUpdateRepository extends MongoRepository < TicketUpdate, String > {
    java.util.List < TicketUpdate > findByTicketIdOrderByCreatedAtAsc(String ticketId);
    void deleteByTicketId(String ticketId);
}
