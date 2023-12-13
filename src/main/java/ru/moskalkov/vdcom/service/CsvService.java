package ru.moskalkov.vdcom.service;

import org.springframework.web.multipart.MultipartFile;
import ru.moskalkov.vdcom.exeption.ReadFromFileException;

import java.util.List;

public interface CsvService {
    <T> List<T> readFromCsv(MultipartFile file, Class<T> type) throws ReadFromFileException;
}
