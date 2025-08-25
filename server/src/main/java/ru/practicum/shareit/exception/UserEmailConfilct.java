package ru.practicum.shareit.exception;

public class UserEmailConfilct extends RuntimeException {
    public UserEmailConfilct(String message) {
        super(message);
    }
}
