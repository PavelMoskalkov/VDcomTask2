package ru.moskalkov.vdcom.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;
import ru.moskalkov.vdcom.dto.UserRequestDto;
import ru.moskalkov.vdcom.dto.UserResponseDto;
import ru.moskalkov.vdcom.entity.User;
import ru.moskalkov.vdcom.exeption.ReadFromFileException;
import ru.moskalkov.vdcom.exeption.UserAlreadyExistsException;
import ru.moskalkov.vdcom.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    @Spy
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CsvServiceImpl csvService;
    static User user1;
    static User user2;
    static UserResponseDto userResponseDto1;
    static UserResponseDto userResponseDto2;
    static UserResponseDto updateUser;
    static List<User> userList;
    static List<UserResponseDto> userResponseDtoList;
    static List<UserRequestDto> userRequestDtoList;
    static UserRequestDto userRequestDto1;
    static UserRequestDto userRequestDto2;
    static Long id;
    static Long nonExistingUserId;
    static MultipartFile file;


    @BeforeAll
    public static void init() {

        user1 = User.builder()
                .id(id)
                .firstname("Firstname1")
                .lastname("Lastname1")
                .email("email1@example.com")
                .build();
        user2 = User.builder()
                .id(2L)
                .firstname("Firstname2")
                .lastname("Lastname2")
                .email("email2@example.com")
                .build();
        userResponseDto1 = UserResponseDto.builder()
                .id(id)
                .firstname("Firstname1")
                .lastname("Lastname1")
                .email("email1@example.com")
                .build();
        userResponseDto2 = UserResponseDto.builder()
                .id(2L)
                .firstname("Firstname2")
                .lastname("Lastname2")
                .email("email2@example.com")
                .build();
        userRequestDto1 = UserRequestDto.builder()
                .firstname("Firstname1")
                .lastname("Lastname1")
                .email("email1@example.com")
                .build();
        userRequestDto2 = UserRequestDto.builder()
                .firstname("Firstname2")
                .lastname("Lastname2")
                .email("email1@example.com")
                .build();

        updateUser = UserResponseDto.builder()
                .firstname("Firstname1")
                .lastname("Lastname1")
                .email("email1@example.com")
                .build();

        userList = Arrays.asList(user1, user2);
        userResponseDtoList = Arrays.asList(userResponseDto1, userResponseDto2);
        userRequestDtoList = Arrays.asList(userRequestDto1, userRequestDto2);
        id = 1L;
        nonExistingUserId = 3L;
        file = mock(MultipartFile.class);
    }

    @Test
    void getAll_shouldCallRepository() {

        when(userRepository.findAll()).thenReturn(userList);
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class)))
                .thenReturn(userResponseDto1)
                .thenReturn(userResponseDto2);

        List<UserResponseDto> actualResponse = userService.getAll();

        assertEquals(userResponseDtoList, actualResponse);
        verify(userRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(User.class), eq(UserResponseDto.class));

    }

    @Test
    void create_shouldThrowExceptionWhenUserExists() {

        when(userRepository.existsByEmail(userRequestDto1.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.create(userRequestDto1));

        verify(userRepository, times(1)).existsByEmail(userRequestDto1.getEmail());
    }

    @Test
    void create_shouldSaveUserWhenUserDoesNotExist() {

        when(userRepository.existsByEmail(userRequestDto1.getEmail())).thenReturn(false);
        when(modelMapper.map(userRequestDto1, User.class)).thenReturn(user1);
        when(userRepository.save(any(User.class))).thenReturn(user1);

        Long result = userService.create(userRequestDto1);

        assertEquals(result, user1.getId());

        verify(userRepository, times(1)).existsByEmail(userRequestDto1.getEmail());
        verify(modelMapper, times(1)).map(userRequestDto1, User.class);
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void update_shouldUpdateUserAndReturnUserResponseDto() {

        when(userRepository.findById(id)).thenReturn(Optional.of(user1));
        doNothing().when(modelMapper).map(userRequestDto1, user1);
        when(modelMapper.map(user1, UserResponseDto.class)).thenReturn(updateUser);

        UserResponseDto result = userService.update(id, userRequestDto1);

        assertEquals(updateUser, result);
        verify(modelMapper).map(userRequestDto1, user1);
        verify(userRepository).save(user1);
        verify(modelMapper).map(user1, UserResponseDto.class);

    }

    @Test
    void update_shouldThrowEntityNotFoundExceptionWhenUserNotFound() {

        when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(nonExistingUserId, userRequestDto1));

        verify(userRepository, times(1)).findById(nonExistingUserId);
        verifyNoMoreInteractions(userRepository, modelMapper);
    }

    @Test
    void delete_shouldDeleteUserById() {
        userService.delete(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    void saveFromCsv_shouldBeSavedSuccessfully() {

        when(csvService.readFromCsv(file, UserRequestDto.class)).thenReturn(userRequestDtoList);
        doNothing().when(userService).saveFromList(userRequestDtoList);


        userService.saveFromCsv(file);

        verify(csvService, times(1)).readFromCsv(file, UserRequestDto.class);
        verify(userService, times(1)).saveFromList(userRequestDtoList);

    }

    @Test
    void saveFromList_shouldBeSavedSuccessfully() {
        when(modelMapper.map(any(UserRequestDto.class), eq(User.class)))
                .thenReturn(user1)
                .thenReturn(user2);
        when(userRepository.existsByEmail(user1.getEmail())).thenReturn(false);
        when(userRepository.existsByEmail(user2.getEmail())).thenReturn(false);

        userService.saveFromList(userRequestDtoList);

        verify(userRepository, times(2)).save(any(User.class));
        verify(modelMapper, times(2)).map(any(UserRequestDto.class), eq(User.class));
    }

    @Test
    void saveFromList_shouldTrowUserAlreadyExistsException() {
        when(modelMapper.map(any(UserRequestDto.class), eq(User.class)))
                .thenReturn(user1)
                .thenReturn(user2);

        when(userRepository.existsByEmail(user1.getEmail())).thenReturn(false);
        when(userRepository.existsByEmail(user2.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.saveFromList(userRequestDtoList));
        verify(modelMapper, times(2)).map(any(UserRequestDto.class), eq(User.class));
    }

    @Test
    void saveFromCsv_shouldTrowReadFromFileException() {

        when(csvService.readFromCsv(file, UserRequestDto.class)).thenThrow(ReadFromFileException.class);

        assertThrows(ReadFromFileException.class, () -> userService.saveFromCsv(file));

        verify(csvService, times(1)).readFromCsv(file, UserRequestDto.class);
    }
}