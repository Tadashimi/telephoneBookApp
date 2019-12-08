package ru.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.example.models.Contact;
import ru.example.models.User;
import ru.example.repositories.UsersRepository;

/**
 * Class for creation sample data in the repository when starting application.
 *
 * @author uolpakova
 * @since 05.12.2019
 */
@Configuration
@Slf4j
public class LoadData {
    @Bean
    CommandLineRunner initData(UsersRepository repository) {
        return args -> {
            User user1 = new User("Mary");
            user1.addContact(new Contact("Billy", "88005553535"));
            user1.addContact(new Contact("John", "88005555555"));
            log.info("Preloading " + repository.save(user1));
            log.info("Preloading " + repository.save(new User("Mia")));
        };
    }
}
