package cz.stepit.social.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a uni-directional friendship between two {@link Member}s.
 */
@Entity
@Table
public class Friendship {

    @Id
    @ManyToOne
    private Member member;

    @Id
    @ManyToOne
    private Member friend;

    protected Friendship() {
        // No-arg constructor required by JPA
    }

    /**
     * Constructor.
     *
     * @param member member
     * @param friend member's friend
     */
    public Friendship(Member member, Member friend) {
        this.member = member;
        this.friend = friend;
    }

    /**
     * Get member.
     *
     * @return member
     */
    public Member getMember() {
        return member;
    }

    /**
     * Get friend.
     *
     * @return friend
     */
    public Member getFriend() {
        return friend;
    }
}
