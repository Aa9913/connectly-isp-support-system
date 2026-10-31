package com.isp.support.repository;

import com.isp.support.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ServiceProviderRepository extends MongoRepository < ServiceProvider, String > {
    Optional < ServiceProvider > findByEmailIgnoreCase(String email);
    java.util.List < ServiceProvider > findByRole(Role role);
}
