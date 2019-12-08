package ru.example.models;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static ru.example.Constants.CONTACT_NAME;
import static ru.example.Constants.CONTACT_PHONE;

/**
 * Tests for Contact class
 *
 * @author ukolpakova
 * @since 05.12.2019
 */
class ContactTest {

    @Test
    void createContactWithoutId() {
        Contact contactWithoutId = new Contact(CONTACT_NAME, CONTACT_PHONE);
        Long currentId = contactWithoutId.getId();
        Assert.assertNotNull(currentId);
        Assert.assertEquals(CONTACT_NAME, contactWithoutId.getName());
        Assert.assertEquals(CONTACT_PHONE, contactWithoutId.getPhone());
    }
}