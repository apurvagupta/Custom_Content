package com.jivesoftware.community.ext.memo.action;

import static com.jivesoftware.community.action.util.JiveDWRUtils.*;

import org.apache.log4j.Logger;

import com.jivesoftware.base.UnauthorizedException;
import com.jivesoftware.base.User;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.Watch;
import com.jivesoftware.community.WatchManager;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;

public class MemoWatchesAction {
    private static final Logger log = Logger.getLogger(MemoWatchesAction.class);
    
    private MemoManager memoManager;
    private WatchManager watchManager;

    public void watchMemo(long memoID) throws NotFoundException {
    try {
        User user = getUser();
        if (user != null) {
        Memo memo = memoManager.getMemo(memoID);

        if (memo == null) {
            log.error("Could not find memo with ID " + memoID);
            throw new NotFoundException();
        }

        watchManager.createWatch(user, memo);
        }
    } catch (UnauthorizedException e) {
        log.error(e);
        throw e;
    }
    }

    public void removeMemoWatch(long memoID) throws NotFoundException {
    try {
        User user = getUser();
        if (user != null) {
        Memo memo = memoManager.getMemo(memoID);
        if (memo == null) {
            log.error("Could not find memo with ID " + memoID);
            throw new NotFoundException();
        }

        Watch w = watchManager.getWatch(user, memo);
        if (w != null) {
            watchManager.deleteWatch(w);
        }
        }
    } catch (UnauthorizedException e) {
        log.error(e);
        throw e;
    }
    }

    public void setMemoManager(MemoManager memoManager) {
    this.memoManager = memoManager;
    }

    public void setWatchManager(WatchManager watchManager) {
    this.watchManager = watchManager;
    }
}
