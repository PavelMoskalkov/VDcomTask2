package ru.moskalkov.vdcom.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import ru.moskalkov.vdcom.dto.UserRequestDto;
import ru.moskalkov.vdcom.exeption.ReadFromFileException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CsvServiceImplTest {
    static MockMultipartFile file;
    static MockMultipartFile fileWithError;
    static String fileContent;
    static String fileContentWithError;

    @InjectMocks
    private CsvServiceImpl csvService;

    @BeforeAll
    public static void init() {
        fileContent = "id,firstname,lastname,email\n" +
                "1,TestFirstname1,TestLastname1,test1@example.com\n" +
                "2,TestFirstname2,TestLastname2,test2@example.com\n" +
                "3,TestFirstname3,TestLastname3,test3@example.com";
        file = new MockMultipartFile(
                "file.csv",
                "file.csv",
                "text/csv",
                fileContent.getBytes());

        fileContentWithError = "id,firstname,lastname,email\n" +
                "TestFirstname1,TestLastname1,test1@example.com\n" +
                "TestFirstname2,TestLastname2,test2@example.com\n" +
                "TestFirstname3,TestLastname3,test3@example.com";
        fileWithError = new MockMultipartFile(
                "file.csv",
                "file.csv",
                "text/csv",
                fileContentWithError.getBytes());
    }

    @Test
    void readFromCsv_ShouldParseDataCorrectly() throws ReadFromFileException {

        List<UserRequestDto> result = csvService.readFromCsv(file, UserRequestDto.class);

        assertEquals(3, result.size());
        assertEquals("TestFirstname1", result.get(0).getFirstname());
        assertEquals("TestLastname2", result.get(1).getLastname());
        assertEquals("test3@example.com", result.get(2).getEmail());
    }

    @Test
    void readFromCsv_ShouldThrowException() {

        ReadFromFileException exception = org.junit.jupiter.api.Assertions.assertThrows(
                ReadFromFileException.class,
                () -> csvService.readFromCsv(fileWithError, UserRequestDto.class)
        );

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
        assertEquals("Cannot read from file", exception.getReason());
    }
}