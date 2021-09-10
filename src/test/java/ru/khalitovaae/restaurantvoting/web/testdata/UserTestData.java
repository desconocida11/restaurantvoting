package ru.khalitovaae.restaurantvoting.web.testdata;

import lombok.experimental.UtilityClass;
import ru.khalitovaae.restaurantvoting.model.Role;
import ru.khalitovaae.restaurantvoting.model.User;
import ru.khalitovaae.restaurantvoting.web.MatcherFactory;
import ru.khalitovaae.restaurantvoting.web.json.JsonUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.khalitovaae.restaurantvoting.web.testdata.VotesTestData.vote_prev_user;

@UtilityClass
public class UserTestData {
    public static final MatcherFactory.Matcher<User> MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(User.class, "registered", "password");
    public static MatcherFactory.Matcher<User> WITH_VOTES_MATCHER =
            MatcherFactory.usingAssertions(User.class,
//     No need use ignoringAllOverriddenEquals, see https://assertj.github.io/doc/#breaking-changes
                    (a, e) -> assertThat(a).usingRecursiveComparison()
                            .ignoringFields("registered", "votes.user", "password", "votes.restaurant.dishes").isEqualTo(e),
                    (a, e) -> {
                        throw new UnsupportedOperationException();
                    });

    public static final int USER_ID = 1;
    public static final int ADMIN_ID = 2;
    public static final int ADMIN_ONLY_ID = 3;
    public static final int NOT_FOUND = 100;
    public static final String USER_MAIL = "user@yandex.ru";
    public static final String ADMIN_MAIL = "admin@gmail.com";
    public static final String ADMIN_ONLY_MAIL = "admin_only@gmail.com";

    public static final User user = new User(USER_ID, "User", USER_MAIL, "password", Role.USER);
    public static final User admin = new User(ADMIN_ID, "Admin", ADMIN_MAIL, "admin", Role.ADMIN, Role.USER);
    public static final User admin_only = new User(ADMIN_ONLY_ID, "AdminOnly", ADMIN_ONLY_MAIL, "admin", Role.ADMIN);

    public static User getNew() {
        return new User(null, "New", "new@gmail.com", "newPass", false, new Date(), Collections.singleton(Role.USER));
    }

    public static User getNewInvalid() {
        return new User(null, null, "new@gmail.com", null, false, new Date(), Collections.singleton(Role.USER));
    }

    public static User getUpdated() {
        return new User(USER_ID, "UpdatedName", USER_MAIL, "newPass", false, new Date(), List.of(Role.ADMIN));
    }

    public static String jsonWithPassword(User user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }

    public static User getWithVotes() {
        User withVotes = new User(user);
        withVotes.setVotes(List.of(vote_prev_user));
        return withVotes;
    }
}