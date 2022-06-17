package ru.miroshka.apsoft.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.miroshka.apsoft.model.File;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FileServiceImpl implements FileService {

    // Хранилище файлов
    private static final Map<Integer, File> FILES_REP = new HashMap<>();

    // Переменная для генерации ID
    private static final AtomicInteger ID = new AtomicInteger();

    @Value("${app.upload.dir:${user.home}}")
    public String uploadDir;

    @Override
    public File uploadFile(MultipartFile file) throws Exception {

        final int fileId = ID.incrementAndGet();
        try {

            File objFile = new File();
            objFile.setId(fileId);
            objFile.setOriginalName(StringUtils.cleanPath(file.getOriginalFilename()));
            File targetFile = modifyFile(file, objFile);
            FILES_REP.put(fileId, targetFile);
            return targetFile;
            //сохраняем на жесткий диск
            //Path copyLocation = Paths.get(uploadDir + java.io.File.separator + fileId + ".txt");
            //Files.copy(new ByteArrayInputStream(objFile.getTextFile()), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            if (FILES_REP.containsKey(fileId)) {
                FILES_REP.remove(fileId);
            }
            throw new Exception(e.getMessage() + " - " + file.getOriginalFilename());
        }
    }

    /**
     * Модифицируем файл и создаем структуру разделов
     *
     * @param fileIn  исходный файл
     * @param fileOut заготовка модицированного файла
     * @return модифицрованный файл с метаданными и структурой разделов
     * @throws IOException
     */
    private File modifyFile(MultipartFile fileIn, File fileOut) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileIn.getInputStream()));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        LinkedHashMap listSection = new LinkedHashMap<>();
        int countString = -1;
        while ((line = reader.readLine()) != null) {
            int countSection = 0;
            countString++;
            for (int i = 0; i < line.length(); i++) {
                if ('#' == line.charAt(i)) {
                    countSection++;
                } else {
                    if (countSection != 0) {
                        //если в файле в разных местах в начале строки есть признак начала одного
                        // и того же раздела, кидаем ошибку
                        if (listSection.containsKey(countSection)) {
                            throw new IOException("Не верна структура разделов в файле");
                        }
                        listSection.put(countSection, countString);
                    } else if (countString == 1) {
                        throw new IOException("Не верна структура разделов в файле, первая строка не входит ни в один раздел!");
                    }
                    break;
                }
            }
            stringBuilder.append(line.substring(countSection))
                    .append(ls);
        }
        
        fileOut.setListSection(listSection);

        StringBuilder stringBuilderSectionAndText = new StringBuilder();
        stringBuilderSectionAndText.append(listSection);
        stringBuilderSectionAndText.append("\n");
        stringBuilderSectionAndText.append(stringBuilder);
        
        fileOut.setTextFile(String.valueOf(stringBuilderSectionAndText).getBytes());

        return fileOut;
    }

    @Override
    public List<Integer> readAll() {
        return new ArrayList<>((FILES_REP.keySet()).stream().toList());
    }

    @Override
    public File read(int id) {
        return FILES_REP.get(id);
    }

    @Override
    public boolean delete(int id) {
        return FILES_REP.remove(id) != null;
    }
}
