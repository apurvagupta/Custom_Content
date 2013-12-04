package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.community.ApprovalWorkflowView;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.jivesoftware.community.moderation.ModerationUIProvider;
import com.jivesoftware.community.objecttype.JiveObjectFactory;

public class MemoModerationUIProvider implements ModerationUIProvider {

    private BeanProvider<MemoObjectType> memoObjectTypeProvider;
    private JiveObjectFactory<Memo> objectFactory;
    
    public int getAdminOrder() {
        return memoObjectTypeProvider.get().getID();
    }

    public String getModerationDisplayName() {
        return "admin.moderation.settings.content.memo";
    }

    public ApprovalWorkflowView getModerationView(long id) {
        ApprovalWorkflowView view = null;
        try {
            view = new ApprovalWorkflowView(objectFactory.loadObject(id));
            view.setShowEditLink(false);
            view.setShowContextLink(false);
        } catch (NotFoundException e) {
            
        }
        
        return view;
    }

    public String getRootAdminString() {
        return "admin.moderation.settings.applied.root";
    }

    public String getSubContainerAdminString() {
        return " admin.moderation.settings.applied";
    }

    public boolean isAdminSetting() {
        return true;
    }

    public boolean isGlobalOnly() {
        return false;
    }

    public void setMemoObjectTypeProvider(BeanProvider<MemoObjectType> memoObjectTypeProvider) {
        this.memoObjectTypeProvider = memoObjectTypeProvider;
    }

    public void setObjectFactory(JiveObjectFactory<Memo> objectFactory) {
        this.objectFactory = objectFactory;
    }

}
