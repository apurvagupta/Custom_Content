package com.jivesoftware.community.ext.memo.action;

import java.util.Date;


import com.jivesoftware.community.Image;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.impl.EmptyJiveIterator;
import com.jivesoftware.community.ext.memo.Memo;

public class EditMemoAction extends EditMemoActionSupport {

    public String input() {
        this.setTitle(memo.getTitle());
        this.setDescription(memo.getDescription());

        this.setTags(tagManager.getTagsAsString(memo));
        return super.input();
    }

    public String execute() {
        populateMemo();
        memo.setModificationDate(new Date());
        
        JiveIterator<Image> images = EmptyJiveIterator.getInstance();
        if(getSessionKey() != null && getSession().get(getSessionKey()) != null) {
            Memo temp = (Memo) getSession().get(getSessionKey());
            images = temp.getImages();
        }

        memoManager.updateMemo(memo, images);

        this.cleanSession();
        tagActionUtil.saveTags(memo, validatedTags);
        getTagSetActionHelper().setTagSets(memo, getContentTagSets());
        if (isMemoModerated()) {
            return SUCCESS_MODERATION;
        }

        return SUCCESS;
    }

    protected void populateMemo() {
        memo.setTitle(title);
        memo.setDescription(description);
    }

    public JiveContainer getContainer() {
        return memo.getJiveContainer();
    }

    public boolean isEdit() {
        return true;
    }    
}
