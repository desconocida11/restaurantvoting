package ru.khalitovaae.restaurantvoting.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.khalitovaae.restaurantvoting.AuthorizedUser;
import ru.khalitovaae.restaurantvoting.model.User;
import ru.khalitovaae.restaurantvoting.repository.UserRepository;

import java.util.List;

import static ru.khalitovaae.restaurantvoting.util.UserUtil.prepareToSave;
import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.notFound;
import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.notFoundWithId;

@Service("userService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @CacheEvict(value = "users", allEntries = true)
    public User create(User user) {
        Assert.notNull(user, "user must not be null");
        return prepareAndSave(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void delete(int id) {
        notFoundWithId(repository.delete(id) != 1, "Not found entity with id=", id);
    }

    public User get(int id) {
        User user = repository.findById(id).orElse(null);
        notFoundWithId(user == null, "Not found entity with id=", id);
        return user;
    }

    public User getByEmail(String email) {
        Assert.notNull(email, "email must not be null");
        User user = repository.getByEmail(email);
        notFound(user == null, "Not found entity with email=" + email);
        return user;
    }

    @Cacheable("users")
    public List<User> getAll() {
        return repository.findAll(UserRepository.SORT_NAME_EMAIL);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void update(User user) {
        Assert.notNull(user, "user must not be null");
        prepareAndSave(user);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void enable(int id, boolean enabled) {
        User user = get(id);
        user.setEnabled(enabled);
    }

    public User getWithVotes(int id) {
        User user = repository.getWithVotes(id);
        notFoundWithId(user == null, "Not found entity with id=", id);
        return user;
    }

    @Override
    public AuthorizedUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.getByEmail(email.toLowerCase());
        notFound(user == null, "User " + email + " is not found");
        return new AuthorizedUser(user);
    }

    private User prepareAndSave(User user) {
        return repository.save(prepareToSave(user, passwordEncoder));
    }
}