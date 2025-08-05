package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserEmailConfilct;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAllUsers().stream()
                .map(UserDtoMapper::mapUserToUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(int userId) {
        return UserDtoMapper.mapUserToUserDto(userRepository.findUserById(userId).orElseThrow(() -> new NoSuchElementException("Не найден пользователь с id=" + userId)));
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getName() == null
                || userDto.getName().isBlank()
                || userDto.getEmail() == null
                || userDto.getEmail().isBlank()) {
            throw new ValidationException("Некорректные данные пользователя");
        }

        Optional<User> userByEmailOpt = userRepository.findUserByEmail(userDto.getEmail());
        if (userByEmailOpt.isPresent()) {
            throw new UserEmailConfilct("Пользователь с email " + userDto.getEmail() + " уже существует");
        }
        return UserDtoMapper.mapUserToUserDto(userRepository.create(UserDtoMapper.mapUserDtoToUser(userDto)));
    }

    @Override
    public UserDto update(int userId, UserDto userDto) {
        UserDto existingUser = getUserById(userId);

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            existingUser.setEmail(userDto.getEmail());
        }

        Optional<User> userByEmailOpt = userRepository.findUserByEmail(existingUser.getEmail());
        if (userByEmailOpt.isPresent() && userByEmailOpt.get().getId() != existingUser.getId()) {
            throw new UserEmailConfilct("Пользователь с email " + existingUser.getEmail() + " уже существует");
        }
        return UserDtoMapper.mapUserToUserDto(userRepository.update(UserDtoMapper.mapUserDtoToUser(existingUser)));
    }

    @Override
    public void delete(int userId) {
        if (!itemRepository.findItemsByUser(userId).isEmpty()) {
            throw new ValidationException("У пользователя есть вещи, удаление невозможно");
        }

        userRepository.deleteById(userId);
    }
}
