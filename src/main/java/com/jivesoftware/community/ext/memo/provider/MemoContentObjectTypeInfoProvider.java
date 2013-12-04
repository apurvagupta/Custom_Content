package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.aaa.authz.EntitlementTypeProvider.EntitlementType;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.jivesoftware.community.objecttype.ContainableType;
import com.jivesoftware.community.objecttype.ContentObjectTypeInfoProvider;
import com.jivesoftware.community.objecttype.EntitlementCheckProvider;
import com.jivesoftware.community.objecttype.UserProfileInfoProvider;

public class MemoContentObjectTypeInfoProvider implements ContentObjectTypeInfoProvider {
    private static final String USER_MEMO_COUNT = "SELECT count(*) FROM jiveMemo WHERE userID=?";
    
    private BeanProvider<MemoObjectType> memoObjectTypeProvider;
    private UserProfileInfoProvider userProfileInfoProvider;
    private EntitlementCheckProvider<Memo> entitlementCheckProvider;
    
    public ContainableType getContainableType() {
        return memoObjectTypeProvider.get();
    }

    public String getCreateNewFormRelativeURL(JiveContainer targetContainer, boolean isUpload, String tempObjectId, String tags, String subject) {
        StringBuilder url = new StringBuilder();
        url.append("create-memo!input.jspa?");
        url.append("container=").append(targetContainer.getID());
        url.append("&containerType=").append(targetContainer.getObjectType());
        return url.toString();
    }

    public String getUserContentCountQuery() {
        return USER_MEMO_COUNT;
    }

    public UserProfileInfoProvider getUserProfileInfoProvider() {
        return userProfileInfoProvider;
    }

    public void setUserProfileInfoProvider(UserProfileInfoProvider userProfileInfoProvider) {
        this.userProfileInfoProvider = userProfileInfoProvider;
    }

    public boolean isBinaryBodyUploadCapable() {
        return false;
    }

    public boolean userHasCreatePermsFor(JiveContainer container) {
        return entitlementCheckProvider.isUserEntitled(container, EntitlementType.CREATE);
    }

    public void setEntitlementCheckProvider(EntitlementCheckProvider<Memo> entitlementCheckProvider) {
        this.entitlementCheckProvider = entitlementCheckProvider;
    }

    public void setMemoObjectTypeProvider(BeanProvider<MemoObjectType> memoObjectTypeProvider) {
        this.memoObjectTypeProvider = memoObjectTypeProvider;
    }

}
