package com.jivesoftware.community.ext.memo.impl;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jivesoftware.base.User;
import com.jivesoftware.base.aaa.AuthenticationProvider;
import com.jivesoftware.base.event.v2.EventDispatcher;
import com.jivesoftware.community.ContainerAwareEntityDescriptor;
import com.jivesoftware.community.Image;
import com.jivesoftware.community.JiveConstants;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContainerManager;
import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.JiveInterceptor;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.RenderCacheManager;
import com.jivesoftware.community.ResultFilter;
import com.jivesoftware.community.TagManager;
import com.jivesoftware.community.cache.Cache;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoBean;
import com.jivesoftware.community.ext.memo.MemoEvent;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.ext.memo.MemoResultFilter;
import com.jivesoftware.community.ext.memo.dao.MemoDao;
import com.jivesoftware.community.impl.CachedPreparedStatement;
import com.jivesoftware.community.impl.DbBlockIterator;
import com.jivesoftware.community.impl.ObjectFactory;
import com.jivesoftware.community.impl.ProxyBypassHelper;
import com.jivesoftware.community.impl.QueryCacheManager;
import com.jivesoftware.community.impl.MemoImageHelper;
import com.jivesoftware.community.impl.querycache.QueryCacheKey;
import com.jivesoftware.community.internal.ExtendedCommunityManager;
import com.jivesoftware.community.internal.InvocableInterceptorManager;
import com.jivesoftware.community.moderation.ModerationUtil;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MemoManagerImpl implements MemoManager, ObjectFactory<Memo> {
    protected static final Logger log = Logger.getLogger(MemoManagerImpl.class);

    private Cache<Long, MemoBean> memoCache;
    private MemoDao memoDao;
    private QueryCacheManager queryCacheManager;
    private JiveContainerManager containerManager;
    private TagManager tagManager;
    private MemoConverter memoConverter;
    private EventDispatcher eventDispatcher;
    private RenderCacheManager renderCacheManager;
    private InvocableInterceptorManager systemInterceptorManager;
    private ExtendedCommunityManager communityManager;
    private AuthenticationProvider authenticationProvider;
    private MemoImageHelper imageHelper;

    public int getMemoCount(JiveContainer container) {
        MemoResultFilter filter = MemoResultFilter.createDefaultFilter();
        filter.setContainer(container);
        return this.getMemoCount(filter);
    }

    public int getMemoCount(MemoResultFilter resultFilter) {
        CachedPreparedStatement cachedPstmt = memoDao.getMemoListCountSQL(resultFilter);

        QueryCacheKey key;
        if (resultFilter.getUser() != null) {
            key = new QueryCacheKey(new MemoObjectType().getID(), resultFilter.getUser().getID(), cachedPstmt, -1);
        } else if (resultFilter.getContainer() != null) {
            key = new QueryCacheKey(new MemoObjectType().getID(), resultFilter.getContainer().getID(), cachedPstmt, -1);
        } else {
            key = new QueryCacheKey(new MemoObjectType().getID(), -1, cachedPstmt, -1);
        }
        return queryCacheManager.getQueryCache().getCount(key, true);
    }

    public int getAllMemosCount() {
        CachedPreparedStatement cachedPstmt = memoDao.getAllMemosCountSQL();
        QueryCacheKey key = new QueryCacheKey(new MemoObjectType().getID(), -1, cachedPstmt, -1);
        return queryCacheManager.getQueryCache().getCount(key, true);
    }

    public JiveIterator<Memo> getMemos(MemoResultFilter resultFilter) {
        CachedPreparedStatement cachedPstmt = memoDao.getMemoListSQL(resultFilter);
        final long[] docBlock = queryCacheManager.getQueryCache().getBlock(cachedPstmt, new MemoObjectType().getID(), -1, resultFilter.getStartIndex(), true);

        int startIndex = resultFilter.getStartIndex();
        int endIndex;

        if (resultFilter.getNumResults() == ResultFilter.NULL_INT) {
            endIndex = getMemoCount(resultFilter);
        } else {
            endIndex = resultFilter.getNumResults() + startIndex;
        }

        if (resultFilter.getUser() != null) {
            return new DbBlockIterator<Memo>(docBlock, cachedPstmt, startIndex, endIndex, new MemoObjectType().getID(), resultFilter.getUser().getObjectType(), resultFilter.getUser().getID(), true);
        } else if (resultFilter.getContainer() != null) {
            return new DbBlockIterator<Memo>(docBlock, cachedPstmt, startIndex, endIndex, new MemoObjectType().getID(), resultFilter.getContainer().getObjectType(), resultFilter.getContainer().getID(), true);
        } else {
            return new DbBlockIterator<Memo>(docBlock, cachedPstmt, startIndex, endIndex, new MemoObjectType().getID(), -1, resultFilter.getStartIndex(), true);
        }
    }

    public JiveIterator<Memo> getAllMemos() {
        CachedPreparedStatement cachedPstmt = memoDao.getAllMemosSQL();
        final long[] docBlock = queryCacheManager.getQueryCache().getBlock(cachedPstmt, new MemoObjectType().getID(), -1, 0, true);

        return new DbBlockIterator<Memo>(docBlock, cachedPstmt, 0, getAllMemosCount(), new MemoObjectType().getID(), -1, 0, true);
    }

    public void deleteMemo(Memo memo) {
        doDeleteMemo(memo);
        fireMemoDeleted(memo);
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    protected void doDeleteMemo(Memo memo) {
        memoDao.deleteMemo(memo.getID());

        clearCache(memo);
        tagManager.removeAllTags(memo);
        this.updateContainer(memo);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteMemos(JiveContainer container) {
        deleteMemos(memoDao.getAllMemoIDs(container));
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteMemos(User user) {
        deleteMemos(memoDao.getAllMemoIDs(user));
    }

    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    protected void deleteMemos(List<Long> memoIDs) {
        for(long memoID : memoIDs) {
            Memo memo = this.getMemo(memoID);
            deleteMemo(memo);
        }
    }
        
    public Memo getMemo(long id) {
        try {
            return loadObject(id);
        } catch (NotFoundException e) {
            return null;
        }
    }
    
    public Memo saveMemo(MemoBean bean, JiveIterator<Image> images) {
        Memo memo = memoConverter.convert(bean);

        // Run the "pre" interceptors
        InvocableInterceptorManager manager = ((InvocableInterceptorManager) memo.getJiveContainer()
        .getInterceptorManager());

        systemInterceptorManager.invokeInterceptors(memo, JiveInterceptor.Type.TYPE_PRE);
        manager.invokeInterceptors(memo, JiveInterceptor.Type.TYPE_PRE);

        memo = doSaveMemo(bean, images);
       
        systemInterceptorManager.invokeInterceptors(memo, JiveInterceptor.Type.TYPE_POST);
        manager.invokeInterceptors(memo, JiveInterceptor.Type.TYPE_POST);
       
        this.fireMemoAdded(memo);
        return memo;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    protected Memo doSaveMemo(MemoBean bean, JiveIterator<Image> images) {       
        try {
            Memo memo = loadObject(memoDao.saveMemo(bean), false);
            imageHelper.saveImages(memo, images);
            clearCache(memo);
            this.updateContainer(memo);
            
            return memo;
        } catch(NotFoundException e) {
            throw new IllegalStateException("Error loading memo after creation", e);
        }
    }

    public Memo updateMemo(Memo memo, JiveIterator<Image> images) {
        Memo updated = doUpdateMemo(memo, images);
        this.fireMemoUpdated(updated);
        return updated;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    protected Memo doUpdateMemo(Memo memo, JiveIterator<Image> images) {
        Memo original = getMemo(memo.getID());
    
        MemoBean bean;
        
        try {
            bean = (MemoBean) loadBean(memo.getID(), true).clone();
        } catch(CloneNotSupportedException e) {
            throw new IllegalStateException(String.format("Clone of bean %s failed", memo), e);
        }
        
        bean.setTitle(memo.getTitle());
        bean.setDescription(memo.getDescription());
        
        bean.setModificationDate(new Date());
        bean.setStatusID(memo.getStatus().intValue());
        
        memoDao.updateMemo(bean);
        
        Memo updated;
        try {
            updated = loadObject(memo.getID(), false);
            if (images != null) {
                imageHelper.saveImages(updated, images);
            }
            this.updateContainer(updated);
        } catch(NotFoundException e) {
            throw new RuntimeException("Error loading bean while updating", e);
        }
        
        if (isContentUpdate(original, updated)) {
            try {
                InvocableInterceptorManager manager = ((InvocableInterceptorManager) updated.getJiveContainer()
                        .getInterceptorManager());
                manager.invokeInterceptors(updated, JiveInterceptor.Type.TYPE_EDIT);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        clearCache(updated);
        return updated;
    }

    protected boolean isContentUpdate(Memo before, Memo after) {
     	if (ModerationUtil.isContentUpdate(before, after)) {
            return true;
        }
        
        //Object types with non-standard content (i.e., other than simply subject and
        //body) can extend this method  determine if it has been "updated" or not. 
        return false;
    }

    public Memo updateMemoStatus(Memo memo, Status status) {
        Memo updated = this.doStatusUpdate(memo, status);
        
        clearCache(updated);
        this.fireMemoModerated(updated);
        return updated;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    protected Memo doStatusUpdate(Memo memo, Status status) {
        MemoBean bean;
        
        try {
            bean = (MemoBean) loadBean(memo.getID(), true).clone();
        } catch(CloneNotSupportedException e) {
            throw new IllegalStateException(String.format("Clone of bean %s failed", memo), e);
        }
        
        bean.setStatusID(status.intValue());
        
        memoDao.updateMemo(bean);
        
        Memo updated;
        try {
            updated = loadObject(memo.getID(), false);
            this.updateContainer(updated);
        } catch(NotFoundException e) {
            throw new RuntimeException("Error loading bean while updating", e);
        }
     
        return updated;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void moveMemo(Memo memo, JiveContainer destination) {
        systemInterceptorManager.invokeInterceptors(memo, JiveInterceptor.Type.TYPE_PRE);
        renderCacheManager.clearContentCache(memo);

        JiveContainer previous = memo.getJiveContainer();
        Date moved = new Date();
        memoDao.moveMemo(memo, destination);

        MemoBean bean = this.loadBean(memo.getID(), false);
        bean.setContainerID(destination.getID());
        bean.setContainerType(destination.getObjectType());

        cacheBean(bean);

        systemInterceptorManager.invokeInterceptors(memo, JiveInterceptor.Type.TYPE_EDIT);

        ProxyBypassHelper.setModificationDateForContainer(destination, moved, authenticationProvider.getJiveUser(), memo.getObjectType());
        ProxyBypassHelper.setModificationDateForContainer(previous, moved, authenticationProvider.getJiveUser(), memo.getObjectType());

        communityManager.clearCache(previous);
        communityManager.clearCache(destination);

        queryCacheManager.getQueryCache().remove(memo.getObjectType(), memo.getID());
        queryCacheManager.clearContainerCaches(previous);
        queryCacheManager.queryRemove(memo.getObjectType(), -1);

        this.fireMemoMoved(memo);
    }

    public Memo loadObject(String id) throws NotFoundException {
        return loadObject(Long.parseLong(id));
    }
    
    public Memo loadObject(long id) throws NotFoundException {
        return loadObject(id, true);
    }
    
    public Memo loadObject(long id, boolean useCache) throws NotFoundException {
        return memoConverter.convert(loadBean(id, useCache));
    }
    
    protected MemoBean loadBean(long id, boolean useCache) {
        if(useCache) {
            MemoBean bean = memoCache.get(id);
            if(bean != null) {
                return bean;
            }
        }
        
        return cacheBean(memoDao.getMemo(id));
    }
    
    protected MemoBean cacheBean(MemoBean bean) {
        if(bean == null) {
            return null;
        }
        
        memoCache.put(bean.getID(), bean);
        
        return bean;
    }
    
    protected void clearCache(Memo memo) {
        memoCache.remove(memo.getID());
        queryCacheManager.getQueryCache().remove(new MemoObjectType().getID(), -1);
        queryCacheManager.getQueryCache().remove(new MemoObjectType().getID(), memo.getUser().getID());
        queryCacheManager.getQueryCache().remove(new MemoObjectType().getID(), memo.getJiveContainer().getID());
        queryCacheManager.getQueryCache().remove(JiveConstants.COMMUNITY, communityManager.getRootCommunity().getID());
        renderCacheManager.clearContentCache(memo);
    }
    
    protected void updateContainer(Memo memo) {
        ProxyBypassHelper.setModificationDateForContainer(memo.getJiveContainer(), new Date(), memo.getUser(), memo.getObjectType());
    }
    
    protected boolean areContainersSame(JiveContainer to, JiveContainer from) {
        return to.getObjectType() == from.getObjectType() && to.getID() == from.getID();
    }

    protected void fireMemoAdded(Memo memo) {
         Future<MemoEvent> f = eventDispatcher.fireAndWait(new MemoEvent(MemoEvent.Type.CREATED, new ContainerAwareEntityDescriptor(memo.getObjectType(), memo.getID(), memo.getJiveContainer().getID(), memo.getJiveContainer().getObjectType()), memo.getUser().getID()));
         try {
             f.get();
         } catch (ExecutionException e) {
             log.error("Could not fire added event for video " + memo.getID(), e);
         } catch (InterruptedException e) {
             log.error("Could not fire added event for video " + memo.getID(), e);
         }
    }

    protected void fireMemoUpdated(Memo memo) {
        Future<MemoEvent> f = eventDispatcher.fireAndWait(new MemoEvent(MemoEvent.Type.MODIFIED, new ContainerAwareEntityDescriptor(memo.getObjectType(), memo.getID(), memo.getJiveContainer().getID(), memo.getJiveContainer().getObjectType()), memo.getUser().getID()));
        try {
            f.get();
        } catch (ExecutionException e) {
            log.error("Could not fire updated event for video " + memo.getID(), e);
        } catch (InterruptedException e) {
            log.error("Could not fire updated event for video " + memo.getID(), e);
        }
    }

    protected void fireMemoModerated(Memo memo) {
        Future<MemoEvent> f = eventDispatcher.fireAndWait(new MemoEvent(MemoEvent.Type.MODERATED, new ContainerAwareEntityDescriptor(memo.getObjectType(), memo.getID(), memo.getJiveContainer().getID(), memo.getJiveContainer().getObjectType()), memo.getUser().getID()));
        try {
            f.get();
        } catch (ExecutionException e) {
            log.error("Could not fire moderated event for video " + memo.getID(), e);
        } catch (InterruptedException e) {
            log.error("Could not fire moderated event for video " + memo.getID(), e);
        }
    }

    protected void fireMemoDeleted(Memo memo) {
        Future<MemoEvent> f = eventDispatcher.fireAndWait(new MemoEvent(MemoEvent.Type.DELETED, new ContainerAwareEntityDescriptor(memo.getObjectType(), memo.getID(), memo.getJiveContainer().getID(), memo.getJiveContainer().getObjectType()), memo.getUser().getID()));
        try {
            f.get();
        } catch (ExecutionException e) {
            log.error("Could not fire updated event for video " + memo.getID(), e);
        } catch (InterruptedException e) {
            log.error("Could not fire updated event for video " + memo.getID(), e);
        }
    }
    
    protected void fireMemoMoved(Memo memo) {
        Future<MemoEvent> f = eventDispatcher.fireAndWait(new MemoEvent(MemoEvent.Type.MOVED, new ContainerAwareEntityDescriptor(memo.getObjectType(), memo.getID(), memo.getJiveContainer().getID(), memo.getJiveContainer().getObjectType())));
        try {
            f.get();
        } catch (ExecutionException e) {
            log.error("Could not fire moved event for video " + memo.getID(), e);
        } catch (InterruptedException e) {
            log.error("Could not fire moved event for video " + memo.getID(), e);
        }
    }

    public void setMemoDao(MemoDao memoDao) {
        this.memoDao = memoDao;
    }

    public void setQueryCacheManager(QueryCacheManager queryCacheManager) {
        this.queryCacheManager = queryCacheManager;
    }

    public void setMemoCache(Cache<Long, MemoBean> memoCache) {
        this.memoCache = memoCache;
    }

    public void setImageHelper(MemoImageHelper imageHelper) {
        this.imageHelper = imageHelper;
    }
    
    public void setContainerManager(JiveContainerManager containerManager) {
        this.containerManager = containerManager;
    }

    public void setMemoConverter(MemoConverter memoConverter) {
        this.memoConverter = memoConverter;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }
    
    @Required
    public void setInterceptorManager(InvocableInterceptorManager interceptorManager) {
        this.systemInterceptorManager = interceptorManager;
    }
    
    public void setTagManager(TagManager tagManager) {
        this.tagManager = tagManager;
    }
    
    public void setRenderCacheManager(RenderCacheManager renderCacheManager) {
        this.renderCacheManager = renderCacheManager;
    }
    
    public void setCommunityManager(ExtendedCommunityManager communityManager) {
        this.communityManager = communityManager;
    }
    
    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }
}
