package com.jivesoftware.community.ext.memo.provider;

import com.google.common.collect.Maps;
import com.jivesoftware.base.event.v2.EventDispatcher;
import com.jivesoftware.community.ContainerAwareEntityDescriptor;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.cache.Cache;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoEvent;
import com.jivesoftware.community.ext.memo.action.MemoActionSupport;
import com.jivesoftware.community.objecttype.ViewCountSupport;
import com.opensymphony.xwork2.ActionInvocation;

public class MemoViewCountSupport implements ViewCountSupport {

    private Cache<Long, Integer> countCache;
    private EventDispatcher eventDispatcher;

    public Cache<Long, Integer> getCountCache() {
        return countCache;
    }

    public String getCacheLockKey() {
        return "memoViewCount";
    }

    public Memo getObjectFromActionInvocation(ActionInvocation actionInvocation) {
        return ((MemoActionSupport) actionInvocation.getAction()).getMemo();
    }

    public void setCountCache(Cache<Long, Integer> memoCountCache) {
        this.countCache = memoCountCache;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public void fireEventsAfterView(JiveObject jiveObject) {
        Memo memo = (Memo) jiveObject;
        eventDispatcher.fire(new MemoEvent(MemoEvent.Type.VIEWED, new ContainerAwareEntityDescriptor(memo), Maps.<String, Object>newHashMap()));
    }

}
