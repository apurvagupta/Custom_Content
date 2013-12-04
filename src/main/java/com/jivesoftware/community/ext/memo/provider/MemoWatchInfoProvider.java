package com.jivesoftware.community.ext.memo.provider;

import java.util.List;
import java.util.Map;

import com.jivesoftware.base.event.ContentEvent;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.base.User;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.Watch;
import com.jivesoftware.community.aaa.authz.EntitlementTypeProvider.EntitlementType;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.impl.AbstractContentWatchInfoProvider;
import com.jivesoftware.community.objecttype.EntitlementCheckProvider;

import edu.emory.mathcs.backport.java.util.Collections;

public class MemoWatchInfoProvider extends AbstractContentWatchInfoProvider {

    private EntitlementCheckProvider<Memo> entitlementCheckProvider;
    
    public boolean canUserWatchObject(JiveObject objectToWatch, User user, Map<String, Object> objectProperties) throws Exception {
        return entitlementCheckProvider.isUserEntitled((Memo)objectToWatch, EntitlementType.VIEW);
    }

    public List<User> getAuthorsToWatchFor(JiveObject object, Map<String, Object> objectProperties) {
        return Collections.singletonList(((Memo) object).getUser());
    }
   
    public boolean isNotifyOnEvent(ContentEvent event) {
        return !(event.getContentModificationType() == ContentEvent.ModificationType.Moderate);
    }

    @Override
    public boolean canWatcherCreateNewContent(Watch watch, JiveObject watchedContent, JiveContainer container) {
        return false;
    }

    @Override
    public boolean canWatcherRespond(Watch watch, JiveObject watchedContent, JiveContainer container) {
        return false;
    }

    public void setEntitlementCheckProvider(EntitlementCheckProvider<Memo> entitlementCheckProvider) {
        this.entitlementCheckProvider = entitlementCheckProvider;
    }
}
