package ru.khalitovaae.restaurantvoting.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.khalitovaae.restaurantvoting.repository.DishRepository;
import ru.khalitovaae.restaurantvoting.to.Menu;
import ru.khalitovaae.restaurantvoting.util.exception.ErrorType;
import ru.khalitovaae.restaurantvoting.web.json.JsonUtil;
import ru.khalitovaae.restaurantvoting.web.testdata.DishTestData;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.khalitovaae.restaurantvoting.web.testdata.DishTestData.*;
import static ru.khalitovaae.restaurantvoting.web.testdata.RestaurantTestData.*;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.ADMIN_MAIL;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.USER_MAIL;

class MenuControllerTest extends AbstractControllerTest {

    @Autowired
    private DishRepository dishRepository;

    @Test
    void getAllMenusForDate() throws Exception {
        setDishes();
        perform(MockMvcRequestBuilders.get(MenuController.URL + MenuController.TODAY_URL))
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
    }

    @Test
    void getMenuForDate() throws Exception {
        final String urlTemplate = MenuController.URL + SLASH + METROPOL_ID + SLASH + MenuController.TODAY_URL;
        perform(MockMvcRequestBuilders.get(urlTemplate))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(WITH_DISHES_MATCHER.contentJson(getMenuForToday()));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createTodayMenu() throws Exception {
        Menu menu = createNewMenu();
        final String urlTemplate = MenuController.URL + SLASH + DUMMY_ID + MenuController.TODAY_URL;
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
        final String urlTemplate = MenuController.URL + SLASH + DUMMY_ID + MenuController.TODAY_URL;
        perform(MockMvcRequestBuilders.post(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateTodayMenu() throws Exception {
        Menu menu = DishTestData.getUpdatedMenu();
        perform(MockMvcRequestBuilders.patch(MenuController.URL + SLASH + METROPOL_ID + MenuController.TODAY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)))
                .andExpect(status().isNoContent());
        DISH_MATCHER.assertMatch(dishRepository.findAllById(List.of(7, 8)), getUpdatedDishes());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteTodayMenu() throws Exception {
        perform(MockMvcRequestBuilders.delete(MenuController.URL + SLASH + PUSHKIN_ID + MenuController.TODAY_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertTrue(dishRepository.findAllById(getMenuPushkin()).isEmpty());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteDishFromMenu() throws Exception {
        int dishId = pushkinDish1.getId();
        String urlTemplate = MenuController.URL + SLASH + PUSHKIN_ID + MenuController.TODAY_URL + "?dish=" + dishId;
        perform(MockMvcRequestBuilders.delete(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertTrue(dishRepository.findById(dishId).isEmpty());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteTodayMenuWithVotes() throws Exception {
        perform(MockMvcRequestBuilders.delete(MenuController.URL + SLASH + METROPOL_ID + MenuController.TODAY_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }
}