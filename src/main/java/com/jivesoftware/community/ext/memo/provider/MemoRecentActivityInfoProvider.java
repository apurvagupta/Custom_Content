package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.base.User;
import com.jivesoftware.base.event.ContentEvent;
import com.jivesoftware.base.event.v2.BaseJiveEvent;
import com.jivesoftware.community.Activity;
import com.jivesoftware.community.ActivityEventHandlingStrategy;
import com.jivesoftware.community.impl.BaseRecentActivityInfoProvider;
import com.jivesoftware.community.impl.activity.DefaultContentActivityEventHandlingStrategy;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoEvent;

public class MemoRecentActivityInfoProvider extends BaseRecentActivityInfoProvider {

    @Override
    public ActivityEventHandlingStrategy getEventHandlingStrategy() {
        return new MemoContentActivityEventHandlingStrategy();
    }
    
    @Override
    public boolean isUserAuthorizedToViewActivity(Activity activity, User user) {
        return super.isUserAuthorizedToViewActivity(activity, user) && ((Memo) activity.getJiveObject()).getStatus().isVisible();
    }
    
    public class MemoContentActivityEventHandlingStrategy extends DefaultContentActivityEventHandlingStrategy {

        @Override
        public boolean isActivityRecordable(BaseJiveEvent event) {
           if(((MemoEvent)event).getContentModificationType() == ContentEvent.ModificationType.Moderate) {
                return false;
            }
               
            return true;
        }
   
    }
}
