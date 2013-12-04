package com.jivesoftware.community.ext.memo.impl;

import org.w3c.dom.Document;

import com.jivesoftware.base.User;
import com.jivesoftware.base.UserManager;
import com.jivesoftware.base.UserNotFoundException;
import com.jivesoftware.community.EntityDescriptor;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContainerManager;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoBean;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.impl.MemoImageHelper;
import com.jivesoftware.community.impl.RenderCacheManagerImpl;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.jivesoftware.community.objecttype.JiveObjectType;
import com.jivesoftware.util.LongList;

public class MemoConverter {
    private static final BodyProvider NULL_BODY_PROVIDER = new BodyProvider(null, null, null) {
        public Document get() {
            return null;
        }
    };

    private UserManager userManager;
    private JiveContainerManager containerManager;
    private RenderCacheManagerImpl renderCacheManager;
    private BeanProvider<MemoObjectType> objectTypeProvider;
    private ViewCountProvider viewCountProvider;
    private MemoImageHelper imageHelper;

    public Memo convert(MemoBean bean) {
        if (bean == null) {
            return null;
        }

        JiveObjectType memoObjectType = objectTypeProvider.get();

        User user;
        try {
            user = userManager.getUser(bean.getUserID());
        } catch (UserNotFoundException e) {
            return null;
        }

        JiveContainer container;
        try {
            container = containerManager.getJiveContainer(bean.getContainerType(), bean.getContainerID());
        } catch (NotFoundException e) {
            return null;
        }

        BodyProvider bodyProvider = this.getBodyProvider(bean, memoObjectType);
        LongList images = imageHelper.getImages(bean.getID(), memoObjectType.getID());
        return loadObject(bean, user, container, memoObjectType, viewCountProvider, bodyProvider, images);
    }

    protected MemoImpl loadObject(MemoBean bean, User user, JiveContainer container, JiveObjectType objectType, ViewCountProvider viewCountProvider, BodyProvider bodyProvider, LongList images) {
        MemoImpl memo = new MemoImpl(bean.getID(), user, container, objectType, viewCountProvider, bodyProvider, images);

        memo.setTitle(bean.getTitle());
        memo.setDescription(bean.getDescription());
        memo.setCreationDate(bean.getCreationDate());
        memo.setModificationDate(bean.getModificationDate());
        memo.setStatus(Status.valueOf(bean.getStatusID()));
        return memo;
    }

    public void setImageHelper(MemoImageHelper imageHelper) {
        this.imageHelper = imageHelper;
    }
    
    protected BodyProvider getBodyProvider(MemoBean bean, JiveObjectType memoObjectType) {
        BodyProvider bodyProvider;
        if (bean.getDescription() != null) {
            bodyProvider = new BodyProvider(renderCacheManager, new EntityDescriptor(memoObjectType.getID(), bean.getID()), bean.getDescription());
        } else {
            bodyProvider = NULL_BODY_PROVIDER;
        }

        return bodyProvider;
    }
    
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setContainerManager(JiveContainerManager containerManager) {
        this.containerManager = containerManager;
    }

    public void setRenderCacheManager(RenderCacheManagerImpl renderCacheManager) {
        this.renderCacheManager = renderCacheManager;
    }

    public void setObjectTypeProvider(BeanProvider<MemoObjectType> objectTypeProvider) {
        this.objectTypeProvider = objectTypeProvider;
    }
    
    public void setViewCountProvider(ViewCountProvider viewCountProvider) {
        this.viewCountProvider = viewCountProvider;
    }

    public static class BodyProvider {
        protected final RenderCacheManagerImpl renderCacheManagerImpl;
        protected final EntityDescriptor bookmarkDescriptor;
        protected final String sourceXml;

        public BodyProvider(RenderCacheManagerImpl renderCacheManagerImpl, EntityDescriptor bookmarkDescriptor, String sourceXml) {
            this.renderCacheManagerImpl = renderCacheManagerImpl;
            this.sourceXml = sourceXml;
            this.bookmarkDescriptor = bookmarkDescriptor;
        }

        public org.w3c.dom.Document get() {
            return renderCacheManagerImpl.retrieveXmlDocument(bookmarkDescriptor, sourceXml);
        }

        public String getSourceXml() {
            return sourceXml;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BodyProvider)) {
                return false;
            }

            BodyProvider that = (BodyProvider) o;

            return !(sourceXml != null ? !sourceXml.equals(that.sourceXml) : that.sourceXml != null);
        }

        public int hashCode() {
            return (sourceXml != null ? sourceXml.hashCode() : 0);
        }

        public String toString() {
            return String.format("BodyProvider{bookmarkDescriptor=%s, sourceXml='%s'}", bookmarkDescriptor, sourceXml);
        }
    }
}
