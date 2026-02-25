package com.projectname.adapter.outbound.persistence;

import com.projectname.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for User entities.
 * This is an adapter implementing the UserPort interface.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

  UserJpaEntity findByUsername(String username);
}
