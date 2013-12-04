package com.jivesoftware.community.ext.memo.proxy;

import java.util.Set;

import com.jivesoftware.base.AuthToken;
import com.jivesoftware.base.UnauthorizedException;
import com.jivesoftware.base.User;
import com.jivesoftware.base.aaa.AuthenticationProvider;
import com.jivesoftware.base.proxy.ProxyUtils;
import com.jivesoftware.base.util.UserPermHelper;
import com.jivesoftware.community.Image;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContainerManager;
import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.ResultFilter;
import com.jivesoftware.community.aaa.authz.EntitlementTypeProvider;
import com.jivesoftware.community.aaa.authz.EntitlementTypeProvider.EntitlementType;
import com.jivesoftware.community.RejectedException;
import com.jivesoftware.community.util.JiveContainerPermHelper;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoBean;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoResultFilter;
import com.jivesoftware.community.impl.ObjectFactory;
import com.jivesoftware.community.objecttype.EntitlementCheckProvider;
import com.jivesoftware.community.proxy.IteratorProxy;

public class MemoManagerProxy implements MemoManager, ObjectFactory<Memo>, IteratorProxy.ProxyFactory<Memo> {
    private MemoManager delegate;
    private AuthenticationProvider authProvider;
    private JiveContainerManager containerManager;
    private EntitlementCheckProvider<Memo> entitlementCheckProvider;
    private EntitlementTypeProvider entitlementTypeProvider;

    public void deleteMemo(Memo memo) {
        if (entitlementCheckProvider.isUserEntitled(memo, EntitlementType.DELETE)) {
            delegate.deleteMemo(memo);
        } else {
            throw new UnauthorizedException();
        }
    }
    
    public void deleteMemos(JiveContainer container) {
        if (entitlementCheckProvider.isUserEntitled(container, EntitlementType.DELETE)) {
            delegate.deleteMemos(container);
        } else {
            throw new UnauthorizedException();
        }
    }
    
    public void deleteMemos(User user) {
        if (UserPermHelper.isUserAdmin(getEffectiveUser())) {
            delegate.deleteMemos(user);
        } else { 
            throw new UnauthorizedException();
        }
    }
    
    public void moveMemo(Memo memo, JiveContainer destination) {
        if(entitlementCheckProvider.isUserEntitled(memo, EntitlementType.DELETE) && entitlementCheckProvider.isUserEntitled(destination, EntitlementType.CREATE)) {
            delegate.moveMemo(memo, destination);
        } else {
            throw new UnauthorizedException();
        }
    }

    public Memo getMemo(long id) {
        Memo memo = delegate.getMemo(id);
        
        if(memo != null) {
            
            if (entitlementCheckProvider.isUserEntitled(memo, EntitlementType.VIEW)) {
                memo = createProxy(memo, authProvider.getAuthToken());
            } else {
                throw new UnauthorizedException();
            }
        }

        return memo;
    }

    public Memo createProxy(Memo memo, AuthToken authToken) {
        if (entitlementCheckProvider.isUserEntitled(memo, EntitlementType.VIEW)) {
            return ProxyUtils.proxyObject(MemoProxy.class, memo, authToken);
        } 
        
        return null;
    }

    public int getMemoCount(JiveContainer container) {
        if (entitlementCheckProvider.isUserEntitled(container, EntitlementType.VIEW)) {
            MemoResultFilter resultFilter = MemoResultFilter.createDefaultFilter();
            resultFilter.setContainer(container);
            return getMemoCount(resultFilter);
        } else {
            return 0;
        }
    }

    public int getMemoCount(MemoResultFilter resultFilter) {
        return getMemoCount(this.getMemos(resultFilter));
    }

    protected int getMemoCount(JiveIterator<Memo> memos) {
        int count = 0;
        while (memos.hasNext()) {
            memos.next();
            count++;
        }

        return count;
    }

    public JiveIterator<Memo> getMemos(MemoResultFilter resultFilter) {
        int start = resultFilter.getStartIndex(); 
        int numResults = resultFilter.getNumResults(); 
        resultFilter.setStartIndex(0); 
        resultFilter.setNumResults(ResultFilter.NULL_INT); 
        
        // query core api 
        JiveIterator<Memo> memos = delegate.getMemos(resultFilter); 
        
        // reset filter 
        resultFilter.setStartIndex(start); 
        resultFilter.setNumResults(numResults); 
        
        // set start/numResults to be valid entries for the iteration 
        start = (start == ResultFilter.NULL_INT || start < 0) ? 0 : start; 
        numResults = (numResults == ResultFilter.NULL_INT) ? -1 : numResults; 
        
        // proxy results 
        return new IteratorProxy<Memo>(memos, authProvider.getAuthToken(), start, numResults);
    }

    public JiveIterator<Memo> getAllMemos() {
        return createIteratorProxy(delegate.getAllMemos());
    }
    
    protected JiveIterator<Memo> createIteratorProxy(JiveIterator<Memo> memos) {
        return new IteratorProxy<Memo>(memos, authProvider.getAuthToken());
    }

    protected boolean isVisible(long memoId) {
        try {
            this.getMemo(memoId);
            return true;
        } catch (UnauthorizedException e) {
            return false;
        }
    }

    public Memo saveMemo(MemoBean bean, JiveIterator<Image> images) {
        try {
            JiveContainer container = containerManager.getJiveContainer(bean.getContainerType(), bean.getContainerID());
            if (entitlementCheckProvider.isUserEntitled(container, EntitlementType.CREATE)) {
                return delegate.saveMemo(bean, images);
            } else {
                throw new UnauthorizedException();
            }
        } catch (NotFoundException e) {
            throw new UnauthorizedException();
        }
    }

    public Memo updateMemo(Memo memo, JiveIterator<Image> images) {
        if (entitlementCheckProvider.isUserEntitled(memo, EntitlementType.EDIT)) {
            return delegate.updateMemo(memo, images);
        } else {
            throw new UnauthorizedException();
        }
    }
    
    public Memo updateMemoStatus(Memo memo, Status status) {
        if (entitlementCheckProvider.isUserEntitled(memo, EntitlementType.EDIT)) {
            return delegate.updateMemoStatus(memo, status);
        } else {
            throw new UnauthorizedException();
        }
    }
    
    public Memo loadObject(long id) throws NotFoundException {
        return this.getMemo(id);
    }

    public Memo loadObject(String id) throws NotFoundException {
        Memo memo = (Memo)((ObjectFactory)delegate).loadObject(id);
        if (entitlementCheckProvider.isUserEntitled(memo, EntitlementType.VIEW)) {
            return memo;
        } else {
            return null;
        }
    }
    
    protected User getEffectiveUser() {
        return authProvider.getAuthentication().getUser();
    }

    public void setDelegate(MemoManager delegate) {
        this.delegate = delegate;
    }

    public void setAuthProvider(AuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    public void setContainerManager(JiveContainerManager containerManager) {
        this.containerManager = containerManager;
    }
    
    public void setEntitlementTypeProvider(EntitlementTypeProvider entitlementTypeProvider) {
        this.entitlementTypeProvider = entitlementTypeProvider;
    }

    public void setEntitlementCheckProvider(EntitlementCheckProvider<Memo> entitlementCheckProvider) {
        this.entitlementCheckProvider = entitlementCheckProvider;
    }
}
