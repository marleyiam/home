package com.sismics.home.core.model.dbi;

import com.google.common.base.Objects;

import java.util.Date;

/**
 * Camera.
 * 
 * @author bgamard
 */
public class Camera {
    /**
     * ID.
     */
    private String id;
    
    /**
     * Name.
     */
    private String name;
    
    /**
     * Stored pictures folder.
     */
    private String folder;
    
    /**
     * Latest picture.
     */
    private String current;
    
    /**
     * Creation date.
     */
    private Date createDate;
    
    /**
     * Deletion date.
     */
    private Date deleteDate;

    public Camera() {
    }

    public Camera(String id, String name, Date createDate, Date deleteDate, String folder, String current) {
        this.id = id;
        this.name = name;
        this.createDate = createDate;
        this.deleteDate = deleteDate;
        this.folder = folder;
        this.current = current;
    }

    /**
     * Getter of id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter of id.
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter of name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of name.
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of createDate.
     *
     * @return createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Setter of createDate.
     *
     * @param createDate createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Getter of deleteDate.
     *
     * @return deleteDate
     */
    public Date getDeleteDate() {
        return deleteDate;
    }

    /**
     * Setter of deleteDate.
     *
     * @param deleteDate deleteDate
     */
    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    /**
     * Getter of current.
     * @return current
     */
    public String getCurrent() {
        return current;
    }

    /**
     * Setter of current.
     * @param current current
     */
    public void setCurrent(String current) {
        this.current = current;
    }
    
    /**
     * Getter of folder.
     * @return folder
     */
    public String getFolder() {
        return folder;
    }

    /**
     * Setter of folder.
     * @param folder folder
     */
    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("folder", folder)
                .toString();
    }
}
