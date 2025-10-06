package tacs.eventos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tacs.eventos.config.TestMongoConfiguration;
import tacs.eventos.config.TestRedisConfiguration;
import tacs.eventos.config.TestSessionConfiguration;

@SpringBootTest
@Import({ TestRedisConfiguration.class, TestMongoConfiguration.class, TestSessionConfiguration.class})
class EventosApplicationTests {

    @Test
    void contextLoads() {
    }

}
