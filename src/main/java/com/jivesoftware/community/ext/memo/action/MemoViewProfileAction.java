package com.jivesoftware.community.ext.memo.action;

import java.util.List;

import com.jivesoftware.community.ApprovalManager;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.ResultFilter;
import com.jivesoftware.community.action.BaseViewProfileContent;
import com.jivesoftware.community.action.util.Paginator;
import com.jivesoftware.community.action.util.SimplePaginator;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.ext.memo.MemoResultFilter;
import com.jivesoftware.community.invitation.Invitation;
import com.jivesoftware.community.invitation.impl.InvitationHelper;
import com.jivesoftware.community.moderation.JiveObjectModerator.Type;

public class MemoViewProfileAction extends BaseViewProfileContent {

    private MemoManager memoManager;
    
    private JiveIterator<Memo> memos;

    public int getMemoType() {
        return new MemoObjectType().getID();
    }
    
    public JiveIterator<Memo> getMemos() {
        if(memos == null) {
            memos = memoManager.getMemos(this.createFilter());
            
            MemoResultFilter filter = this.createFilter();
            filter.setStartIndex(0);
            filter.setNumResults(ResultFilter.NULL_INT);
            totalItemCount = memoManager.getMemoCount(filter);
        }
        
        return memos;
    }
    
    protected MemoResultFilter createFilter() {
        MemoResultFilter filter = MemoResultFilter.createDefaultFilter();
        filter.setStartIndex(start);
        filter.setNumResults(numResults);
        filter.setViewingSelf(this.isViewingSelf());
        filter.setUser(targetUser);
        
        return filter;
    }
    
    public Paginator getNewPaginator() {
        return new SimplePaginator(this, getNumResults(), isMoreResultsAvailable());
    }
    
    public boolean isMoreResultsAvailable() {
        return getTotalItemCount() > getStart() + getNumResults();
    }

    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }
    
}
