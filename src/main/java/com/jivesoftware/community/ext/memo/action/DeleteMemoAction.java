package com.jivesoftware.community.ext.memo.action;

import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.web.JiveResourceResolver;


public class DeleteMemoAction extends MemoActionSupport {
    private MemoManager memoManager;

    @Override
    public String execute() {
        memoManager.deleteMemo(memo);

        return SUCCESS;
    }

    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }

    public String getRedirectURL() {
        return JiveResourceResolver.getJiveObjectURL(memo.getJiveContainer(), false);
    }
}
