package ru.khalitovaae.restaurantvoting.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.khalitovaae.restaurantvoting.web.testdata.DishTestData;
import ru.khalitovaae.restaurantvoting.web.testdata.VotesTestData;
import ru.khalitovaae.restaurantvoting.model.Restaurant;
import ru.khalitovaae.restaurantvoting.repository.DishRepository;
import ru.khalitovaae.restaurantvoting.repository.RestaurantRepository;
import ru.khalitovaae.restaurantvoting.to.Menu;
import ru.khalitovaae.restaurantvoting.util.exception.ErrorType;
import ru.khalitovaae.restaurantvoting.web.json.JsonUtil;
import ru.khalitovaae.restaurantvoting.web.testdata.RestaurantTestData;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.khalitovaae.restaurantvoting.web.testdata.VotesTestData.VOTE_RESULT_MATCHER;
import static ru.khalitovaae.restaurantvoting.web.testdata.DishTestData.*;
import static ru.khalitovaae.restaurantvoting.web.testdata.RestaurantTestData.*;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.ADMIN_MAIL;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.USER_MAIL;

class RestaurantControllerTest extends AbstractControllerTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishRepository dishRepository;

    @Test
    void getAllMenusForDate() throws Exception {
        setDishes();
        perform(MockMvcRequestBuilders.get(RestaurantController.URL + RestaurantController.MENU_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Metropol"))
                .andExpect(jsonPath("$[0].dishes", hasSize(2)))
                .andExpect(jsonPath("$[1].name").value("NapoliPizza"))
                .andExpect(jsonPath("$[1].dishes", hasSize(3)))
                .andExpect(jsonPath("$[2].name").value("Pushkin"))
                .andExpect(jsonPath("$[2].dishes", hasSize(5)));
        // TODO Matcher
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(RestaurantController.URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(getAllSorted()));
    }

    @Test
    void getVotingResults() throws Exception {
        perform(MockMvcRequestBuilders.get(RestaurantController.URL + RestaurantController.VOTES_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_RESULT_MATCHER.contentJson(VotesTestData.getVotingResults()));
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
        String urlTemplate = RestaurantController.URL + SLASH + PUSHKIN_ID + RestaurantController.VOTES_URL + "?date=2021-08-20";
        perform(MockMvcRequestBuilders.get(urlTemplate))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void getMenuForDate() throws Exception {
        perform(MockMvcRequestBuilders.get(RestaurantController.URL + SLASH + METROPOL_ID + SLASH + RestaurantController.MENU_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(WITH_DISHES_MATCHER.contentJson(getMenuForToday()));
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
    @WithUserDetails(value = ADMIN_MAIL)
    void createTodayMenu() throws Exception {
        Menu menu = createNewMenu();
        final String urlTemplate = RestaurantController.URL + SLASH + DUMMY_ID + RestaurantController.MENU_URL;
        ResultActions action = perform(
                MockMvcRequestBuilders.post(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)));
        action.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(getDishesForMenu()));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createTodayMenuUnauthorized() throws Exception {
        Menu menu = createNewMenu();
        final String urlTemplate = RestaurantController.URL + SLASH + DUMMY_ID + RestaurantController.MENU_URL;
        perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValue(menu)))
                        .andExpect(status().isForbidden());
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

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateTodayMenu() throws Exception {
        Menu menu = DishTestData.getUpdatedMenu();
        perform(MockMvcRequestBuilders.put(RestaurantController.URL + SLASH + METROPOL_ID + RestaurantController.MENU_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)))
                .andExpect(status().isCreated());
        DISH_MATCHER.assertMatch(dishRepository.findAllById(List.of(14, 15, 16)), getUpdatedDishes());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteTodayMenu() throws Exception {
        perform(MockMvcRequestBuilders.delete(RestaurantController.URL + SLASH + PUSHKIN_ID + RestaurantController.MENU_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertTrue(dishRepository.findAllById(getMenuPushkin()).isEmpty());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteTodayMenuWithVotes() throws Exception {
        perform(MockMvcRequestBuilders.delete(RestaurantController.URL + SLASH + METROPOL_ID + RestaurantController.MENU_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }
}