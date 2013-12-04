package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.base.event.v2.BaseJiveEvent;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.tags.type.impl.ContentGenericTaggableTypeInfoProvider;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoEvent;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.ext.memo.proxy.MemoPermHelper;

public class MemoGenericTaggableTypeInfoProvider extends ContentGenericTaggableTypeInfoProvider {

    private static final String SELECT_SQL =
            "SELECT containerType, containerID, tagID FROM "
                    + "(SELECT containerType, containerID, tagID FROM jiveObjectTag, jiveMemo"
                    + " WHERE jiveObjectTag.objectType = " + MemoObjectType.MEMO_TYPE_ID
                    + " AND jiveObjectTag.objectID = jiveMemo.memoID"
                    + " AND (jiveMemo.status = " + Memo.Status.PUBLISHED.intValue()
                    + " OR jiveMemo.status = " + Memo.Status.ABUSE_VISIBLE.intValue() + ")"
                    + " UNION ALL"
                    + " SELECT containerType, containerID, tagID FROM jiveCommunityTag, jiveMemo"
                    + " WHERE jiveCommunityTag.objectType = " + MemoObjectType.MEMO_TYPE_ID
                    + " AND jiveCommunityTag.objectID = jiveMemo.memoID"
                    + " AND (jiveMemo.status = " + Memo.Status.PUBLISHED.intValue()
                    + " OR jiveMemo.status = " + Memo.Status.ABUSE_VISIBLE.intValue() + ")"
                    + ") temp"
                    + " ORDER BY containerType, containerID";

    public EventType getMappedEventType(BaseJiveEvent e) {
        if (!(e instanceof MemoEvent)) {
            return null;
        }
        MemoEvent memo = (MemoEvent) e;
        switch (memo.getType()) {
            case DELETED:
                return EventType.deleted;
        }
        return null;
    }

    public boolean isTaggable(JiveContainer container) {
        return MemoPermHelper.areMemosEnabled(container);
    }

    public boolean isVisible(JiveObject jiveObject) {
        if (jiveObject instanceof Memo) {
        	Memo memo = (Memo) jiveObject;
            return memo.getStatus() == Memo.Status.PUBLISHED || memo.getStatus() == Memo.Status.ABUSE_VISIBLE;
        }

        return false;
    }

    public String getTagCloudSummaryQuery() {
        return SELECT_SQL;
    }

}
