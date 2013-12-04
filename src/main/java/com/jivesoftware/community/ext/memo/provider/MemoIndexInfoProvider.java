package com.jivesoftware.community.ext.memo.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jivesoftware.community.EntityDescriptor;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.jivesoftware.community.search.BaseIndexInfoProvider;
import com.jivesoftware.community.search.IndexField;

public class MemoIndexInfoProvider extends BaseIndexInfoProvider {

    private static final String COUNT_SQL = "SELECT count(memoID) FROM jiveMemo WHERE "
        + "status = " + JiveContentObject.Status.PUBLISHED.intValue() + " AND "
        + "modificationDate <= ?";
    
    private static final String ID_SQL = "SELECT memoID FROM jiveMemo WHERE "
        + "status = " + JiveContentObject.Status.PUBLISHED.intValue() + " AND "
        + "memoID >= ? AND memoID <= ? AND modificationDate >= ? AND modificationDate <= ?";
    
    private static final String MAX_ID_SQL = "SELECT MAX(memoID) from jiveMemo WHERE "
        + "status = " + JiveContentObject.Status.PUBLISHED.intValue() + " AND "
        + "modificationDate <= ?";
    
    private static final String MIN_ID_SQL = "SELECT MIN(memoID) from jiveMemo WHERE "
        + "status = " + JiveContentObject.Status.PUBLISHED.intValue() + " AND "
        + "modificationDate <= ?";
    
    private BeanProvider<MemoObjectType> memoObjectTypeProvider;
    private MemoManager memoManager;

    @Override
    protected String getCountSQL() {
        return COUNT_SQL;
    }

    @Override
    protected String getIDsSQL() {
        return ID_SQL;
    }

    @Override
    protected Map<IndexField, String> getIndexFields(long id) {
        Memo memo = memoManager.getMemo(id);
        final Map<IndexField, String> map = new HashMap<IndexField, String>();
        
        if(memo != null) {
            String tags = tagManager.getTagsAsString(memo);
            JiveContainer container = memo.getJiveContainer();
            List<EntityDescriptor> ids = util.getParentContainerIDs(container);
            
            map.put(IndexField.subject, memo.getSubject());
            map.put(IndexField.tags, tags);
            map.put(IndexField.objectID, String.valueOf(memo.getID()));
            map.put(IndexField.objectType, String.valueOf(memo.getObjectType()));
            map.put(IndexField.containerID, String.valueOf(container.getID()));
            map.put(IndexField.containerType, String.valueOf(container.getObjectType()));
            map.put(IndexField.creationDate, String.valueOf(memo.getCreationDate().getTime()));
            map.put(IndexField.modificationDate, String.valueOf(memo.getModificationDate().getTime()));
            map.put(IndexField.containerIDs, util.buildIDString(ids.iterator()));
            map.put(IndexField.userID, String.valueOf(memo.getUser().getID()));
            map.put(IndexField.author, util.buildIDString(memo.getAuthors()));
        }
        
        return map;
    }

    @Override
    protected String getLanguage(long id) {
        return null;
    }

    @Override
    protected String getMaxIDSQL() {
        return MAX_ID_SQL;
    }

    @Override
    protected String getMinIDSQL() {
        return MIN_ID_SQL;
    }

    @Override
    protected int getObjectTypeID() {
        return memoObjectTypeProvider.get().getID();
    }

    public boolean isIndexable(JiveObject jiveObject) {
        Memo memo = null;
        if(jiveObject instanceof EntityDescriptor) {
            EntityDescriptor ed = (EntityDescriptor) jiveObject;
            if(ed.getObjectType() == getObjectTypeID()) {
                memo = memoManager.getMemo(ed.getID());
            }
        } else {
            memo = (Memo)jiveObject;
        }
        
        return memo == null ? false : memo.getStatus().isVisible();
    }

    public void setMemoObjectTypeProvider(BeanProvider<MemoObjectType> memoObjectTypeProvider) {
        this.memoObjectTypeProvider = memoObjectTypeProvider;
    }

    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }

}
