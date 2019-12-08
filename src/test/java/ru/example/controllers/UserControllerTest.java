package ru.example.controllers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import ru.example.TelephoneBookApp;
import ru.example.models.Contact;
import ru.example.models.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.example.Constants.*;

/**
 * Unit tests for UserController.
 *
 * @author ukolpakova
 * @since 06.12.2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TelephoneBookApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getRootUrl() {
        return LOCALHOST_URL + port + USERS_URL;
    }

    private User createUserInRepository() {
        User sampleUser = new User(USER_NAME);
        Contact sampleContact = new Contact(CONTACT_NAME, CONTACT_PHONE);
        sampleUser.addContact(sampleContact);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");

        HttpEntity<User> request = new HttpEntity<>(sampleUser, headers);

        restTemplate.postForEntity(getRootUrl(), request, String.class);
        return sampleUser;
    }

    @Test
    public void getAllUsers() {
        createUserInRepository();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl(),
                HttpMethod.GET, entity, String.class);
        Assert.assertNotEquals(EMPTY_BODY, response.getBody());
    }

    @Test
    public void getUserById() {
        User sampleUser = createUserInRepository();
        User userFromRepository = restTemplate.getForObject(getRootUrl() + sampleUser.getId(), User.class);
        Assert.assertEquals(sampleUser, userFromRepository);
    }

    @Test
    public void updateUser() {
        User newUser = createUserInRepository();
        Long userId = newUser.getId();
        newUser.setName("new" + USER_NAME);
        restTemplate.put(getRootUrl() + userId, newUser);
        User updatedUser = restTemplate.getForObject(getRootUrl() + userId, User.class);
        Assert.assertEquals(newUser, updatedUser);
    }

    @Test
    public void searchUser() {
        User sampleUser = createUserInRepository();
        String searchString = sampleUser.getName().substring(1);
        User[] result = restTemplate.postForObject(getRootUrl() + SEARCH_URL, searchString, User[].class);
        Assert.assertNotEquals(0, result.length);
        List<User> usersList = Arrays.asList(result);
        Assert.assertTrue(usersList.contains(sampleUser));
    }

    @Test
    public void deleteUser() {
        Long userId = createUserInRepository().getId();
        restTemplate.delete(getRootUrl() + userId);
        assertThrows(RestClientException.class, () -> {
            restTemplate.getForObject(getRootUrl() + userId, User.class);
        });
    }

    @Test
    public void deleteAllUsers() {
        restTemplate.delete(getRootUrl());

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl(),
                HttpMethod.GET, entity, String.class);
        Assert.assertEquals(EMPTY_BODY, response.getBody());
    }

    @Test
    public void getAllContacts() {
        Long userId = createUserInRepository().getId();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + userId + CONTACTS_URL,
                HttpMethod.GET, entity, String.class);
        Assert.assertNotEquals(EMPTY_BODY, response.getBody());
    }

    @Test
    public void getContactById() {
        User sampleUser = createUserInRepository();
        Contact sampleContact = sampleUser.getContacts()
                .values()
                .stream()
                .findAny()
                .get();
        Long contactId = sampleContact.getId();

        Contact contactFromRepository = restTemplate.getForObject(getRootUrl() + sampleUser.getId() + CONTACTS_URL + contactId, Contact.class);
        Assert.assertEquals(sampleContact, contactFromRepository);
    }

    @Test
    public void updateContact() {
        User sampleUser = createUserInRepository();
        Long userId = sampleUser.getId();
        Contact newContact = sampleUser.getContacts()
                .values()
                .stream()
                .findAny()
                .get();
        Long contactId = newContact.getId();

        newContact.setName("new" + CONTACT_NAME);
        newContact.setPhone("new" + CONTACT_PHONE);
        restTemplate.put(getRootUrl() + userId + CONTACTS_URL + contactId, newContact);
        Contact updatedContact = restTemplate.getForObject(getRootUrl() + userId + CONTACTS_URL + contactId, Contact.class);
        Assert.assertEquals(newContact, updatedContact);
    }

    @Test
    public void deleteContactById() {
        User sampleUser = createUserInRepository();
        Long userId = sampleUser.getId();
        Long contactId = sampleUser
                .getContacts()
                .keySet()
                .stream()
                .findAny()
                .get();

        restTemplate.delete(getRootUrl() + userId + CONTACTS_URL + contactId);
        assertThrows(RestClientException.class, () -> {
            restTemplate.getForObject(getRootUrl() + userId + CONTACTS_URL + contactId, Contact.class);
        });
    }

    @Test
    public void deleteAllContacts() {
        User sampleUser = createUserInRepository();
        Long userId = sampleUser.getId();
        restTemplate.delete(getRootUrl() + userId + CONTACTS_URL);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + userId + CONTACTS_URL,
                HttpMethod.GET, entity, String.class);
        Assert.assertEquals(EMPTY_BODY, response.getBody());
    }

    @Test
    public void searchContact() {
        User sampleUser = createUserInRepository();
        Contact sampleContact = sampleUser.getContacts()
                .values()
                .stream()
                .findAny()
                .get();
        String phoneForSearch = sampleContact.getPhone().substring(1);

        String targetUri = getRootUrl() + sampleUser.getId() + CONTACTS_URL + SEARCH_URL;
        Contact[] result = restTemplate.postForObject(targetUri, phoneForSearch, Contact[].class);
        Assert.assertNotEquals(0, result.length);
        List<Contact> contactsList = Arrays.asList(result);
        Assert.assertTrue(contactsList.contains(sampleContact));
    }
}
