package ru.khalitovaae.restaurantvoting.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.khalitovaae.restaurantvoting.model.Vote;
import ru.khalitovaae.restaurantvoting.repository.VoteRepository;
import ru.khalitovaae.restaurantvoting.to.VoteTo;
import ru.khalitovaae.restaurantvoting.util.exception.ErrorType;
import ru.khalitovaae.restaurantvoting.web.testdata.VotesTestData;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.ADMIN_MAIL;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.USER_MAIL;
import static ru.khalitovaae.restaurantvoting.web.testdata.VotesTestData.*;


class VoteControllerTest extends AbstractControllerTest {

    @Autowired
    private VoteRepository voteRepository;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        List<VoteTo> expected = VotesTestData.getAllVotesToAdmin();
        perform(MockMvcRequestBuilders.get(VoteController.URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(expected));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(VoteController.URL + SLASH + VOTE_ID_TODAY))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(new VoteTo(vote_today)));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(VoteController.URL + SLASH + VOTE_ID_TODAY))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(errorType(ErrorType.DATA_NOT_FOUND));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createWithLocation() throws Exception {
        Vote expected = VotesTestData.getNew(Clock.systemDefaultZone());
        ResultActions action = perform(MockMvcRequestBuilders.post(VoteController.URL + "?restaurant=5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        Vote created = VOTE_MATCHER.readFromJson(action);
        int newId = created.id();
        expected.setId(newId);
        Assertions.assertThat(created.getTime()).isCloseTo(expected.getTime(), within(2, ChronoUnit.SECONDS));
        VOTE_TIME_MATCHER.assertMatch(created, expected);
        VOTE_TIME_MATCHER.assertMatch(voteRepository.getById(newId), expected);
    }
}