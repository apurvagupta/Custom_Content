package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.RecentHistoryProvider;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.ext.memo.action.ViewMemoAction;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.opensymphony.xwork2.ActionSupport;

public class MemoRecentHistoryProvider implements RecentHistoryProvider {

    private BeanProvider<MemoObjectType> memoObjectTypeProvider;
    
    public JiveObject getJiveObjectFromViewAction(String objectTypeCode, ActionSupport viewAction) {
        if(memoObjectTypeProvider.get().getCode().equals(objectTypeCode)) {
            return ((ViewMemoAction) viewAction).getMemo();
        }
        
        return null;
    }

    public void setMemoObjectTypeProvider(BeanProvider<MemoObjectType> memoObjectTypeProvider) {
        this.memoObjectTypeProvider = memoObjectTypeProvider;
    }

}
