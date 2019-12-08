package ru.example.models;

import lombok.Data;

/**
 * Class presented contact in user's telephone book.
 * Attention: Class uses lombok data sp getters and setters are generated automatically.
 *
 * @author uolpakova
 * @since 05.12.2019
 */
@Data
public class Contact {
    private static final AtomicIdCounter ID_COUNTER = new AtomicIdCounter();
    private Long id;
    private String name;
    private String phone;

    public Contact(String name, String phone) {
        this.id = ID_COUNTER.nextId();
        this.name = name;
        this.phone = phone;
    }
}
