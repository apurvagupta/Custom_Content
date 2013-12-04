package com.jivesoftware.community.ext.memo.dao;

import java.util.List;

import com.jivesoftware.base.User;
import com.jivesoftware.community.EntityDescriptor;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoBean;
import com.jivesoftware.community.ext.memo.MemoResultFilter;
import com.jivesoftware.community.impl.CachedPreparedStatement;

public interface MemoDao {
    public MemoBean getMemo(long id);

    public long saveMemo(MemoBean object);

    public void updateMemo(MemoBean object);

    public void deleteMemo(long id);
    
    public void moveMemo(Memo memo, JiveContainer destination);

    public CachedPreparedStatement getMemoListSQL(MemoResultFilter resultFilter);

    public CachedPreparedStatement getMemoListCountSQL(MemoResultFilter resultFilter);

    public CachedPreparedStatement getAllMemosCountSQL();

    public CachedPreparedStatement getAllMemosSQL();
    
    public List<Long> getAllMemoIDs(JiveContainer container);
    
    public List<Long> getAllMemoIDs(User user);
    
    public void deleteMemos(List<Long> memoIDs);

    public List<EntityDescriptor> getFrequentMemoContainers(long userID);
}
