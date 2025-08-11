package com.example.filehandlers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IFileHandler<T> {
    void create(File file , List<T>data) throws IOException;
    List<T>read(File file) throws IOException;
    Optional<T>read(File file, String identifier) throws IOException;
    boolean update(File file, T updateData, String identifier) throws IOException;
    boolean delete(File file, String identifier) throws IOException;
}
