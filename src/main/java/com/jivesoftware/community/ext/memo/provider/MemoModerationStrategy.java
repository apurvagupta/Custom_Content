package com.jivesoftware.community.ext.memo.provider;

import static com.jivesoftware.community.JiveContentObject.Status.*;

import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.moderation.DefaultModerationStrategy;

public class MemoModerationStrategy extends DefaultModerationStrategy {

    private MemoManager memoManager;
    
    @Override
    public void stateChangeCallback(JiveObject jiveObject, Status status) {
        Memo memo = memoManager.getMemo(jiveObject.getID());
        
        if((memo.getStatus() == ABUSE_HIDDEN || memo.getStatus() == ABUSE_VISIBLE) && status == AWAITING_MODERATION) {
            return;
        }

        memoManager.updateMemoStatus(memo, status);
    }

    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }

}
