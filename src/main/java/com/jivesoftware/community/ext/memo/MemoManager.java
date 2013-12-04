package com.jivesoftware.community.ext.memo;

import com.jivesoftware.base.User;
import com.jivesoftware.community.Image;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.JiveIterator;

public interface MemoManager {

    public int getMemoCount(JiveContainer container);

    public int getMemoCount(MemoResultFilter resultFilter);

    public JiveIterator<Memo> getMemos(MemoResultFilter resultFilter);

    public JiveIterator<Memo> getAllMemos();

    public Memo saveMemo(MemoBean memoBean, JiveIterator<Image> images);

    public Memo updateMemo(Memo memo, JiveIterator<Image> images);
    
    public Memo updateMemoStatus(Memo memo, Status status);

    public Memo getMemo(long id);

    public void deleteMemo(Memo memo);

    public void deleteMemos(JiveContainer container);
    
    public void deleteMemos(User user);
    
    public void moveMemo(Memo memo, JiveContainer target);
}
