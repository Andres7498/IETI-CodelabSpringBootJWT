package org.adaschool.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.data.mongodb.uri=mongodb://localhost/testdb", "jwt.secret=secret"})
class ApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
