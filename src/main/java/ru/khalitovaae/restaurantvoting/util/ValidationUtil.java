package ru.khalitovaae.restaurantvoting.util;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;
import ru.khalitovaae.restaurantvoting.HasId;
import ru.khalitovaae.restaurantvoting.to.Menu;
import ru.khalitovaae.restaurantvoting.util.exception.ErrorType;
import ru.khalitovaae.restaurantvoting.util.exception.IllegalRequestDataException;
import ru.khalitovaae.restaurantvoting.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;

@UtilityClass
public class ValidationUtil {
    private static final Validator validator;

    static {
        //  From Javadoc: implementations are thread-safe and instances are typically cached and reused.
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        //  From Javadoc: implementations of this interface must be thread-safe
        validator = factory.getValidator();
    }

    public static <T> void validate(T bean) {
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public static void notFoundWithId(boolean condition, String message, int id) {
        notFound(condition, message + id);
    }

    public static void notFound(boolean condition, String message) {
        if (condition) {
            throw new NotFoundException(message);
        }
    }

    public static void checkIllegalRequestWithId(boolean condition, String message, int userId) {
        checkIllegalRequest(condition, message + userId);
    }

    public static void checkIllegalRequest(boolean condition, String message) {
        if (condition) {
            throw new IllegalRequestDataException(message);
        }
    }

    public static void checkNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException(bean + " must be new (id=null)");
        }
    }

    public static void assureIdConsistent(HasId bean, int id) {
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.id() != id) {
            throw new IllegalRequestDataException(bean + " must be with id=" + id);
        }
    }

    public static void assureDayConsistent(Menu menu, LocalDate day) {
        if (menu.getDay() == null) {
            menu.setDay(day);
        } else if (!menu.getDay().equals(day)) {
            throw new IllegalRequestDataException("You can create/update menu only for day=" + day);
        }
    }

    //  https://stackoverflow.com/a/65442410/548473
    @NonNull
    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }

    public static String getMessage(Throwable e) {
        return e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getClass().getName();
    }

    public static Throwable logAndGetRootCause(Logger log, HttpServletRequest req, Exception e, boolean logStackTrace, ErrorType errorType) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        if (logStackTrace) {
            log.error(errorType + " at request " + req.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request  {}: {}", errorType, req.getRequestURL(), rootCause.toString());
        }
        return rootCause;
    }
}
