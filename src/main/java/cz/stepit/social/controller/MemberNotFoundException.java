package cz.stepit.social.controller;

public class MemberNotFoundException extends RuntimeException {

    protected final long id;

    public MemberNotFoundException(long id) {
        super("Member with ID " + id + " not found");
        this.id = id;
    }

    public long getMemberId() {
        return id;
    }
}
