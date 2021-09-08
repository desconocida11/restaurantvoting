package ru.khalitovaae.restaurantvoting.web.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.khalitovaae.restaurantvoting.model.User;
import ru.khalitovaae.restaurantvoting.repository.UserRepository;
import ru.khalitovaae.restaurantvoting.util.exception.ErrorType;
import ru.khalitovaae.restaurantvoting.web.AbstractControllerTest;
import ru.khalitovaae.restaurantvoting.web.json.JsonUtil;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.khalitovaae.restaurantvoting.web.testdata.UserTestData.*;
import static ru.khalitovaae.restaurantvoting.web.user.ProfileController.URL;

class ProfileControllerTest extends AbstractControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(user));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(URL))
                .andExpect(status().isNoContent());
        Assertions.assertNull(userRepository.getByEmail(USER_MAIL));
    }

    @Test
    void register() throws Exception {
        User newUser = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, "newPass")))
                .andDo(print())
                .andExpect(status().isCreated());

        User created = MATCHER.readFromJson(action);
        int newId = created.id();
        newUser.setId(newId);
        MATCHER.assertMatch(created, newUser);
        MATCHER.assertMatch(userRepository.getById(newId), newUser);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void update() throws Exception {
        User updated = getUpdated();
        perform(MockMvcRequestBuilders.put(URL).contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, "newPass")))
                .andDo(print())
                .andExpect(status().isNoContent());

        MATCHER.assertMatch(userRepository.getById(USER_ID), getUpdated());
    }

    @Test
    void registerInvalid() throws Exception {
        User newUser = getNewInvalid();
        perform(MockMvcRequestBuilders.post(URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newUser)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateInvalid() throws Exception {
        User updated = getUpdated();
        updated.setEmail(null);
        updated.setName(null);
        perform(MockMvcRequestBuilders.put(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateDuplicate() throws Exception {
        User updated = getUpdated();
        updated.setEmail(ADMIN_MAIL);
        updated.setName("newName");
        perform(MockMvcRequestBuilders.put(URL).contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(ErrorType.VALIDATION_ERROR.name())));
    }
}