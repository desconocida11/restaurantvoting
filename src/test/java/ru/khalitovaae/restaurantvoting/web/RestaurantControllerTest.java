package ru.khalitovaae.restaurantvoting.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.khalitovaae.restaurantvoting.model.Restaurant;
import ru.khalitovaae.restaurantvoting.repository.RestaurantRepository;
import ru.khalitovaae.restaurantvoting.util.exception.ErrorType;
import ru.khalitovaae.restaurantvoting.web.json.JsonUtil;
import ru.khalitovaae.restaurantvoting.web.testdata.RestaurantTestData;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.khalitovaae.restaurantvoting.web.testdata.RestaurantTestData.*;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.ADMIN_MAIL;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.USER_MAIL;

class RestaurantControllerTest extends AbstractControllerTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(RestaurantController.URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(getAllSorted()));
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(RestaurantController.URL + SLASH + METROPOL_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(metropol));
    }

    @Test
    void getVotes() throws Exception {
        String urlTemplate = RestaurantController.URL + SLASH + PUSHKIN_ID + RestaurantController.VOTES_URL + "/2021-08-20";
        perform(MockMvcRequestBuilders.get(urlTemplate))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void getByName() throws Exception {
        perform(MockMvcRequestBuilders.get(RestaurantController.URL + "/search?name=" + METROPOL))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(metropol));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(RestaurantController.URL + SLASH + DUMMY_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(restaurantRepository.findById(DUMMY_ID).isPresent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteConstraint() throws Exception {
        perform(MockMvcRequestBuilders.delete(RestaurantController.URL + SLASH + METROPOL_ID))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(ErrorType.DATA_ERROR));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void deleteUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.delete(RestaurantController.URL + SLASH + DUMMY_ID))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        Restaurant restaurant = RestaurantTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(RestaurantController.URL + SLASH + METROPOL_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(restaurant)))
                .andExpect(status().isNoContent());
        MATCHER.assertMatch(restaurantRepository.getById(METROPOL_ID), RestaurantTestData.getUpdated());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithLocation() throws Exception {
        Restaurant newRestaurant = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(RestaurantController.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurant)));
        Restaurant created = MATCHER.readFromJson(action);
        int newId = created.id();
        newRestaurant.setId(newId);
        MATCHER.assertMatch(created, newRestaurant);
        MATCHER.assertMatch(restaurantRepository.getById(newId), newRestaurant);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createWithLocationUnauthorized() throws Exception {
        Restaurant newRestaurant = getNew();
        perform(MockMvcRequestBuilders.post(RestaurantController.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurant)))
                .andExpect(status().isForbidden());
    }
}