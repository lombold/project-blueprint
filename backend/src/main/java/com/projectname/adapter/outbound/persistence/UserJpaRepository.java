package com.projectname.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for persisted users.
 * UserRepositoryAdapter implements the outbound port using this repository.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    UserJpaEntity findByUsername(String username);
}
