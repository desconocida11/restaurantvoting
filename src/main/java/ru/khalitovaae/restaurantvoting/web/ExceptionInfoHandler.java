package ru.khalitovaae.restaurantvoting.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.khalitovaae.restaurantvoting.util.ValidationUtil;
import ru.khalitovaae.restaurantvoting.util.exception.ErrorInfo;
import ru.khalitovaae.restaurantvoting.util.exception.ErrorType;
import ru.khalitovaae.restaurantvoting.util.exception.IllegalRequestDataException;
import ru.khalitovaae.restaurantvoting.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static ru.khalitovaae.restaurantvoting.util.exception.ErrorType.*;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    public static final String EXCEPTION_DUPLICATE_EMAIL = "exception.user.duplicateEmail";
    public static final String EXCEPTION_DUPLICATE_RESTAURANT_NAME = "exception.restaurant.duplicateName";
    public static final String EXCEPTION_DUPLICATE_DISH_NAME = "exception.dish.duplicateName";
    public static final String ERROR_UNDEFINED = "error.undefined";

    private static final Map<String, String> CONSTRAINS_I18N_MAP = Map.of(
            "users_unique_email_idx", EXCEPTION_DUPLICATE_EMAIL,
            "restaurants_name_unique_idx", EXCEPTION_DUPLICATE_RESTAURANT_NAME,
            "name_day_restaurant_idx", EXCEPTION_DUPLICATE_DISH_NAME);

    private final MessageSourceAccessor messageSourceAccessor;

    public ExceptionInfoHandler(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> handleError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorInfo> conflict(HttpServletRequest req, DataIntegrityViolationException e) {
        String rootMsg = ValidationUtil.getRootCause(e).getMessage();
        if (rootMsg != null) {
            String lowerCaseMsg = rootMsg.toLowerCase();
            for (Map.Entry<String, String> entry : CONSTRAINS_I18N_MAP.entrySet()) {
                if (lowerCaseMsg.contains(entry.getKey())) {
                    return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, messageSourceAccessor.getMessage(entry.getValue()));
                }
            }
        }
        return logAndGetErrorInfo(req, e, true, DATA_ERROR);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorInfo> bindValidationError(HttpServletRequest req, BindException e) {
        String[] details = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + messageSourceAccessor.getMessage(fieldError))
                .toArray(String[]::new);

        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, details);
    }

    @ExceptionHandler({IllegalRequestDataException.class,
            MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorInfo> illegalRequestDataError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, true, APP_ERROR);
    }

    private ResponseEntity<ErrorInfo> logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logStackTrace, ErrorType errorType, String... details) {
        Throwable rootCause = ValidationUtil.logAndGetRootCause(log, req, e, logStackTrace, errorType);
        String message;
        try {
            message = messageSourceAccessor.getMessage(errorType.getErrorCode());
        } catch (NoSuchMessageException exception) {
            message = messageSourceAccessor.getMessage(ERROR_UNDEFINED);
        }
        return ResponseEntity.status(errorType.getStatus())
                .body(new ErrorInfo(req.getRequestURL(), errorType,
                        message,
                        details.length != 0 ? details : new String[]{ValidationUtil.getMessage(rootCause)})
                );
    }
}