package cz.stepit.social.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.stepit.social.entities.Member;

/**
 * Repository for {@link Member}.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
}
