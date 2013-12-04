package com.jivesoftware.community.ext.memo.impl;

import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.stats.ViewCountManager;

public class ViewCountProvider {
    private ViewCountManager viewCountManager;

    public void setViewCountManager(ViewCountManager viewCountManager) {
        this.viewCountManager = viewCountManager;
    }
    
    public int getViewCount(Memo memo) {
        return viewCountManager.getViewCount(memo);
    }
}
