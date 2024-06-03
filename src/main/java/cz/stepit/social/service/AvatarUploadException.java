package cz.stepit.social.service;

public class AvatarUploadException extends RuntimeException {

    public AvatarUploadException() {
        super("Cannot upload avatar");
    }
}
