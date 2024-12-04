package com.dekapx.apps.repository;

import com.dekapx.apps.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.dekapx.apps.repository.UserTestData.FIRST_NAME;
import static com.dekapx.apps.repository.UserTestData.LAST_NAME;
import static com.dekapx.apps.repository.UserTestData.USERNAME;
import static com.dekapx.apps.repository.UserTestData.createUser;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    private UserRepository repository;

    @Autowired
    public UserRepositoryTest(UserRepository repository) {
        this.repository = repository;
    }

    @BeforeEach
    public void setup() {
        User user = createUser();
        this.repository.save(user);
    }

    @AfterEach
    public void cleanUp() {
        User user = repository.findByUsername(USERNAME);
        this.repository.delete(user);
    }

    @Test
    public void shouldReturnCustomerWhenFindByFirstName() {
        User user = this.repository.findByUsername(USERNAME);
        assertThat(user)
                .isNotNull()
                .satisfies(o -> {
                    assertThat(o.getFirstName()).isEqualTo(FIRST_NAME);
                    assertThat(o.getLastName()).isEqualTo(LAST_NAME);
                });

    }
}
