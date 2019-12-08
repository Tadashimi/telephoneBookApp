package ru.example.repositories;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.example.exceptions.ContactNotFoundException;
import ru.example.exceptions.UserNotFoundException;
import ru.example.models.Contact;
import ru.example.models.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.example.Constants.*;

/**
 * Unit test for UsersRepository
 *
 * @author ukolpakova
 * @since 05.12.2019
 */
class UsersRepositoryTest {

    private final UsersRepository usersRepository = new UsersRepository();
    private User sampleUser;
    private final Map<Long, Contact> contactMap = new ConcurrentHashMap<>();

    @BeforeEach
    public void createSampleUser() {
        sampleUser = createUser();
        usersRepository.save(sampleUser);
    }

    private User createUser() {
        for (int i = 0; i < NUMBER_OF_CONTACTS; i++) {
            Contact currentContact = new Contact(CONTACT_NAME + i, CONTACT_PHONE + i);
            contactMap.put(currentContact.getId(), currentContact);
        }
        return new User(USER_NAME, contactMap);
    }

    @Test
    void findByIdExistingUser() {
        Optional<User> userFromRepository = usersRepository.findById(sampleUser.getId());
        Assert.assertTrue(userFromRepository.isPresent());
        Assert.assertEquals(sampleUser, userFromRepository.get());
    }

    @Test
    void findByIdNonexistentUser() {
        Optional<User> userFromRepository = usersRepository.findById(USER_NONEXISTENT_ID);
        Assert.assertFalse(userFromRepository.isPresent());
    }

    @Test
    void deleteByIdExistingUser() {
        Long userId = sampleUser.getId();
        usersRepository.deleteById(userId);
        Optional<User> userIsInRepository = usersRepository.findById(sampleUser.getId());
        Assert.assertFalse(userIsInRepository.isPresent());
    }

    @Test
    void deleteByIdNonexistentUser() {
        assertThrows(UserNotFoundException.class,
                () -> usersRepository.deleteById(USER_NONEXISTENT_ID));
    }

    @Test
    void deleteAllUsers() {
        User sampleUser2 = createUser();
        usersRepository.save(sampleUser2);
        usersRepository.deleteAll();
        Assert.assertTrue(usersRepository.findAll().isEmpty());
    }

    @Test
    void findAllUsers() {
        int userCountBefore = usersRepository.findAll().size();
        usersRepository.save(new User(USER_NAME));
        int userCountAfter = usersRepository.findAll().size();
        Assert.assertEquals(userCountBefore + 1, userCountAfter);
    }

    @Test
    void updateExistingUserInfo() {
        Long currentId = sampleUser.getId();

        User newUser = new User(USER_NAME);
        usersRepository.updateUserInfo(currentId, newUser);

        Optional<User> userFromRepository = usersRepository.findById(currentId);
        Assert.assertTrue(userFromRepository.isPresent());
        User actualUser = userFromRepository.get();
        Assert.assertEquals(USER_NAME, actualUser.getName());
        Assert.assertEquals(sampleUser.getContacts(), actualUser.getContacts());

    }

    @Test
    void updateNonexistentUser() {
        User newUser = new User(USER_NAME);
        assertThrows(UserNotFoundException.class,
                () -> usersRepository.updateUserInfo(USER_NONEXISTENT_ID, newUser));
    }

    @Test
    void searchExistingUser() {
        String partOfCurrentUserName = USER_NAME.substring(1);
        List<User> result = usersRepository.searchUsers(partOfCurrentUserName);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    void searchNonexistentUser() {
        String bigUserName = USER_NAME + CONTACT_NAME;
        List<User> result = usersRepository.searchUsers(bigUserName);
        Assert.assertTrue(result.isEmpty());
    }

    //Contacts Test
    @Test
    void findUserAllContacts() {
        Map<Long, Contact> expectedContactList = sampleUser.getContacts();

        Long currentUserId = sampleUser.getId();
        Map<Long, Contact> actualContactList = usersRepository.findAllUserContacts(currentUserId);

        Assert.assertEquals(expectedContactList, actualContactList);
    }

    @Test
    void findUserExistingContactById() {
        Contact anyContact = sampleUser.getContacts().values().stream().findAny().get();
        Long contactId = anyContact.getId();

        Long currentUserId = sampleUser.getId();
        Optional<Contact> contactFromRepository = usersRepository.findUserContactByContactId(currentUserId, contactId);

        Assert.assertTrue(contactFromRepository.isPresent());
        Assert.assertEquals(anyContact, contactFromRepository.get());
    }

    @Test
    void findUserNonexistentContactById() {
        Long currentUserId = sampleUser.getId();

        Optional<Contact> contactFromRepository = usersRepository.findUserContactByContactId(currentUserId, CONTACT_NONEXISTENT_ID);

        Assert.assertFalse(contactFromRepository.isPresent());
    }

    @Test
    void deleteUserExistingContactById() {
        Contact anyContact = sampleUser.getContacts().values().stream().findAny().get();
        Long contactId = anyContact.getId();

        Long currentUserId = sampleUser.getId();

        usersRepository.deleteContactByContactId(currentUserId, contactId);

        Optional<Contact> contactFromRepository = usersRepository.findUserContactByContactId(currentUserId, contactId);
        Assert.assertFalse(contactFromRepository.isPresent());
    }

    @Test
    void deleteUserNonexistentContactById() {
        Long currentUserId = sampleUser.getId();

        assertThrows(ContactNotFoundException.class,
                () -> usersRepository.deleteContactByContactId(currentUserId, CONTACT_NONEXISTENT_ID));
    }

    @Test
    void addContact() {
        Long currentUserId = sampleUser.getId();
        Contact newContact = new Contact(CONTACT_NAME, CONTACT_PHONE);
        Long newContactId = newContact.getId();

        usersRepository.addContact(currentUserId, newContact);

        Optional<Contact> contactFromRepository = usersRepository.findUserContactByContactId(currentUserId, newContactId);
        Assert.assertTrue(contactFromRepository.isPresent());
        Assert.assertEquals(newContact, contactFromRepository.get());
    }

    @Test
    void updateExistingContact() {
        Long currentUserId = sampleUser.getId();
        Contact newContact = new Contact(CONTACT_NAME, CONTACT_PHONE);
        Long contactId = sampleUser.getContacts()
                .keySet()
                .stream()
                .findAny()
                .get();

        usersRepository.updateContact(currentUserId, contactId, newContact);

        Optional<Contact> updatedContact = usersRepository.findUserContactByContactId(currentUserId, contactId);
        Assert.assertTrue(updatedContact.isPresent());
        Assert.assertEquals(newContact.getName(), updatedContact.get().getName());
        Assert.assertEquals(newContact.getPhone(), updatedContact.get().getPhone());
    }

    @Test
    void updateNonexistentContact() {
        Long currentUserId = sampleUser.getId();
        Contact newContact = new Contact(CONTACT_NAME, CONTACT_PHONE);

        assertThrows(ContactNotFoundException.class,
                () -> usersRepository.updateContact(currentUserId, CONTACT_NONEXISTENT_ID, newContact));
    }

    @Test
    void searchExistingContact() {
        Long userId = sampleUser.getId();
        String partOfContactName = CONTACT_PHONE.substring(1);

        List<Contact> result = usersRepository.searchContacts(userId, partOfContactName);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    void searchNonexistentContact() {
        Long userId = sampleUser.getId();
        String bigContactPhone = CONTACT_PHONE + CONTACT_NAME;

        List<Contact> result = usersRepository.searchContacts(userId, bigContactPhone);
        Assert.assertTrue(result.isEmpty());
    }
}