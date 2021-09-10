package ru.khalitovaae.restaurantvoting.web.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.khalitovaae.restaurantvoting.model.User;
import ru.khalitovaae.restaurantvoting.service.UserService;

import java.util.List;

import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.assureIdConsistent;
import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.checkNew;

public abstract class AbstractUserController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final UserService service;

    protected AbstractUserController(UserService service) {
        this.service = service;
    }

    protected User get(int id) {
        log.info("get {}", id);
        return service.get(id);
    }

    protected List<User> getAll() {
        log.info("getAll");
        return service.getAll();
    }

    protected void delete(int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    protected User getWithVotes(int id) {
        log.info("get with votes {}", id);
        return service.getWithVotes(id);
    }

    protected List<User> getAllWithVotes() {
        log.info("get all with votes");
        return service.getAllWithVotes();
    }

    protected void update(User user, int id) {
        log.info("update {} with id={}", user, id);
        assureIdConsistent(user, id);
        service.update(user);
    }

    protected User getByMail(String email) {
        log.info("getByEmail {}", email);
        return service.getByEmail(email);
    }

    protected User createNew(User user) {
        log.info("create {}", user);
        checkNew(user);
        return service.create(user);
    }
}
