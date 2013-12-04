package com.jivesoftware.community.ext.memo.provider;

import java.util.ArrayList;
import java.util.List;

import com.jivesoftware.base.User;
import com.jivesoftware.community.ContainableTypeInfoProvider;
import com.jivesoftware.community.JiveConstants;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.JiveObjectLoader;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.aaa.authz.EntitlementTypeProvider.EntitlementType;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.objecttype.ContentObjectType;
import com.jivesoftware.community.objecttype.EntitlementCheckProvider;
import com.jivesoftware.community.stats.StatisticsGenerator;

import edu.emory.mathcs.backport.java.util.Collections;

public class MemoContainableTypeInfoProvider implements ContainableTypeInfoProvider {

    private List<StatisticsGenerator> statisticsGenerators = new ArrayList<StatisticsGenerator>();
    private JiveObjectLoader jiveObjectLoader;
    private EntitlementCheckProvider<Memo> entitlementCheckProvider;
    
    public JiveObject getContainerFor(JiveObject jiveObject) {
        if (jiveObject instanceof JiveContentObject) {
            JiveContentObject contentObject = (JiveContentObject) jiveObject;
            try {
                return jiveObjectLoader.getJiveObject(contentObject.getContainerType(), contentObject.getContainerID());
            }
            catch (NotFoundException e) {
                return null;
            }
        }
        return null;
    }

    public List<StatisticsGenerator> getStatisticsGenerators() {
        return Collections.unmodifiableList(statisticsGenerators);
    }

    public void setStatisticsGenerators(List<StatisticsGenerator> statisticsGenerators) {
        this.statisticsGenerators = statisticsGenerators;
    }

    public List<ContentObjectType> getSubContentTypes() {
        return new ArrayList<ContentObjectType>();
    }

    public String getTabViewID() {
        return "memo";
    }

    public JiveContainer getUserPersonalContainerForContentType(User owner) {
        return null;
    }

    public boolean isAvailableForContainer(int containerType) {
        switch(containerType) {
        case JiveConstants.COMMUNITY: 
        case JiveConstants.PROJECT:
        case JiveConstants.SOCIAL_GROUP:
        case JiveConstants.USER_CONTAINER:
            return true;
        default:
            return false;
        }
    }

    public boolean isEnabledByDefaultForContainer(int containerType) {
        return true;
    }

    public boolean isRequiredForContainer(int containerType) {
        return false;
    }

    public boolean userHasCreatePermsFor(JiveContainer container, User user) {
        return entitlementCheckProvider.isUserEntitled(container, EntitlementType.CREATE);
    }

    public void setJiveObjectLoader(JiveObjectLoader jiveObjectLoader) {
        this.jiveObjectLoader = jiveObjectLoader;
    }

    public void setEntitlementCheckProvider(EntitlementCheckProvider<Memo> entitlementCheckProvider) {
        this.entitlementCheckProvider = entitlementCheckProvider;
    }
}
