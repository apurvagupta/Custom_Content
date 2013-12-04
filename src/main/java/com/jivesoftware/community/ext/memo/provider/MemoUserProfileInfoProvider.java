package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.community.objecttype.UserProfileInfoProvider;

public class MemoUserProfileInfoProvider implements UserProfileInfoProvider {

    public String getActionName() {
        return "view-profile-memos";
    }

    public String getMainBodyFtl() {
        return "/plugins/memo-type/resources/templates/view-memos.ftl";
    }

    public String getSelfActionFtl() {
        return "/plugins/memo-type/resources/templates/view-profile-memo-action.ftl";
    }

    @Override
    public String getActionPackageNamespace() {
        return "";
    }

    @Override
    public String getProfileViewSidebarAction() {
        return null;
    }
}
