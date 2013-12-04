package com.jivesoftware.community.ext.memo.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.jivesoftware.base.UnauthorizedException;
import com.jivesoftware.base.User;
import com.jivesoftware.community.CommentDelegator;
import com.jivesoftware.community.CommentDelegatorImpl;
import com.jivesoftware.community.CommentManager;
import com.jivesoftware.community.Image;
import com.jivesoftware.community.ImageException;
import com.jivesoftware.community.JiveConstants;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContext;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.event.ImageEvent;
import com.jivesoftware.community.impl.DatabaseObjectIterator;
import com.jivesoftware.community.impl.DbImage;
import com.jivesoftware.community.impl.DbImageManager;
import com.jivesoftware.community.impl.MemoImageHelper;
import com.jivesoftware.util.LongList;
import com.jivesoftware.util.SimpleDataSource;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.impl.MemoConverter.BodyProvider;

import com.jivesoftware.community.impl.ListJiveIterator;
import com.jivesoftware.community.lifecycle.JiveApplication;
import com.jivesoftware.community.objecttype.JiveObjectType;
import com.jivesoftware.community.ext.memo.MemoObjectType;

public class MemoImpl implements Memo {
    private static final Logger log = Logger.getLogger(MemoImpl.class);

    private JiveObjectType jiveObjectType;
    
    private long id;
    private JiveContainer container;
    private User user;
    private Status status;
    private Date creationDate;
    private Date modificationDate;
    private String title;
    private String description;
    private LongList images;
    
    private ViewCountProvider viewCountProvider;
    private BodyProvider body;
    
    public MemoImpl() {
    }

    public MemoImpl(long id, User user, JiveContainer container, JiveObjectType objectType, ViewCountProvider viewCountProvider, BodyProvider body, LongList images) {
        this.id = id;
        this.user = user;
        this.container = container;
        this.jiveObjectType = objectType;
        this.viewCountProvider = viewCountProvider;
        this.body = body;
        this.images = images;
    }

    public JiveObjectType getJiveObjectType() {
        return jiveObjectType;
    }
    
    public int getObjectType() {
        return jiveObjectType == null ? MemoObjectType.MEMO_TYPE_ID : jiveObjectType.getID();
    }

    public long getID() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public JiveContainer getJiveContainer() {
        return container;
    }
    
    public long getContainerID() {
        return container.getID();
    }
    
    public int getContainerType() {
        return container.getObjectType();
    }
 
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
       this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
       this.description = description;
    }
    
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getPlainBody() {
        return description;
    }

    public String getPlainSubject() {
        return title;
    }

    public String getSubject() {
        return title;
    }

    public String getUnfilteredSubject() {
        return title;
    }

    public Document getBody() {
        return body.get();
    }

    public JiveIterator<User> getAuthors() {
        return new ListJiveIterator<User>(Collections.singletonList(getUser()));
    }

    public CommentDelegator getCommentDelegator() {
        if (this.getID() == -1) {
            throw new IllegalStateException("Cannot retrieve comment manager prior to memo being saved.");
        }
        return new CommentDelegatorImpl(this, getContext());
    }

    public int getCommentStatus() {
        return CommentManager.COMMENTS_OPEN;
    }

    protected JiveContext getContext() {
        return JiveApplication.getContext();
    }

    public int getViewCount() {
        return viewCountProvider.getViewCount(this);
    }
    
    public void addImage(Image image) throws IllegalStateException, ImageException, UnauthorizedException {
        if (image.getJiveContentObject() != null) {
            throw new IllegalStateException("Unable to add image " + image.getID() + " to review " + getID() + ": image is already associated with object with ID "
                + image.getJiveContentObject().getID() + " and type " + image.getJiveContentObject().getObjectType());
        }
                            
        if (getImageCount() > getImageManager().getMaxImagesPerObject()) {
            throw new ImageException(ImageException.TOO_MANY_IMAGES);
        }
                            
        synchronized (images) {
            images.add(image.getID());
        }
                            
        fireImageEvent(image, ImageEvent.Type.ADDED);
    }
                            
    public Image createImage(String name, String contentType, InputStream data) throws IllegalStateException, ImageException, UnauthorizedException {
        if (getImageCount() > getImageManager().getMaxImagesPerObject()) {
            throw new ImageException(ImageException.TOO_MANY_IMAGES);
        }
                            
        SimpleDataSource dataSource = new SimpleDataSource();
        dataSource.setContentType(contentType);
        dataSource.setName(name);
        dataSource.setInputStream(data);
                            
        Image image = getImageManager().createImage(this, dataSource);
                            
        synchronized (images) {
            images.add(image.getID());
        }
                            
        fireImageEvent(image, ImageEvent.Type.ADDED);
                            
        return image;
    }
                           
    public void deleteImage(Image image) throws ImageException, UnauthorizedException {
        if (!images.contains(image.getID())) {
            throw new IllegalArgumentException("Image " + image.getID() + " does not belong to review " + id);
        }
                            
        DbImage dbImage;
        if (image instanceof DbImage) {
            dbImage = (DbImage) image;
        } else {
            try {
                dbImage = (DbImage) getImage(image.getID());
            } catch (Exception anfe) {
                throw new ImageException(ImageException.GENERAL_ERROR, anfe);
            }
        }
    
        fireImageEvent(image, ImageEvent.Type.DELETED);
   
        try {
            MemoImageHelper.deleteImage(dbImage);
            synchronized (images) {
                images.remove(images.indexOf(dbImage.getID()));
            }
        } catch (Exception e) {
            throw new ImageException(ImageException.GENERAL_ERROR, e);
        }
    }
                
    public Image getImage(long imageID) throws IllegalArgumentException, ImageException {
        try {
            DbImage image = getImageManager().getImage(imageID);

            if (!images.contains(imageID)) {
                throw new IllegalArgumentException("Image " + imageID + " does not belong to review " + id);
            }

            if (image != null) {
                return image;
            } else {
                throw new ImageException("Unable to load image " + imageID);
            }
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            throw new ImageException(ImageException.GENERAL_ERROR);
        }
    }

    public int getImageCount() {
        synchronized (images) {
            return images.size();
        }
    }

    public JiveIterator<Image> getImages() {
        return new DatabaseObjectIterator<Image>(JiveConstants.IMAGE, images.toList());
    }

    protected void fireImageEvent(Image image, ImageEvent.Type type) {
        Map<String, Object> paramMap = Collections.emptyMap();
        JiveApplication.getContext().getEventDispatcher().fire(new ImageEvent(type, this, image, paramMap));
    }

    protected DbImageManager getImageManager() {
        return (DbImageManager) getContext().getImageManager();
    }
    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof MemoImpl)) {
            return false;
        }
        
        MemoImpl that = (MemoImpl)object;
        
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(this.jiveObjectType, that.jiveObjectType);
        builder.append(this.id, that.id);
        builder.append(this.user, that.user);
        builder.append(this.container, that.container);
        builder.append(this.title, that.title);
        builder.append(this.description, that.description);
        builder.append(this.status, that.status);
        builder.append(this.creationDate, that.creationDate);
        builder.append(this.modificationDate, that.modificationDate);
        
        return builder.isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(jiveObjectType);
        builder.append(id);
        builder.append(user);
        builder.append(container);
        builder.append(title);
        builder.append(description);
        builder.append(status);
        builder.append(creationDate);
        builder.append(modificationDate);
        
        return builder.hashCode();
    }
}
