package ru.moskalkov.vdcom.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.moskalkov.vdcom.dto.UserRequestDto;
import ru.moskalkov.vdcom.dto.UserResponseDto;
import ru.moskalkov.vdcom.entity.User;
import ru.moskalkov.vdcom.exeption.ReadFromFileException;
import ru.moskalkov.vdcom.exeption.UserAlreadyExistsException;
import ru.moskalkov.vdcom.repository.UserRepository;
import ru.moskalkov.vdcom.service.CsvService;
import ru.moskalkov.vdcom.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CsvService csvService;
    private final ModelMapper modelMapper;

    @Override
    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Long create(UserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(HttpStatus.BAD_REQUEST, "User already exists");
        } else {
            final User user = userRepository.save(modelMapper.map(request, User.class));
            return user.getId();
        }
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto request) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        modelMapper.map(request, user);

        userRepository.save(user);

        return modelMapper.map(user, UserResponseDto.class);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void saveFromCsv(MultipartFile file) throws UserAlreadyExistsException, ReadFromFileException {
        List<UserRequestDto> userRequestDtoList = csvService.readFromCsv(file, UserRequestDto.class);
        this.saveFromList(userRequestDtoList);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFromList(List<UserRequestDto> userRequestDtoList) throws UserAlreadyExistsException {
        userRequestDtoList.forEach(userRequestDto -> {
            final User user = modelMapper.map(userRequestDto, User.class);

            if (userRepository.existsByEmail(user.getEmail())) {
                throw new UserAlreadyExistsException(HttpStatus.BAD_REQUEST, "User already exists");
            }

            userRepository.save(user);
        });
    }
}
