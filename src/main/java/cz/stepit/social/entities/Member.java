package cz.stepit.social.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents
 */
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String username;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> friendships = new ArrayList<>();

    protected Member() {
        // No-arg constructor required by JPA
    }

    /**
     * Constructor.
     *
     * @param username member's username
     */
    public Member(String username) {
        this.username = username;
    }

    /**
     * Adds {@code friend} to this member.
     *
     * <p>This method creates a {@link Friendship} between this member and provided {@code friend}.
     *
     * @param friend friend to be added
     */
    public void addFriend(Member friend) {
        final var friendship = new Friendship(this, friend);
        friendships.add(friendship);
    }

    /**
     * Get ID.
     *
     * @return ID
     */
    public long getId() {
        return id;
    }

    /**
     * Get username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get list of {@link Friendship}s.
     *
     * @return list of {@link Friendship}s
     */
    public List<Friendship> getFriendships() {
        return friendships;
    }
}
