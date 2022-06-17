package ru.miroshka.apsoft.model;

import java.util.LinkedHashMap;

public class File {
    private Integer id;
    private byte[] textFile;
    private String originalName;
    private LinkedHashMap listSection;


    public LinkedHashMap getListSection() {
        return listSection;
    }

    public void setListSection(LinkedHashMap listSection) {
        this.listSection = listSection;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public byte[] getTextFile() {
        return textFile;
    }

    public void setTextFile(byte[] textFile) {
        this.textFile = textFile;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
