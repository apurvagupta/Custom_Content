package com.jivesoftware.community.ext.memo;

import java.util.Date;

import com.jivesoftware.community.CommentContentResource;
import com.jivesoftware.community.ImageContentResource;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;

public interface Memo extends JiveContentObject, CommentContentResource, ImageContentResource {

    public void setStatus(Status status);
    
    public void setCreationDate(Date creationDate);
    
    public void setModificationDate(Date modificationDate);
    
    public String getTitle();
    
    public void setTitle(String title);
    
    public String getDescription();
    
    public void setDescription(String description);
    
    public JiveContainer getJiveContainer();
    
    public int getViewCount();
}
