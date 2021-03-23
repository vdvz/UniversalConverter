package com.example.UniversalConverter;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConversionRulesReader implements Closeable, AutoCloseable {

    private final CSVReader reader;
    private final FileReader fr;

    public ConversionRulesReader(String pathToResourceWithRules) throws FileNotFoundException {
        fr = new FileReader(pathToResourceWithRules);
        reader = new CSVReader(fr);
    }

    public String[] getNextValues() throws IOException, CsvValidationException {
        return reader.readNext();
    }

    @Override
    public void close() throws IOException {
        reader.close();
        fr.close();
    }
}
