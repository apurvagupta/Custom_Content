package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.base.User;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.TaggableTypeInfoProvider;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.jivesoftware.community.objecttype.ContainableType;

public class MemoTaggableTypeInfoProvider implements TaggableTypeInfoProvider {

    private BeanProvider<MemoObjectType> memoObjectTypeProvider;
    
    public ContainableType getContainableType() {
        return memoObjectTypeProvider.get();
    }

    public boolean isAllowedToTag(JiveObject object, User user) {
        return true;
    }

    public void setMemoObjectTypeProvider(BeanProvider<MemoObjectType> memoObjectTypeProvider) {
        this.memoObjectTypeProvider = memoObjectTypeProvider;
    }

}
