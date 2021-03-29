package com.example.UniversalConverter;

import com.opencsv.exceptions.CsvValidationException;

import java.io.Closeable;
import java.io.IOException;

public interface ResourceReader_I extends Closeable, AutoCloseable {


    String[] getNextValues() throws IOException, CsvValidationException;
}
