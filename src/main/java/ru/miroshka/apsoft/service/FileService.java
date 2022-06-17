package ru.miroshka.apsoft.service;

import org.springframework.web.multipart.MultipartFile;
import ru.miroshka.apsoft.model.File;
import java.util.List;

public interface FileService {
    /**
     * Создаем новый File
     * @param file - файл для создания
     */
    File uploadFile(MultipartFile file) throws Exception;

    /**
     * Возвращает список всех имеющихся файлов
     * @return список клиентов
     */
    List<Integer> readAll();

    /**
     * Возвращает файл по его ID
     * @param id - ID файла
     * @return - объект файла с заданным ID
     */
    File read(int id);


    /**
     * Удаляет файл с заданным ID
     * @param id - id файла, который нужно удалить
     * @return - true если файл был удален, иначе false
     */
    boolean delete(int id);
}