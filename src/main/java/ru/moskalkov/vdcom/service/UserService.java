package ru.moskalkov.vdcom.service;

import org.springframework.web.multipart.MultipartFile;
import ru.moskalkov.vdcom.dto.UserRequestDto;
import ru.moskalkov.vdcom.dto.UserResponseDto;
import ru.moskalkov.vdcom.exeption.ReadFromFileException;
import ru.moskalkov.vdcom.exeption.UserAlreadyExistsException;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAll();

    Long create(UserRequestDto userResponseDto);

    UserResponseDto update(Long id, UserRequestDto userResponseDto);

    void delete(Long id);

    void saveFromCsv(MultipartFile file) throws UserAlreadyExistsException, ReadFromFileException;

    void saveFromList(List<UserRequestDto> userRequestDtoList) throws UserAlreadyExistsException;

}
