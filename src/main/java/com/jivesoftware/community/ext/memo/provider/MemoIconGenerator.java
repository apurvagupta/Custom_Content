package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.util.BaseIconGenerator;

public class MemoIconGenerator extends BaseIconGenerator {

    public static final String SMALL = "jive-icon-sml jive-icon-memo-sml";
    public static final String MEDIUM = "jive-icon-med jive-icon-memo-med";
    public static final String BIG = "jive-icon-big jive-icon-memo-big";
    
    public String getCommentIcon(JiveObject object, boolean returnAsCssClass, int type) {
        return getIcon(object, returnAsCssClass, type);
    }

    public String getIcon(boolean returnAsCssClass, int type) {
        return getIcon(null, returnAsCssClass, type);
    }

    public String getIcon(JiveObject object, boolean returnAsCssClass, int type) {
        switch(type) {
        case 0:
            return SMALL;
        case 1:
            return MEDIUM;
        case 2:
            return BIG;
        case 3:
        case 4:
        case 5:
            return BIG;
        default:
            return MEDIUM;
        }
    }
}
