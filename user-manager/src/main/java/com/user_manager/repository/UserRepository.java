package com.user_manager.repository;

import com.user_manager.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO USERS(full_name, username, email, password) VALUES(:full_name,:username, :email, :password)", nativeQuery = true)
    int registerNewUser(@Param("full_name") String full_name,
                        @Param("username") String username,
                        @Param("email") String email,
                        @Param("password") String password);

}
