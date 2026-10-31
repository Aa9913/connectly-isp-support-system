package com.isp.support.repository;

import com.isp.support.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface AdminRepository extends MongoRepository < Admin, String > {
    Optional < Admin > findByEmailIgnoreCase(String email);
}
