package com.jivesoftware.community.ext.memo.provider;

import java.util.List;

import com.jivesoftware.base.User;
import com.jivesoftware.community.EntityDescriptor;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.RecentContentEventHandlingStrategy;
import com.jivesoftware.community.RecentContentInfoProvider;
import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.dao.MemoDao;
import com.jivesoftware.community.impl.QueryContainer;
import com.jivesoftware.community.web.JiveResourceResolver;
import com.jivesoftware.util.DateUtils;

public class MemoRecentContentInfoProvider implements RecentContentInfoProvider {

    private MemoDao memoDao;
    
    public boolean eventsHandledExclusivelyByCustomListener() {
        return false;
    }

    public User getAuthor(JiveObject object) {
        return ((Memo) object).getUser();
    }

    public QueryContainer getContentContainerInfoQuery(List<Long> objectIDs, EntityDescriptor container) {
        QueryContainer sql = new QueryContainer();
        
        sql.appendText("SELECT memoID, containerID, containerType FROM jiveMemo WHERE memoID in (");
        
        String sep = "";
        for(long objectID : objectIDs) {
            sql.appendText(sep);
            sql.appendText(String.valueOf(objectID));
            sep = ",";
        }
        
        sql.appendText(") AND status = ").appendText(String.valueOf(Status.PUBLISHED.intValue()));
        
        return sql;
    }

    public String getDate(JiveObject object) {
        return new DateUtils().displayFriendly(((Memo) object).getModificationDate());
    }

    public User getEditingUser(JiveObject object) {
        return null;
    }

    public RecentContentEventHandlingStrategy getEventHandlingStrategy() {
        return null;
    }
    
    public String getObjectUrl(JiveObject object, boolean constructAbsoluteURL) {
        return JiveResourceResolver.getJiveObjectURL(object, constructAbsoluteURL);
    }

    public JiveContainer getParent(JiveObject object) {
        return ((Memo) object).getJiveContainer();
    }

    public List<EntityDescriptor> getRecentContentContainersForUser(User user) {
        return memoDao.getFrequentMemoContainers(user.getID());
    }

    public String getSubject(JiveObject object) {
        return ((Memo) object).getSubject();
    }

    public void setMemoDao(MemoDao memoDao) {
        this.memoDao = memoDao;
    }

}
