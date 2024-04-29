package cz.stepit.social.service;

public class SelfFriendException extends RuntimeException {

    public SelfFriendException() {
        super("Cannot add self as friend.");
    }
}
