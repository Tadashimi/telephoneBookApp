package ru.example.exceptions;

/**
 * Exception used to indicate when a user is looked up but not found
 *
 * @author uolpakova
 * @since 05.12.2019
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Could not find user " + id);
    }
}
