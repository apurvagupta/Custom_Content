package com.jivesoftware.community.ext.memo.provider;

import org.apache.log4j.Logger;

import com.jivesoftware.base.User;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoResultFilter;
import com.jivesoftware.community.impl.QueryCache;
import com.jivesoftware.community.objecttype.ContainableTypeManager;

public class MemoContainableTypeManager implements ContainableTypeManager {
    private static final Logger log = Logger.getLogger(MemoContainableTypeManager.class);
    private MemoManager memoManager;
    
    public void deleteAllContent(JiveContainer container) {
        memoManager.deleteMemos(container);
    }

    public void deleteAllContent(User user) {
        memoManager.deleteMemos(user);
    }

    public int getContentCount(User user) {
        MemoResultFilter filter = new MemoResultFilter();
        filter.setUser(user);
        return memoManager.getMemoCount(filter);
    }

    public int getContentCount(JiveContainer container, boolean recursive) {
        MemoResultFilter filter = new MemoResultFilter();
        filter.setContainer(container);
        filter.setRecursive(recursive);
        return memoManager.getMemoCount(filter);
    }

    @Override
    public void migrateAllContent(JiveContainer srcContainer, JiveContainer destContainer) {
        MemoResultFilter filter = MemoResultFilter.createDefaultFilter();
        filter.setContainer(srcContainer);

        int count = memoManager.getMemoCount(filter);

        int blockSize = QueryCache.BLOCK_SIZE;
        int blocks = (int) Math.ceil(count / blockSize);

        filter.setNumResults(blockSize);

        log.debug("Migrating memos ...");
        for (int i = 0; i <= blocks; i++) {
            for (Memo memo : memoManager.getMemos(filter)) {
                log.debug("Moving brick oven id " + memo.getID() + ".");
                memoManager.moveMemo(memo, destContainer);
            }
        }
    }

    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }

    
}
