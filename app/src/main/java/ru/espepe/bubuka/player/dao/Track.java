package ru.espepe.bubuka.player.dao;

import ru.espepe.bubuka.player.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table TRACK.
 */
public class Track {

    private Long id;
    private java.util.Date startDate;
    private java.util.Date endDate;
    private String file;
    private Long block_id;
    private Long file_id;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient TrackDao myDao;

    private Block block;
    private Long block__resolvedKey;

    private StorageFile storageFile;
    private Long storageFile__resolvedKey;


    public Track() {
    }

    public Track(Long id) {
        this.id = id;
    }

    public Track(Long id, java.util.Date startDate, java.util.Date endDate, String file, Long block_id, Long file_id) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.file = file;
        this.block_id = block_id;
        this.file_id = file_id;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTrackDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Long getBlock_id() {
        return block_id;
    }

    public void setBlock_id(Long block_id) {
        this.block_id = block_id;
    }

    public Long getFile_id() {
        return file_id;
    }

    public void setFile_id(Long file_id) {
        this.file_id = file_id;
    }

    /** To-one relationship, resolved on first access. */
    public Block getBlock() {
        Long __key = this.block_id;
        if (block__resolvedKey == null || !block__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BlockDao targetDao = daoSession.getBlockDao();
            Block blockNew = targetDao.load(__key);
            synchronized (this) {
                block = blockNew;
            	block__resolvedKey = __key;
            }
        }
        return block;
    }

    public void setBlock(Block block) {
        synchronized (this) {
            this.block = block;
            block_id = block == null ? null : block.getId();
            block__resolvedKey = block_id;
        }
    }

    /** To-one relationship, resolved on first access. */
    public StorageFile getStorageFile() {
        Long __key = this.file_id;
        if (storageFile__resolvedKey == null || !storageFile__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StorageFileDao targetDao = daoSession.getStorageFileDao();
            StorageFile storageFileNew = targetDao.load(__key);
            synchronized (this) {
                storageFile = storageFileNew;
            	storageFile__resolvedKey = __key;
            }
        }
        return storageFile;
    }

    public void setStorageFile(StorageFile storageFile) {
        synchronized (this) {
            this.storageFile = storageFile;
            file_id = storageFile == null ? null : storageFile.getId();
            storageFile__resolvedKey = file_id;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
