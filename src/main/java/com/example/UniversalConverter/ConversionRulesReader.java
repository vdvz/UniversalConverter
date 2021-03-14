package com.example.UniversalConverter;

import com.opencsv.CSVReader;

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

    @Override
    public void close() throws IOException {
        reader.close();
        fr.close();
    }
}
