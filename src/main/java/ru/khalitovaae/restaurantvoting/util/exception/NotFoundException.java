package ru.khalitovaae.restaurantvoting.util.exception;

public class NotFoundException extends IllegalRequestDataException {
    public NotFoundException(String msg) {
        super(msg);
    }
}