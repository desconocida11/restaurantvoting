package ru.khalitovaae.restaurantvoting.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;
import ru.khalitovaae.restaurantvoting.HasId;
import ru.khalitovaae.restaurantvoting.util.exception.IllegalRequestDataException;
import ru.khalitovaae.restaurantvoting.util.exception.NotFoundException;

@UtilityClass
public class ValidationUtil {

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

    //  https://stackoverflow.com/a/65442410/548473
    @NonNull
    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }
}
