package ru.khalitovaae.restaurantvoting.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.khalitovaae.restaurantvoting.web.testdata.RestaurantTestData.PUSHKIN_ID;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.ADMIN_MAIL;
import static ru.khalitovaae.restaurantvoting.web.testdata.VotesTestData.VOTE_ID_TODAY;


@SpringBootTest(classes = VoteControllerAfterDeadlineTest.FixedClockAfterDeadlineConfig.class)
class VoteControllerAfterDeadlineTest extends AbstractControllerTest {

    // Fixed current date to make our tests
    private static final LocalDateTime AFTER_DEADLINE = LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0));

    @TestConfiguration
    static class FixedClockAfterDeadlineConfig {

        @Primary
        @Bean
        Clock fixedClockAfterDeadline() {
            return Clock.fixed(AFTER_DEADLINE.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
        }
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateAfterDeadline() throws Exception {
        Clock.fixed(AFTER_DEADLINE.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
        perform(MockMvcRequestBuilders.put(VoteController.URL + SLASH + VOTE_ID_TODAY + "?restaurant=" + PUSHKIN_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
}