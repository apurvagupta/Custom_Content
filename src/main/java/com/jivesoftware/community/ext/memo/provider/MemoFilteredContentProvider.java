package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.community.JiveConstants;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.ResultFilter;
import com.jivesoftware.community.TagSet;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.impl.QueryContainer;
import com.jivesoftware.community.internal.ExtendedCommunityManager;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.jivesoftware.community.objecttype.AbstractFilteredContentProvider;

public class MemoFilteredContentProvider extends AbstractFilteredContentProvider {

    private BeanProvider<MemoObjectType> memoObjectTypeProvider;
    private ExtendedCommunityManager communityManager;
    
    @Override
    protected String getIdentifierColumn() {
        return "memoID";
    }

    @Override
    protected int getObjectTypeId() {
        return memoObjectTypeProvider.get().getID();
    }

    @Override
    protected String getTableName() {
        return "jiveMemo";
    }

    @Override
    protected boolean shortCircuitWhenNotRoot() {
        return false;
    }

    public QueryContainer getFilteredContentQuery(ResultFilter filter, JiveContainer container, boolean isRoot) {
        boolean filterUser = filter.getUserID() != ResultFilter.NULL_INT;
        boolean filterCreationDate = filter.getCreationDateRangeMin() != null || filter.getCreationDateRangeMax() != null;
        boolean filterModifiedDate = filter.getModificationDateRangeMin() != null || filter.getModificationDateRangeMax() != null;
        
        QueryContainer query = new QueryContainer();
        
        query.appendText("SELECT DISTINCT jiveMemo.memoID as objectID, ");
        query.appendText(String.valueOf(getObjectTypeId()));
        query.appendText(" as objectType, jiveMemo.modificationDate as modDate,");
        query.appendText(" jiveMemo.creationDate as createDate");
        query.appendText(" FROM jiveMemo");
        
        if (!isRoot && filter.isRecursive() && container.getObjectType() == JiveConstants.COMMUNITY) {
            query.appendText(", jiveCommunity");
        }
        
        if(filter.getTagSets().size() > 0) {
            query.appendText(", jiveObjectTagSet ots");
        }
        query.appendText(" WHERE 1=1");
        if (!isRoot) {
            if (filter.isRecursive() && container.getObjectType() == JiveConstants.COMMUNITY) {
                query.appendText(" AND jiveMemo.containerType = ?");
                query.addArgumentValue(JiveConstants.COMMUNITY);
                query.appendText(" AND jiveMemo.containerID = jiveCommunity.communityID");
                int[] lftRgtValues = communityManager.getLftRgtValues(container.getID());
                query.appendText(" AND jiveCommunity.lft >= ?");
                query.addArgumentValue(lftRgtValues[0]);
                query.appendText(" AND jiveCommunity.rgt <= ?");
                query.addArgumentValue(lftRgtValues[1]);
            } else {
                query.appendText(" AND jiveMemo.containerType = ?");
                query.addArgumentValue(container.getObjectType());
                query.appendText(" AND jiveMemo.containerID = ?");
                query.addArgumentValue(container.getID());
            }
        }
        
        if (filterUser) {
            query.appendText(" AND jiveMemo.userID = ?");
            query.addArgumentValue(filter.getUserID());
        }
        
        if (filterCreationDate) {
            if (filter.getCreationDateRangeMin() != null) {
                query.appendText(" AND jiveMemo.creationDate >= ?");
                query.addArgumentValue(filter.getCreationDateRangeMin().getTime());
            }
            
            if (filter.getCreationDateRangeMax() != null) {
                query.appendText(" AND jiveMemo.creationDate <= ?");
                query.addArgumentValue(filter.getCreationDateRangeMax().getTime());
            }
        }

        if (filterModifiedDate) {
            if (filter.getModificationDateRangeMin() != null) {
                query.appendText(" AND jiveMemo.modificationDate >= ?");
                query.addArgumentValue(filter.getModificationDateRangeMin().getTime());
            }
            
            if (filter.getModificationDateRangeMax() != null) {
                query.appendText(" AND jiveMemo.modificationDate <= ?");
                query.addArgumentValue(filter.getModificationDateRangeMax().getTime());
            }
        }
        
        query.appendText(" AND jiveMemo.status in (?,?)");
        query.addArgumentValue(JiveContentObject.Status.PUBLISHED.intValue());
        query.addArgumentValue(JiveContentObject.Status.ABUSE_VISIBLE.intValue());
        
        if(filter.getTagSets().size() > 0) {
            query.appendText(" AND eventID = ots.objectID AND ots.objectType = " + MemoObjectType.MEMO_TYPE_ID + " AND ");
            if(filter.getTagSets().size() == 1) {
                query.appendText("ots.tagSetID = ?");
            } else {
                query.appendText("ots.tagSetID in (");
                String sep = "";
                for(int i = 0; i < filter.getTagSets().size(); i++) {
                    query.appendText(sep);
                    query.appendText("?");
                    sep = ",";
                }
                query.appendText(")");
            }

            for(TagSet tagSet : filter.getTagSets()) {
                query.addArgumentValue(tagSet.getID());
            }
        }
        return query;
    }
    
    public void setMemoObjectTypeProvider(BeanProvider<MemoObjectType> memoObjectTypeProvider) {
        this.memoObjectTypeProvider = memoObjectTypeProvider;
    }

    public void setCommunityManager(ExtendedCommunityManager communityManager) {
        this.communityManager = communityManager;
    }
    
}
