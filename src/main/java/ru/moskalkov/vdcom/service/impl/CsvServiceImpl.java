package ru.moskalkov.vdcom.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.moskalkov.vdcom.exeption.ReadFromFileException;
import ru.moskalkov.vdcom.service.CsvService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvServiceImpl implements CsvService {
    public <T> List<T> readFromCsv(MultipartFile file, Class<T> type) throws ReadFromFileException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReaderBuilder(reader).build()) {
            return new CsvToBeanBuilder<T>(csvReader)
                    .withType(type)
                    .build()
                    .parse();
        } catch (Exception e) {
            throw new ReadFromFileException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot read from file");
        }
    }
}
