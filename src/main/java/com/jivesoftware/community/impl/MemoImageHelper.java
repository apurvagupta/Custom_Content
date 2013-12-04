package com.jivesoftware.community.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.jivesoftware.base.database.ConnectionManager;
import com.jivesoftware.base.database.dao.DAOException;
import com.jivesoftware.community.Image;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.cache.Cache;
import com.jivesoftware.community.impl.dao.ImageBean;
import com.jivesoftware.community.impl.dao.ImageDAO;
import com.jivesoftware.util.LongList;

public class MemoImageHelper {
    private static final Logger log = Logger.getLogger(MemoImageHelper.class);
    private static final String LOAD_IMAGES = "SELECT imageID FROM jiveImage WHERE objectType=? AND objectID=?";

    private Cache<Long, ImageBean> imageCache;
    private ImageDAO imageDao;
    
    public static void deleteImage(DbImage image) {
        image.delete();
    }
    
    public void saveImages(JiveObject resource, JiveIterator<Image> images) {
        for( Image image : images) {
            try {
                ImageBean bean = imageDao.getByImageID(image.getID());
                bean.setObjectID(((JiveObject)resource).getID());
                imageDao.update(bean);
                imageCache.remove(image.getID());
            }
            catch (DAOException e) {
                log.debug(e.getMessage(), e);
            }
        }

    }
    
    protected void clearImageCache(JiveIterator<Image> images) {
        for(Image image : images) {
            imageCache.remove(image.getID());
        }

    }
    
    public LongList getImages(long id, int objectType) {
        LongList imageList = new LongList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_IMAGES);
            pstmt.setInt(1, objectType);
            pstmt.setLong(2, id);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                imageList.add(rs.getLong(1));
            }
        } catch (SQLException sql) {
            log.error(sql.getMessage(), sql);
        } finally {
            ConnectionManager.close(rs, pstmt, con);
        }
        
        return imageList;
    }

    public void setImageCache(Cache<Long, ImageBean> imageCache) {
        this.imageCache = imageCache;
    }

    public void setImageDao(ImageDAO imageDao) {
        this.imageDao = imageDao;
    }
}
