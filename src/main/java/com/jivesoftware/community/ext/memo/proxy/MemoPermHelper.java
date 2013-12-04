package com.jivesoftware.community.ext.memo.proxy;

import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.aaa.authz.EntitlementTypeProvider.EntitlementType;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.lifecycle.JiveApplication;
import com.jivesoftware.community.objecttype.ContainableType;
import com.jivesoftware.community.objecttype.EntitlementCheckProvider;
import com.jivesoftware.community.util.BasePermHelper;

public class MemoPermHelper extends BasePermHelper {
    public static boolean getCanCreateMemo(JiveContainer container) {
        return getEntitlementCheckProvider().isUserEntitled(container, EntitlementType.CREATE);
    }

    public static boolean getCanViewMemos(JiveContainer container) {
        return getEntitlementCheckProvider().isUserEntitled(container, EntitlementType.VIEW);
    }
    
    public static boolean getCanDeleteMemo(Memo memo) {
        return getEntitlementCheckProvider().isUserEntitled(memo, EntitlementType.DELETE);
    }
    
    public static boolean getCanUpdateMemo(Memo memo) {
        return getEntitlementCheckProvider().isUserEntitled(memo, EntitlementType.EDIT);
    }
    
    public static boolean areMemosEnabled(JiveContainer container) {
        for (ContainableType type : container.getContentTypes()) {
            if (type.getID() == new MemoObjectType().getID()) {
                return true;
            }
        }

        return false;
    }

    private static EntitlementCheckProvider<Memo> getEntitlementCheckProvider() {
        return (EntitlementCheckProvider<Memo>)JiveApplication.getContext().getSpringBean("memoEntitlementCheckProvider");
    }
}
