package ru.example.models;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static ru.example.Constants.*;

/**
 * Tests for User class
 *
 * @author ukolpakova
 * @since 05.12.2019
 */
class UserTest {
    private final Map<Long, Contact> CONTACTS = new ConcurrentHashMap<>();

    @BeforeEach
    public void createContacts() {
        for (int i = 0; i < NUMBER_OF_CONTACTS; i++) {
            Contact currentContact = new Contact(CONTACT_NAME + i, CONTACT_PHONE + i);
            CONTACTS.put(currentContact.getId(), currentContact);
        }
    }

    @Test
    void createUserWithOnlyName() {
        User userWithOnlyName = new User(USER_NAME);
        Long currentId = userWithOnlyName.getId();

        Assert.assertEquals(USER_NAME, userWithOnlyName.getName());
        Assert.assertNotNull(currentId);
        Assert.assertTrue(userWithOnlyName.getContacts().isEmpty());
    }

    @Test
    void getContacts() {
        User userWithContacts = new User(USER_NAME, CONTACTS);

        Map<Long, Contact> contactsFromUser = userWithContacts.getContacts();
        Assert.assertEquals(CONTACTS, contactsFromUser);
    }

    @Test
    void getContactById() {
        User userWithContacts = new User(USER_NAME, CONTACTS);
        Contact anyContact = CONTACTS.values().stream().findAny().get();

        Optional<Contact> contactsFromUser = userWithContacts.getContactById(anyContact.getId());

        Assert.assertTrue(contactsFromUser.isPresent());
        Assert.assertEquals(anyContact, contactsFromUser.get());
    }

    @Test
    void addContact() {
        User userWithOnlyName = new User(USER_NAME);
        Contact newContact = new Contact(CONTACT_NAME, CONTACT_PHONE);
        Long contactId = newContact.getId();
        userWithOnlyName.addContact(newContact);

        Optional<Contact> contactFromUser = userWithOnlyName.getContactById(contactId);
        Assert.assertTrue(contactFromUser.isPresent());
        Assert.assertEquals(newContact, contactFromUser.get());
    }

    @Test
    void deleteAllContacts() {
        User userWithContacts = new User(USER_NAME, CONTACTS);
        userWithContacts.deleteAllContacts();
        Assert.assertTrue(userWithContacts.getContacts().isEmpty());
    }

    @Test
    void deleteContact() {
        User userWithContacts = new User(USER_NAME, CONTACTS);
        userWithContacts.deleteContactById(CONTACT_EXISTING_ID);
        Assert.assertFalse(userWithContacts.getContactById(CONTACT_EXISTING_ID).isPresent());
    }

    @Test
    void updateContact() {
        User userWithContacts = new User(USER_NAME, CONTACTS);
        Contact newContact = new Contact(CONTACT_NAME, CONTACT_PHONE);
        Contact anyContact = userWithContacts.getContacts()
                .values()
                .stream()
                .findAny()
                .get();
        Long contactId = anyContact.getId();

        userWithContacts.updateContact(contactId, newContact);

        Optional<Contact> updatedContact = userWithContacts.getContactById(contactId);
        Assert.assertTrue(updatedContact.isPresent());
        Assert.assertEquals(newContact.getName(), updatedContact.get().getName());
        Assert.assertEquals(newContact.getPhone(), updatedContact.get().getPhone());
    }
}