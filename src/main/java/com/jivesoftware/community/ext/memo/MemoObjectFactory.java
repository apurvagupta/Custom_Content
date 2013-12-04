package com.jivesoftware.community.ext.memo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jivesoftware.base.AuthToken;
import com.jivesoftware.base.UnauthorizedException;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.impl.BlockObjectFactory;
import com.jivesoftware.community.impl.ObjectFactory;
import com.jivesoftware.community.objecttype.JiveObjectFactory;
import com.jivesoftware.community.proxy.IteratorProxy;

public class MemoObjectFactory implements JiveObjectFactory<Memo>, BlockObjectFactory<Memo> {
    private static final Logger log = Logger.getLogger(MemoObjectFactory.class);
    
    private ObjectFactory<Memo> objectFactory;
    private ObjectFactory<Memo> proxiedObjectFactory;
    private IteratorProxy.ProxyFactory<Memo> proxyFactory;
    
    
    public Memo createProxy(Memo memo, AuthToken authToken) {
        return proxyFactory.createProxy(memo, authToken);
    }

    public Memo loadObject(long id) throws NotFoundException {
        return objectFactory.loadObject(id);
    }

    public Memo loadObject(String id) throws NotFoundException {
        return objectFactory.loadObject(id);
    }

    public Memo loadProxyObject(long id) throws NotFoundException, UnauthorizedException {
        return proxiedObjectFactory.loadObject(id);
    }

    public Memo loadProxyObject(String id) throws NotFoundException, UnauthorizedException {
        return proxiedObjectFactory.loadObject(id);
    }

    public List<Memo> loadObjects(List<Long> ids) {
        if(objectFactory instanceof BlockObjectFactory) {
            return ((BlockObjectFactory) objectFactory).loadObjects(ids);
        }
        
        List<Memo> memos = new ArrayList<Memo>(ids.size());
        for(long id : ids) {
            try {
                memos.add(loadObject(id));
            } catch (NotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }
        
        return memos;
    }

    public void setObjectFactory(ObjectFactory<Memo> objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setProxiedObjectFactory(ObjectFactory<Memo> proxiedObjectFactory) {
        this.proxiedObjectFactory = proxiedObjectFactory;
    }

    public void setProxyFactory(IteratorProxy.ProxyFactory<Memo> proxyFactory) {
        this.proxyFactory = proxyFactory;
    }


}
