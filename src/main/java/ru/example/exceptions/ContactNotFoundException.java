package ru.example.exceptions;

/**
 * Exception used to indicate when a user was not created
 *
 * @author uolpakova
 * @since 05.12.2019
 */
public class ContactNotFoundException extends RuntimeException {
    public ContactNotFoundException(Long id) {
        super("Could not find contact " + id);
    }
}
