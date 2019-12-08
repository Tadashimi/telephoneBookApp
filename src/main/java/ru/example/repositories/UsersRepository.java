package ru.example.repositories;

import org.springframework.stereotype.Repository;
import ru.example.exceptions.ContactNotFoundException;
import ru.example.exceptions.UserNotFoundException;
import ru.example.models.Contact;
import ru.example.models.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repository for users list and users' contact lists
 *
 * @author uolpakova
 * @since 05.12.2019
 */
@Repository
public class UsersRepository {

    private Map<Long, User> users = new ConcurrentHashMap<>();

    public UsersRepository() {
    }

    /**
     * Method save new user (telephone book owner) to the users map.
     *
     * @param user - new User
     * @return User - created user
     */
    public User save(User user) {
        Long userId = user.getId();
        users.put(userId, user);
        return users.get(userId);
    }

    /**
     * Method delete user (telephone book owner) by user id with its contacts.
     *
     * @param id - id for deleting user
     * @return String - message that user was deleted
     */
    public String deleteById(Long id) {
        User currentUser = users.get(id);
        if (currentUser == null) {
            throw new UserNotFoundException(id);
        }
        users.remove(id);
        return "User with id " + id + " was deleted";
    }

    /**
     * Method delete all users (telephone book owners) from telephone book owners map.
     *
     * @return String - message that all users (telephone book owners) were deleted
     */
    public String deleteAll() {
        users.clear();
        return "All contacts were deleted";
    }

    /**
     * Method for get all users (telephone book owners) with theirs contacts.
     *
     * @return Map<Long id, User user> - all users in the repository
     */
    public Map<Long, User> findAll() {
        return Collections.unmodifiableMap(users);
    }

    /**
     * Method for get user (telephone book owner) by id.
     *
     * @param id - id for get user
     * @return Optional<User user> - user in repository with appropriate id
     */
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Method updates user info (without contacts).
     *
     * @param id   - id for user to update
     * @param user - user with new data
     * @return User - updated user
     */
    public User updateUserInfo(Long id, User user) {
        User currentUser = getCurrentUser(id);
        currentUser.setName(user.getName());
        return currentUser;
    }

    /**
     * Method search user by substring of user name.
     * If users cannot be found then return empty list.
     *
     * @param userName - part of user name
     * @return List<User> - founded users list
     */
    public List<User> searchUsers(String userName) {
        return users.values()
                .stream()
                .filter(it -> it.getName().toUpperCase().contains(userName.toUpperCase()))
                .collect(Collectors.toList());
    }

    /**
     * Method gets all user's contacts.
     * Empty map if user don't have contacts.
     * If cannot find user using userId then UserNotFoundException throws.
     *
     * @param userId - id for user which contacts must be get
     * @return Map<Long, Contact> - all user's contacts
     */
    public Map<Long, Contact> findAllUserContacts(Long userId) {
        return getCurrentUser(userId).getContacts();
    }

    /**
     * Method get contact by its id.
     * If cannot find user using userId then UserNotFoundException throws.
     *
     * @param userId    - id for user which contact must be get
     * @param contactId - contact id which must be found
     * @return Optional<Contact> - founded contact
     */
    public Optional<Contact> findUserContactByContactId(Long userId, Long contactId) {
        return getCurrentUser(userId).getContactById(contactId);
    }

    /**
     * Method add new contact to user's telephone book.
     * If cannot find user using userId then UserNotFoundException throws.
     *
     * @param userId     - id for user to which telephone book contact must be added
     * @param newContact - contact to be added
     * @return Contact - added contact
     */
    public Contact addContact(Long userId, Contact newContact) {
        return getCurrentUser(userId).addContact(newContact);
    }

    /**
     * Method deletes user's contact by contact id.
     * If cannot find user using userId then UserNotFoundException throws.
     *
     * @param userId    - id for user which contact must be deleted
     * @param contactId - id for contact that must be deleted
     * @return String - message that contact was deleted
     */
    public String deleteContactByContactId(Long userId, Long contactId) {
        User currentUser = getCurrentUser(userId);
        Optional<Contact> currentContact = currentUser.getContactById(contactId);
        if (currentContact.isPresent()) {
            currentUser.deleteContactById(contactId);
            return "Contact with id " + contactId + " was deleted";
        } else {
            throw new ContactNotFoundException(contactId);
        }
    }

    /**
     * Method deletes all user's contacts.
     * If cannot find user using userId then UserNotFoundException throws.
     *
     * @param userId - id for user to which telephone book must be cleared
     * @return String - message that all contacts were deleted
     */
    public String deleteAllContacts(Long userId) {
        getCurrentUser(userId).deleteAllContacts();
        return "All contacts were deleted";
    }

    /**
     * Method update user's contact by contact id.
     * If cannot find user using userId then UserNotFoundException throws.
     *
     * @param userId     - id for user which contact must be updated
     * @param contactId  - id for contact that must be updated
     * @param newContact - new contact info
     * @return Contact - updated contact
     */
    public Contact updateContact(Long userId, Long contactId, Contact newContact) {
        return getCurrentUser(userId).updateContact(contactId, newContact);
    }

    /**
     * Method search contact by substring of phone number.
     * If contact cannot be found then return empty list.
     * If cannot find user using userId then UserNotFoundException throws.
     *
     * @param userId       - id for user which telephone book is used for search
     * @param contactPhone - part of phone number to search contact
     * @return List<Contact> - founded contacts
     */
    public List<Contact> searchContacts(Long userId, String contactPhone) {
        Collection<Contact> allUserContact = getCurrentUser(userId)
                .getContacts()
                .values();
        return allUserContact
                .stream()
                .filter(it -> it.getPhone().toUpperCase().contains(contactPhone.toUpperCase()))
                .collect(Collectors.toList());
    }

    private User getCurrentUser(Long userId) {
        User currentUser = users.get(userId);
        if (currentUser == null) {
            throw new UserNotFoundException(userId);
        }
        return currentUser;
    }
}
