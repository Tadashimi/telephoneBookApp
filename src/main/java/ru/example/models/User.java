package ru.example.models;

import lombok.Data;
import ru.example.exceptions.ContactNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class presented User - telephone book owner
 * Attention: Class uses lombok data sp getters and setters are generated automatically.
 *
 * @author uolpakova
 * @since 05.12.2019
 */
@Data
public class User {
    private static final AtomicIdCounter ID_COUNTER = new AtomicIdCounter();
    private Long id;
    private String name;
    private Map<Long, Contact> contacts;

    public User(String name) {
        this(name, new HashMap<>());
    }

    public User(String name, Map<Long, Contact> contacts) {
        this.id = ID_COUNTER.nextId();
        this.name = name;
        this.contacts = contacts;
    }

    public void deleteContactById(Long contactId) {
        contacts.remove(contactId);
    }

    public void deleteAllContacts() {
        contacts.clear();
    }

    public Contact addContact(Contact contact) {
        contacts.put(contact.getId(), contact);
        return contacts.get(contact.getId());
    }

    public Contact updateContact(Long contactId, Contact contact) {
        Contact currentContact = contacts.get(contactId);
        if (currentContact == null) {
            throw new ContactNotFoundException(contactId);
        }
        currentContact.setName(contact.getName());
        currentContact.setPhone(contact.getPhone());
        return currentContact;
    }

    public Optional<Contact> getContactById(Long contactId) {
        return Optional.ofNullable(contacts.get(contactId));
    }
}
