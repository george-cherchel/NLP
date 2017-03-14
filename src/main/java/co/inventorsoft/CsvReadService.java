package co.inventorsoft;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CsvReadService {

    private CsvMapper csvMapper = new CsvMapper();

    public List<CsvWrapper> parse(String fileName) {
        List<CsvWrapper> result = new ArrayList<>();
        CsvSchema csvSchema = csvMapper.schemaFor(CsvWrapper.class).withHeader().withColumnReordering(true).withColumnSeparator(',');
        try (InputStream inputStream = new FileInputStream(fileName)) {
            MappingIterator<CsvWrapper> objectMappingIterator = csvMapper.readerFor(CsvWrapper.class).with(csvSchema).readValues(inputStream);
            while (objectMappingIterator.hasNext()) {
                result.add(objectMappingIterator.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
