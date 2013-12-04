package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.base.User;
import com.jivesoftware.base.aaa.AuthenticationProvider;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.impl.AbstractUserBarProvider;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;

public class MemoUserBarProvider extends AbstractUserBarProvider {

    private AuthenticationProvider authProvider;
    private BeanProvider<MemoObjectType> memoObjectTypeProvider;
    
    @Override
    public int getObjectTypeId() {
        return memoObjectTypeProvider.get().getID();
    }

    public String getUserBarBrowseURL() {
        return "memos";
    }

    public String getUserBarYourStuffURL() {
        User user = authProvider.getJiveUser();
        
        return "people/" + user.getUsername() + "?view=memo";
    }

    public boolean isVisibleOnUserBarBrowseDropDown(JiveContainer currentContainer) {
        return true;
    }

    public boolean isVisibleOnUserBarHistoryDropDown(JiveContainer currentContainer) {
        return true;
    }

    public boolean isVisibleOnUserBarNewDropDown(JiveContainer currentContainer) {
        return true;
    }

    public boolean isVisibleOnUserBarYourStuffDropDown(JiveContainer currentContainer) {
        return true;
    }

    public void setAuthProvider(AuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    public void setMemoObjectTypeProvider(BeanProvider<MemoObjectType> memoObjectTypeProvider) {
        this.memoObjectTypeProvider = memoObjectTypeProvider;
    }


}
