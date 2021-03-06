package ru.espepe.bubuka.player.dao;

import java.util.List;
import ru.espepe.bubuka.player.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table BLOCK.
 */
public class Block {

    private Long id;
    private String name;
    private String mediadir;
    private Integer fading;
    private Boolean loop;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient BlockDao myDao;

    private List<Play> playList;
    private List<Track> trackList;

    public Block() {
    }

    public Block(Long id) {
        this.id = id;
    }

    public Block(Long id, String name, String mediadir, Integer fading, Boolean loop) {
        this.id = id;
        this.name = name;
        this.mediadir = mediadir;
        this.fading = fading;
        this.loop = loop;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBlockDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMediadir() {
        return mediadir;
    }

    public void setMediadir(String mediadir) {
        this.mediadir = mediadir;
    }

    public Integer getFading() {
        return fading;
    }

    public void setFading(Integer fading) {
        this.fading = fading;
    }

    public Boolean getLoop() {
        return loop;
    }

    public void setLoop(Boolean loop) {
        this.loop = loop;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Play> getPlayList() {
        if (playList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PlayDao targetDao = daoSession.getPlayDao();
            List<Play> playListNew = targetDao._queryBlock_PlayList(id);
            synchronized (this) {
                if(playList == null) {
                    playList = playListNew;
                }
            }
        }
        return playList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetPlayList() {
        playList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Track> getTrackList() {
        if (trackList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TrackDao targetDao = daoSession.getTrackDao();
            List<Track> trackListNew = targetDao._queryBlock_TrackList(id);
            synchronized (this) {
                if(trackList == null) {
                    trackList = trackListNew;
                }
            }
        }
        return trackList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetTrackList() {
        trackList = null;
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
