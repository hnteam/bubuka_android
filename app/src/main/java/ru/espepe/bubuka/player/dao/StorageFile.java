package ru.espepe.bubuka.player.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table STORAGE_FILE.
 */
public class StorageFile {

    private Long id;
    private String type;
    private Integer identity;
    private String name;
    private String path;
    private Integer version;

    public StorageFile() {
    }

    public StorageFile(Long id) {
        this.id = id;
    }

    public StorageFile(Long id, String type, Integer identity, String name, String path, Integer version) {
        this.id = id;
        this.type = type;
        this.identity = identity;
        this.name = name;
        this.path = path;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
