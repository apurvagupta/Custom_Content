package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.base.User;
import com.jivesoftware.community.Comment;
import com.jivesoftware.community.CommentContentResource;
import com.jivesoftware.community.CommentableTypeInfoProviderSupport;
import com.jivesoftware.community.EntityDescriptor;
import com.jivesoftware.community.InterceptorManager;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.ext.memo.Memo;

public class MemoCommentableTypeInfoProvider extends CommentableTypeInfoProviderSupport {
    
    public int getCommentStatus(CommentContentResource commentTarget) throws NotFoundException {
        return commentTarget.getCommentStatus();
    }

    public InterceptorManager getInterceptorManager(CommentContentResource commentTarget) throws NotFoundException {
        return ((Memo)commentTarget).getJiveContainer().getInterceptorManager();
    }

    public EntityDescriptor getParentObject(CommentContentResource commentTarget) throws NotFoundException {
        return new EntityDescriptor(((Memo)commentTarget).getJiveContainer());
    }

    public boolean isAuthorizedInBackChannel(CommentContentResource commentTarget, User user) {
        return false;
    }

    public boolean isCommentAttachedToCommentTarget(EntityDescriptor commentTarget, EntityDescriptor comment) {
        return commentTarget.getID() == comment.getID();
    }

    public boolean isCommentsEnabled() {
        return true;
    }

    public boolean isDeleteable(CommentContentResource commentTarget, Comment comment) {
        return comment.getCommentContentResource() != null && commentTarget.getID() == comment.getCommentContentResource().getID();
    }

    public boolean isInDraftState(CommentContentResource commentTarget) {
        return false;
    }

    public boolean validateCommentOptions(CommentContentResource commentTarget, Comment comment, boolean isAnonymousUser) {
        return true;
    }

}
