package ru.khalitovaae.restaurantvoting.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.khalitovaae.restaurantvoting.model.Vote;
import ru.khalitovaae.restaurantvoting.repository.RestaurantRepository;
import ru.khalitovaae.restaurantvoting.repository.VoteRepository;
import ru.khalitovaae.restaurantvoting.to.VoteTo;
import ru.khalitovaae.restaurantvoting.util.exception.ErrorType;
import ru.khalitovaae.restaurantvoting.web.json.JsonUtil;
import ru.khalitovaae.restaurantvoting.web.testdata.VotesTestData;

import java.time.*;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.ADMIN_MAIL;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.USER_MAIL;
import static ru.khalitovaae.restaurantvoting.web.testdata.VotesTestData.*;

class VoteControllerTest extends AbstractControllerTest {

    // Fixed current date to make our tests
    private final static ZonedDateTime AFTER_DEADLINE = LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0)).atZone(ZoneId.systemDefault());
    private final static LocalDateTime BEFORE_DEADLINE = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        List<Vote> expected = VotesTestData.getAllVotesAdmin();
        perform(MockMvcRequestBuilders.get(VoteController.URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHER.contentJson(expected));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(VoteController.URL + SLASH + VOTE_ID_TODAY))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHER.contentJson(vote_today));
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
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteAfterDeadline() throws Exception {
        Clock.fixed(AFTER_DEADLINE.toInstant(), ZoneId.systemDefault());
        perform(MockMvcRequestBuilders.delete(VoteController.URL + SLASH + VOTE_ID_TODAY))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateAfterDeadline() throws Exception {
        VoteTo voteTo = VotesTestData.getUpdated(Clock.systemDefaultZone());
        perform(MockMvcRequestBuilders.put(VoteController.URL + SLASH + VOTE_ID_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(voteTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
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
        expected.setTime(created.getTime());
        VOTE_MATCHER.assertMatch(created, expected);
        VOTE_MATCHER.assertMatch(voteRepository.getById(newId), expected);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
//        TODO Mock current time
//        VoteTo voteTo = VotesTestData.getUpdated();
//        perform(MockMvcRequestBuilders.put(VoteController.URL + SLASH + VOTE_ID_TODAY)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(JsonUtil.writeValue(voteTo)))
//                .andExpect(status().isNoContent());
    }
}