package com.jivesoftware.community.ext.memo.action;

import com.jivesoftware.base.event.v2.EventDispatcher;
import com.jivesoftware.community.ContainerAwareEntityDescriptor;
import com.jivesoftware.community.ContentTag;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.WatchManager;
import com.jivesoftware.community.ext.memo.MemoEvent;
import com.jivesoftware.community.renderer.impl.v2.TinyMCESupport;

public class ViewMemoAction extends MemoActionSupport {
    private JiveIterator<ContentTag> tags;
    private WatchManager watchManager;
    private EventDispatcher eventDispatcher;

    @Override
    public String execute() {
        if(memo != null) {
 
            tags = tagManager.getTags(memo);
            if(eventDispatcher != null) {
                eventDispatcher.fire(new MemoEvent(MemoEvent.Type.VIEWED, new ContainerAwareEntityDescriptor(memo.getObjectType(), memo.getID(), memo.getJiveContainer().getID(), memo.getJiveContainer().getObjectType())));
            }
            
            return SUCCESS;
        } 
        
        return ERROR;
    }

    public JiveContainer getContainer() {
        if(memo == null) {
            return null;
        }
        
        try {
            return jiveObjectLoader.getJiveContainer(memo.getContainerType(), memo.getContainerID());
        } catch (NotFoundException e) {
            return super.getContainer();
        }
    }

    public boolean isWatched() {
        boolean watched = false;
        if (getUser() != null) {
            watched = watchManager.isWatched(getUser(), memo);
        }
        return watched;
    }
    public String getMacroJavaScript() {
        return TinyMCESupport.getMacroJavaScript(getGlobalRenderManager());
    }

    public JiveIterator<ContentTag> getTags() {
        return tags;
    }
    public void setWatchManager(WatchManager watchManager) {
        this.watchManager = watchManager;
    }
    public void setJiveEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }
    
}

