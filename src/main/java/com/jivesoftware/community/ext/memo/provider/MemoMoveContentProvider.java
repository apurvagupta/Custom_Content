package com.jivesoftware.community.ext.memo.provider;

import org.apache.log4j.Logger;

import com.jivesoftware.base.UnauthorizedException;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.RejectedException;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.objecttype.MoveContentProvider;

public class MemoMoveContentProvider implements MoveContentProvider<Memo> {

    private static final Logger log = Logger.getLogger(MemoMoveContentProvider.class);
    
    private MemoManager memoManager;
    
    @Override
    public MoveValidationResult canMoveContent(Memo toMove, JiveContainer target) {
        return new MoveValidationResult(true, null);
    }

    @Override
    public String getPageDescriptionKey() {
        return "memo.move.desc";
    }

    @Override
    public String getPageTitleKey() {
        return "memo.move.title";
    }

    @Override
    public void moveContent(Memo toMove, JiveContainer target) throws UnauthorizedException, RejectedException {
        log.debug("Moving memo " + toMove.getID() + " to container " + target);
        memoManager.moveMemo(toMove, target);
    }

    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }
}
