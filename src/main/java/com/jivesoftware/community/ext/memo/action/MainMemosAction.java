package com.jivesoftware.community.ext.memo.action;

import com.jivesoftware.community.Community;
import com.jivesoftware.community.action.CommunityActionSupport;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.opensymphony.xwork2.Preparable;

public class MainMemosAction extends CommunityActionSupport implements Preparable {
    private MemoManager memoManager;
    private BeanProvider<MemoObjectType> memoObjectTypeProvider;

    private int memoCount = 0;

    public void prepare() throws Exception {
        community = communityManager.getRootCommunity();
    }

    public void setCommunity(Community community) {
        //do nothing.  always use the root community
    }

    public String execute() {
        memoCount = memoManager.getMemoCount(community);
        return SUCCESS;
    }

    public int getMemoCount() {
        return memoCount;
    }
    
    public int getMemoType() {
        return memoObjectTypeProvider.get().getID();
    }

    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }

    public void setMemoObjectTypeProvider(BeanProvider<MemoObjectType> memoObjectTypeProvider) {
        this.memoObjectTypeProvider = memoObjectTypeProvider;
    }

}

