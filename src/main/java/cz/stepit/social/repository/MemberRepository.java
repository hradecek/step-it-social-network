package cz.stepit.social.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.stepit.social.entities.Member;

import java.util.Optional;

/**
 * Repository for {@link Member}.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);
}
