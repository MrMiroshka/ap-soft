package ru.miroshka.apsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.miroshka.apsoft.model.File;
import ru.miroshka.apsoft.service.FileService;

import java.util.List;


@RestController
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Добавляем новый file
     *
     * @param file - файл, который нужно добавить
     * @return - возвращаем HTTP статус 201 Created
     */
    @PostMapping("/uploadFile")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        try {
            File targetFile = fileService.uploadFile(file);
            return targetFile != null
                    ? ResponseEntity
                            .ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + targetFile.getOriginalName() + "\"")
                            .body(targetFile.getTextFile())
                    : ResponseEntity
                            .badRequest()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + targetFile.getOriginalName() + "\"")
                            .body("Возникли проблемы при обработки файла.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Упс. Что-то пошло не так" + " => " + e.getMessage() + "\n");

        }
    }


    /**
     * Получаем список всех имеющихся файлов
     *
     * @return если список не null и не пуст, возвращаем сисок и HTTP статус 200 OK
     * иначе возвращаем HTTP статус 404 Not Found
     */
    @GetMapping(value = "/files")
    public ResponseEntity<List<Integer>> read() {
        final List<Integer> listFilesId = fileService.readAll();

        return listFilesId != null && !listFilesId.isEmpty()
                ? new ResponseEntity<>(listFilesId, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Получаем файл по его id
     *
     * @param id - уникальный идентификатор файла
     * @return если файл найден возвращает файл и статус HTTP 200 OK,
     * иначе HTTP статус 404 Not Found
     */
    @GetMapping(value = "/file/{id}")
    @ResponseBody
    public ResponseEntity read(@PathVariable(name = "id") int id) {
        final File file = fileService.read(id);

        return file != null
                ? ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalName() + "\"")
                    .body(file.getTextFile())
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    /**
     * Удаление существующего файла (поиск по id)
     *
     * @param id - уникальный идентификатор файла
     * @return если удалось удалить файл, то вернет HTTP статус 200 OK,
     * мначе вернет HTTP статус 304 Not Modified
     */
    @DeleteMapping(value = "/del/{id}")
    public ResponseEntity delete(@PathVariable(name = "id") int id) {
        final boolean deleted = fileService.delete(id);

        return deleted
                ? ResponseEntity.ok("Файл - id = {" + id + "}, успешно удален !\n")
                : ResponseEntity.badRequest().body("Файл - id = {" + id + "}, не удален ! Так как не был найден!\n");
    }
}
