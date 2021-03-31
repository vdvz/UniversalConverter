package com.example.UniversalConverter;

import com.opencsv.exceptions.CsvValidationException;

import java.io.Closeable;
import java.io.IOException;

public interface ResourceReaderI extends Closeable, AutoCloseable {


    String[] getNextValues() throws IOException, CsvValidationException;
}
