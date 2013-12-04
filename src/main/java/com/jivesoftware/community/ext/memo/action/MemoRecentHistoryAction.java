package com.jivesoftware.community.ext.memo.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.jivesoftware.base.UnauthorizedException;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.EntityDescriptor;
import com.jivesoftware.community.RecentHistoryManager;
import com.jivesoftware.community.RecentHistoryManager.HistoryArchetype;
import com.jivesoftware.community.action.JiveActionSupport;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;

public class MemoRecentHistoryAction extends JiveActionSupport {
    private static final Logger log = Logger.getLogger(MemoRecentHistoryAction.class);
    
    private RecentHistoryManager recentHistoryManager;
    private BeanProvider<MemoObjectType> memoObjectTypeProvider;
    private MemoManager memoManager;
    
    List<Memo> memoHistory;

    public String execute() {
        Map<HistoryArchetype, List<EntityDescriptor>> history = recentHistoryManager.getRecentHistory(getRequest());
        
        if(history == null) {
            return SUCCESS;
        }
        
        List<Long> list = getHistoryForType(history);
        if(!list.isEmpty()) {
            memoHistory = new ArrayList<Memo>(list.size());
            synchronized (list) {
                for(long id : list) {
                    try {
                        Memo memo = memoManager.getMemo(id);
                        
                        if(memo == null) {
                            log.debug("Could not load memo with id: " + id);
                        } else {
                            memoHistory.add(memo);
                        }
                    } catch( UnauthorizedException e) {
                        log.debug("Not authorized to load memo with id: " + id, e);
                    }
                    
                }
            }
        }
        
        return SUCCESS;
    }
    
    protected List<Long> getHistoryForType(Map<HistoryArchetype, List<EntityDescriptor>> history) {
        List<Long> historyForType = new ArrayList<Long>();
        int objectTypeId = memoObjectTypeProvider.get().getID();
        
        HistoryArchetype archetype = getRecentHistoryArchetype(objectTypeId);
        List<EntityDescriptor> list = history.get(archetype);

        if(list != null) {
            for(EntityDescriptor entityDescriptor : list) {
                if(entityDescriptor.getObjectType() == objectTypeId) {
                    historyForType.add(entityDescriptor.getID());
                }
            }
        }
        
        return historyForType;
    }
    
    protected HistoryArchetype getRecentHistoryArchetype(int objectTypeId) {
    	return RecentHistoryManager.getArchetype(objectTypeId);
    }
    
    public List<Memo> getMemoHistory() {
        if(memoHistory != null) {
            return memoHistory;
        } else {
            return Lists.newArrayList();
        }
    }
    
    public String getView() {
        return "memo";
    }

    public void setRecentHistoryManager(RecentHistoryManager recentHistoryManager) {
        this.recentHistoryManager = recentHistoryManager;
    }
    
    public void setMemoObjectTypeProvider(BeanProvider<MemoObjectType> memoObjectTypeProvider) {
        this.memoObjectTypeProvider = memoObjectTypeProvider;
    }

    public MemoManager getMemoManager() {
        return memoManager;
    }

    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }
}
