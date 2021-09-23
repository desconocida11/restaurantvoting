package ru.khalitovaae.restaurantvoting.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.khalitovaae.restaurantvoting.repository.VoteRepository;

import java.time.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.khalitovaae.restaurantvoting.web.testdata.RestaurantTestData.PUSHKIN_ID;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.ADMIN_MAIL;
import static ru.khalitovaae.restaurantvoting.web.testdata.VotesTestData.*;


@SpringBootTest(classes = VoteControllerBeforeDeadlineTest.FixedClockBeforeDeadlineConfig.class)
class VoteControllerBeforeDeadlineTest extends AbstractControllerTest {

    @Autowired
    private VoteRepository voteRepository;

    // Fixed current date to make our tests
    private static final LocalDateTime BEFORE_DEADLINE = LocalDateTime.of(LocalDate.now(), LocalTime.of(10,0));

    // https://blog.ttulka.com/how-to-test-date-and-time-in-spring-boot
    @TestConfiguration
    static class FixedClockBeforeDeadlineConfig {

        @Primary
        @Bean
        Clock fixedClockBeforeDeadline() {
            return Clock.fixed(BEFORE_DEADLINE.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
        }
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateBeforeDeadline() throws Exception {
        Clock clock = Clock.fixed(BEFORE_DEADLINE.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
        perform(MockMvcRequestBuilders.put(VoteController.URL + SLASH + VOTE_ID_TODAY + "?restaurant=" + PUSHKIN_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        VOTE_MATCHER.assertMatch(voteRepository.getById(VOTE_ID_TODAY), getUpdated(clock));
    }
}