package ru.moskalkov.vdcom.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.moskalkov.vdcom.dto.UserRequestDto;
import ru.moskalkov.vdcom.dto.UserResponseDto;
import ru.moskalkov.vdcom.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    static MultipartFile file;
    static Long id;
    static UserRequestDto requestDto;
    static List<UserResponseDto> mockUsersResponseDtoList;

    @BeforeAll
    public static void init() {
        file = mock(MultipartFile.class);
        id = 1L;
        requestDto = mock(UserRequestDto.class);
        mockUsersResponseDtoList = Arrays.asList(new UserResponseDto(), new UserResponseDto());
    }

    @Test
    void getAllUsers_shouldGetAllUsers() {
        when(userService.getAll()).thenReturn(mockUsersResponseDtoList);

        ResponseEntity<List<UserResponseDto>> response = userController.getAllUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUsersResponseDtoList, response.getBody());
        verify(userService).getAll();
    }

    @Test
    void create_shouldCreateNewUser() {
        when(userService.create(requestDto)).thenReturn(new UserResponseDto().getId());

        ResponseEntity<Long> response = userController.create(requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).create(any());
    }

    @Test
    void update_shouldUpdateUserById() {
        when(userService.update(id, requestDto)).thenReturn(new UserResponseDto());

        ResponseEntity<UserResponseDto> response = userController.update(id, requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).update(id, requestDto);
    }

    @Test
    void delete_shouldDeleteUserById() {
        userController.delete(id);
        verify(userService).delete(id);
    }

    @Test
    void uploadCsv_shouldUploadCsvFile() {

        doNothing().when(userService).saveFromCsv(file);

        ResponseEntity<String> response = userController.uploadCsv(file);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).saveFromCsv(file);
    }
}