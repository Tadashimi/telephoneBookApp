package ru.example.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.example.exceptions.ContactNotFoundException;
import ru.example.exceptions.UserNotFoundException;
import ru.example.models.Contact;
import ru.example.models.User;
import ru.example.repositories.UsersRepository;

import java.util.List;
import java.util.Map;

/**
 * Web layer for user repository (using Spring MVC).
 *
 * @author uolpakova
 * @since 05.12.2019
 */
@RestController
public class UserController {
    @Autowired
    private final UsersRepository repository;

    UserController(UsersRepository repository) {
        this.repository = repository;
    }

    /**
     * Method for GET "/users".
     *
     * @return Map<Long id, User> of all users - telephone books owners
     */
    @GetMapping("/users")
    Map<Long, User> getAllUsers() {
        return repository.findAll();
    }

    /**
     * Method for POST "/users". Method create a new user - telephone book owner.
     * In current realisation method doesn't create users's contact, ony user's info.
     *
     * @param newUser - user contained new data for user
     * @return User - created user
     */
    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    /**
     * Method for DELETE "/users".
     * Method deletes all users.
     *
     * @return message that all users were deleted.
     */
    @DeleteMapping("/users")
    String deleteAllUsers() {
        return repository.deleteAll();
    }

    /**
     * Method for GET "/users/{id}".
     * Method return User by its id.
     * If user can't be found method throws UserNotFoundException.
     *
     * @param id - user id
     * @return founded User with id from param
     */
    @GetMapping("/users/{id}")
    User getUser(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Method for DELETE "/users/{id}".
     * Method deletes user by id.
     * If deleting nonexistent user UserNotFoundException will be thrown.
     *
     * @return message that user was deleted.
     */
    @DeleteMapping("/users/{id}")
    String deleteUser(@PathVariable Long id) {
        return repository.deleteById(id);
    }

    /**
     * Method for PUT "/users/{id}".
     * Method updates user info.
     * In current realisation method doesn't update user's contacts.
     * If updating nonexistent user UserNotFoundException will be thrown.
     *
     * @param updatedUser - User with new user info
     * @param id          - id of updated user
     * @return User - updated User
     */
    @PutMapping("/users/{id}")
    User updateUser(@RequestBody User updatedUser, @PathVariable Long id) {
        return repository.updateUserInfo(id, updatedUser);
    }

    /**
     * Method for POST "/users/search".
     * Method for search user by name.
     *
     * @param name User name for search
     * @return List<User> of founded users or empty list then users can't be found.
     */
    @PostMapping("/users/search")
    List<User> searchUsers(@RequestBody String name) {
        return repository.searchUsers(name);
    }

    /**
     * Method for GET "/users/{userId}/contacts".
     * Method for getting all user's contacts.
     *
     * @param userId - telephone book owner id
     * @return Map<Long, Contact> of user's contacts
     */
    @GetMapping("/users/{userId}/contacts")
    Map<Long, Contact> getAllUserContacts(@PathVariable Long userId) {
        return repository.findAllUserContacts(userId);
    }

    /**
     * Method for POST "/users/{userId}/contacts".
     * Method creates new contact for user.
     *
     * @param newContact - new Contact for user's telephone book
     * @param userId     - telephone book owner id
     * @return Contact - created contact
     */
    @PostMapping("/users/{userId}/contacts")
    Contact createNewUserContacts(@RequestBody Contact newContact, @PathVariable Long userId) {
        return repository.addContact(userId, newContact);
    }

    /**
     * Method for DELETE "/users/{userId}/contacts".
     * Delete all user'scontacts.
     *
     * @param userId - telephone book owner id
     * @return String - message that all contacts were deleted
     */
    @DeleteMapping("/users/{userId}/contacts")
    String deleteAllUsersContacts(@PathVariable Long userId) {
        return repository.deleteAllContacts(userId);
    }

    /**
     * Method for GET "/users/{userId}/contacts/{contactId}".
     * Method return contact by its id.
     * If contact cannot be found method throws ContactNotFoundException.
     *
     * @param userId    - telephone book owner id
     * @param contactId - contact id
     * @return Contact that was found
     */
    @GetMapping("/users/{userId}/contacts/{contactId}")
    Contact getUserContact(@PathVariable Long userId, @PathVariable Long contactId) {
        return repository.findUserContactByContactId(userId, contactId)
                .orElseThrow(() -> new ContactNotFoundException(contactId));
    }

    /**
     * Method for DELETE "/users/{userId}/contacts/{contactId}".
     * Method deletes contact by its id.
     * If deleting nonexistent contact ContactNotFoundException will be thrown.
     *
     * @param userId    - telephone book owner id
     * @param contactId - deleting contact id
     * @return String - message that contact was deleted
     */
    @DeleteMapping("/users/{userId}/contacts/{contactId}")
    String deleteUserContact(@PathVariable Long userId, @PathVariable Long contactId) {
        return repository.deleteContactByContactId(userId, contactId);
    }

    /**
     * Method for PUT "/users/{userId}/contacts/{contactId}".
     * Method updates contact.
     * If updating nonexistent contact then ContactNotFoundException will be thrown.
     *
     * @param updatedContact - Contact with new contact info
     * @param userId         - telephone book owner id
     * @param contactId      - updating contact id
     * @return Contact - updated Contact
     */
    @PutMapping("/users/{userId}/contacts/{contactId}")
    Contact updateUserContact(@RequestBody Contact updatedContact, @PathVariable Long userId, @PathVariable Long contactId) {
        return repository.updateContact(userId, contactId, updatedContact);
    }

    /**
     * Method for POST "/users/{userId}/contacts/search".
     * Method for search contact by phone.
     *
     * @param phone  - Contact phone for search
     * @param userId - telephone book owner id
     * @return List<Contact> of founded contacts or empty list then contacts can't be found.
     */
    @PostMapping("/users/{userId}/contacts/search")
    List<Contact> searchContacts(@RequestBody String phone, @PathVariable Long userId) {
        return repository.searchContacts(userId, phone);
    }
}
