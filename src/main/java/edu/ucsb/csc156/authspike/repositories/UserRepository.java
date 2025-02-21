package edu.ucsb.csc156.authspike.repositories;

import edu.ucsb.csc156.authspike.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findBySub(String sub);
}
