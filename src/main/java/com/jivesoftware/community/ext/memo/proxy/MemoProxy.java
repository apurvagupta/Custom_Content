package com.jivesoftware.community.ext.memo.proxy;

import java.io.InputStream;
import java.util.Date;

import org.w3c.dom.Document;

import com.jivesoftware.base.AuthToken;
import com.jivesoftware.base.UnauthorizedException;
import com.jivesoftware.base.User;
import com.jivesoftware.base.proxy.JiveProxy;
import com.jivesoftware.base.proxy.ProxyUtils;
import com.jivesoftware.community.CommentDelegator;
import com.jivesoftware.community.Image;
import com.jivesoftware.community.ImageException;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.aaa.authz.EntitlementTypeProvider.EntitlementType;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.objecttype.EntitlementCheckProvider;
import com.jivesoftware.community.objecttype.JiveObjectType;
import com.jivesoftware.community.proxy.ImageProxy;
import com.jivesoftware.community.proxy.IteratorProxy;

public class MemoProxy implements Memo, JiveProxy<Memo> {
    private Memo memo;
    private AuthToken authToken;
    private EntitlementCheckProvider<Memo> entitlementCheckProvider;

    public AuthToken getProxyAuthToken() {
        return authToken;
    }

    public Memo getUnproxiedObject() {
        return memo;
    }

    public void init(Memo target, AuthToken authToken) {
        this.memo = target;
        this.authToken = authToken;
    }

    protected boolean isAllowedToEdit() {
        return entitlementCheckProvider.isUserEntitled(memo, EntitlementType.EDIT);
    }

    public long getContainerID() {
        return memo.getContainerID();
    }

    public int getContainerType() {
        return memo.getContainerType();
    }

    public long getID() {
        return memo.getID();
    }

    public int getObjectType() {
        return memo.getObjectType();
    }

    public JiveObjectType getJiveObjectType() {
        return memo.getJiveObjectType();
    }

    public String getTitle() {
        return memo.getTitle();
    }
    
    public void setTitle(String title) {
        if (isAllowedToEdit()) {
            memo.setTitle(title);
        } else {
            throw new UnauthorizedException();
        }
    
    }
    
    public String getDescription() {
        return memo.getDescription();
    }
    
    public void setDescription(String description) {
        if (isAllowedToEdit()) {
            memo.setDescription(description);
        } else {
            throw new UnauthorizedException();
        }
    
    }
    

    @Override
    public int hashCode() {
        return memo.hashCode();
    }

    public Document getBody() {
        return memo.getBody();
    }

    public Date getCreationDate() {
        return memo.getCreationDate();
    }

    public Date getModificationDate() {
        return memo.getModificationDate();
    }

    public String getPlainBody() {
        return memo.getPlainBody();
    }

    public String getPlainSubject() {
        return memo.getPlainSubject();
    }

    public Status getStatus() {
        return memo.getStatus();
    }

    public String getSubject() {
        return memo.getSubject();
    }

    public String getUnfilteredSubject() {
        return memo.getUnfilteredSubject();
    }

    public User getUser() {
        return memo.getUser();
    }

    public JiveIterator<User> getAuthors() {
        return memo.getAuthors();
    }

    public void setCreationDate(Date creationDate) {
        if (isAllowedToEdit()) {
            memo.setCreationDate(creationDate);
        } else {
            throw new UnauthorizedException();
        }
    }

    public void setModificationDate(Date modificationDate) {
        if (isAllowedToEdit()) {
            memo.setModificationDate(modificationDate);
        } else {
            throw new UnauthorizedException();
        }
    }

    public CommentDelegator getCommentDelegator() {
        return memo.getCommentDelegator();
    }

    public int getCommentStatus() {
        return memo.getCommentStatus();
    }
    public void setStatus(Status status) {
        if (isAllowedToEdit()) {
            memo.setStatus(status);
        } else {
            throw new UnauthorizedException();
        }
    }

    public JiveContainer getJiveContainer() {
        return memo.getJiveContainer();
    }

    public void setEntitlementCheckProvider(EntitlementCheckProvider<Memo> entitlementCheckProvider) {
        this.entitlementCheckProvider = entitlementCheckProvider;
    }
    
    public int getViewCount() {
        return memo.getViewCount();
    }
    
    public void addImage(Image image) throws IllegalStateException, ImageException, UnauthorizedException {
        if (isAllowedToEdit()) {
            memo.addImage(image);
        } else {
            throw new UnauthorizedException();
        }
    }
                            
    public Image createImage(String name, String contentType, InputStream data) throws IllegalStateException, ImageException, UnauthorizedException {
        if (isAllowedToEdit()) {
            return ProxyUtils.proxyObject(ImageProxy.class, memo.createImage(name, contentType, data), authToken);
        } else {
            throw new UnauthorizedException();
        }
    }
                            
    public void deleteImage(Image image) throws ImageException, UnauthorizedException {
        if (isAllowedToEdit()) {
            memo.deleteImage(image);
        } else {
            throw new UnauthorizedException();
        }
    }
                            
    public Image getImage(long imageID) throws IllegalArgumentException, ImageException {
        return ProxyUtils.proxyObject(ImageProxy.class, memo.getImage(imageID), authToken);
    }
                            
    public int getImageCount() {
        return memo.getImageCount();
    }
    
    public JiveIterator<Image> getImages() {
        return new IteratorProxy<Image>(memo.getImages(), authToken);
    }
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof MemoProxy) {
            return memo.equals(((MemoProxy) object).memo);
        } else {
            return memo.equals(object);
        }
    }

    public String toString() {
        return memo.toString();
    }

}
