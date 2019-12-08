package ru.example.models;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Class for generate unique IDs
 *
 * @author uolpakova
 * @since 05.12.2019
 */
public class AtomicIdCounter {
    private AtomicLong counter = new AtomicLong(0);

    public Long nextId() {
        return counter.incrementAndGet();
    }
}
