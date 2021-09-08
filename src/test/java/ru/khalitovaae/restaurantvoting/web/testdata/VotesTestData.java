package ru.khalitovaae.restaurantvoting.web.testdata;

import lombok.experimental.UtilityClass;
import ru.khalitovaae.restaurantvoting.model.Vote;
import ru.khalitovaae.restaurantvoting.to.VoteResultTo;
import ru.khalitovaae.restaurantvoting.web.MatcherFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.khalitovaae.restaurantvoting.web.testdata.RestaurantTestData.metropol;
import static ru.khalitovaae.restaurantvoting.web.testdata.RestaurantTestData.pushkin;

@UtilityClass
public class VotesTestData {

    public static final MatcherFactory.Matcher<Vote> VOTE_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Vote.class,
                    "user", "restaurant.dishes");

    public static final MatcherFactory.Matcher<Vote> VOTE_TIME_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Vote.class,
                    "user", "restaurant.dishes", "time");

    public static MatcherFactory.Matcher<VoteResultTo> VOTE_RESULT_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(VoteResultTo.class, "restaurant.dishes");

    public static final int VOTE_ID_TODAY = 20;

    public static final Vote vote_today = new Vote(VOTE_ID_TODAY, UserTestData.admin, metropol, LocalDate.now(), LocalTime.of(0, 10, 0));
    public static final Vote vote_prev_user = new Vote(21, UserTestData.user, pushkin, LocalDate.of(2021, 8, 20), LocalTime.of(9,0,0));
    public static final Vote vote_prev_admin = new Vote(22, UserTestData.admin, metropol, LocalDate.of(2021, 8, 20), LocalTime.of(10,0, 0));

    public static List<VoteResultTo> getVotingResults() {
        return List.of(new VoteResultTo(metropol, 1L));
    }

    public static List<Vote> getAllVotesAdmin() {
        return List.of(vote_today, vote_prev_admin);
    }

    public static Vote getUpdated(Clock clock) {
        return new Vote(VOTE_ID_TODAY, UserTestData.admin, pushkin, LocalDate.now(clock), LocalTime.now(clock));
    }

    public static Vote getNew(Clock clock) {
        return new Vote(null, UserTestData.user, pushkin, LocalDate.now(clock), LocalTime.now(clock));
    }
}
