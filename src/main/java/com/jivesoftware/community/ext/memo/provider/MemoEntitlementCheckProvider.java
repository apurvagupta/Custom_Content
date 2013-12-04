package com.jivesoftware.community.ext.memo.provider;

import org.apache.log4j.Logger;

import com.jivesoftware.base.User;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.aaa.authz.EntitlementTypeProvider.EntitlementType;
import com.jivesoftware.community.aaa.authz.EntitlementTypeProvider.Type;
import com.jivesoftware.community.entitlements.Mask;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.impl.BaseEntitlementProvider;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.jivesoftware.community.util.JiveContainerPermHelper;

public class MemoEntitlementCheckProvider extends BaseEntitlementProvider<Memo> {
    private static final Logger log = Logger.getLogger(MemoEntitlementCheckProvider.class);
    
    private BeanProvider<MemoObjectType> memoObjectTypeProvider;
    
    @Override
    public boolean isUserEntitled(Memo memo, Type type) {
        return isUserEntitled(getEffectiveUser(), memo, type);
    }

    @Override
    public boolean isUserEntitled(JiveContainer container, Type type) {
        return isUserEntitled(getEffectiveUser(), container, type);
    }

    @Override
    public boolean isUserEntitled(User user, Memo memo, Type type) {
        if (isAnyNull(user, memo, type)) {
            return false;
        }

        if (EntitlementType.EDIT.equals(type)) {
            return ownerOperationAllowed(user, memo);
        }
        else if (EntitlementType.DELETE.equals(type)) {
            return ownerOperationAllowed(user, memo);
        }
        else if (EntitlementType.VIEW.equals(type)) {
            return handleView(user, memo);
        }
        else if (EntitlementType.CREATE_COMMENT.equals(type)) {
            return isUserEntitled(user, memo.getJiveContainer(), type);
        } 

        log.warn("ObservationEntitlementCheckProvider.isUserEntitled(user, post, type) unable to handle the type '"
                + type + "'", new Exception());
        return false;
    }

    @Override
    public boolean isUserEntitled(User user, JiveContainer container, Type type) {
        if (isAnyNull(user, container, type) || isBannedFromPosting(user)) {
            return false;
        }

        Mask mask;
        if (EntitlementType.CREATE.equals(type)) {
            mask = getCreateMask();
        }
        else if (EntitlementType.ADMIN.equals(type)) {
            mask = getAdminMask();
        }
        else if (EntitlementType.VIEW.equals(type)) {
            mask = getViewMask();
        }
        else if (EntitlementType.CREATE_COMMENT.equals(type)) {
            mask = getCommentMask();
        }
        else {
            return false;
        }

        return isEntitled(user, container, mask);

    }

    protected boolean ownerOperationAllowed(User user, Memo memo) {
        if (isBannedFromPosting(user)) {
            return false;
        }
        
        boolean canCreateInContainer = isEntitled(user, memo.getJiveContainer(), getCreateMask());
        
        return (canCreateInContainer && isOwner(memo, user)) || isAdmin(memo, user) || isModerator(memo, user);
    }

    protected boolean handleView(User user, Memo memo) {
        // User must be the owner or a moderator if the memo is in a moderated state
        if (memo.getStatus() == JiveContentObject.Status.ABUSE_HIDDEN
                || memo.getStatus() == JiveContentObject.Status.AWAITING_MODERATION)
        {
            if (!isOwner(memo, user) || isAdmin(memo, user) || isModerator(memo, user)) {
                return false;
            }
        }
        
        return isEntitled(user, memo.getJiveContainer(), getViewMask());

    }
    
    protected boolean isEntitled(User user, JiveContainer container, Mask mask) {
        int objectType = memoObjectTypeProvider.get().getID();
        if (super.isEntitled(user, getEntitledContainer(container, objectType), objectType, mask)) {
            return true;
        }

        return false;
    }

    protected boolean isOwner(Memo memo, User user) {
        if (user == null || user.isAnonymous()) {
            return false;
        }

        return memo.getUser().getID() == user.getID();
    }
    
    protected boolean isAdmin(Memo memo, User user) {
        return JiveContainerPermHelper.isContainerAdmin(getEntitledContainer(memo.getJiveContainer(), memoObjectTypeProvider.get().getID()), user);
    }
    
    protected boolean isModerator(Memo memo, User user) {
        return JiveContainerPermHelper.isContainerModerator(getEntitledContainer(memo.getJiveContainer(), memoObjectTypeProvider.get().getID()), user);
    }

    public void setMemoObjectTypeProvider(BeanProvider<MemoObjectType> memoObjectTypeProvider) {
        this.memoObjectTypeProvider = memoObjectTypeProvider;
    }

}
