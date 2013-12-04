package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.objecttype.ContentNotificationProvider;

public class MemoContentNotificationProvider implements ContentNotificationProvider<Memo> {

    public String getAttachmentName(Memo memo) {
        return "";
    }

    public long getAttachmentSize(Memo memo) {
        return 0;
    }

    public String getObjectTitle(Memo memo) {
        return memo.getSubject();
    }

    public boolean isAttachmentAvailable() {
        return false;
    }
}
