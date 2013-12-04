package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.community.objecttype.ContainerContentInfoProvider;

public class MemoContainerContentInfoProvider implements ContainerContentInfoProvider {

    @Override
    public String getMainBodyFtl() {
        return "/plugins/memo-type/resources/templates/container-memos.ftl";
    }

    @Override
    public String getSidebarFtl() {
        return null;
    }

}
