package com.isp.support.repository;

import com.isp.support.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CustomerRepository extends MongoRepository < Customer, String > {
    Optional < Customer > findByEmailIgnoreCase(String email);
    long countByEnabledTrue();
}
