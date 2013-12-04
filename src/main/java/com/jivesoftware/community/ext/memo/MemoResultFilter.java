package com.jivesoftware.community.ext.memo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.google.common.collect.Lists;
import com.jivesoftware.base.User;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.ResultFilter;
import com.jivesoftware.community.JiveContentObject.Status;

public class MemoResultFilter extends ResultFilter {
    private JiveContainer container;
    private User user;
    private boolean viewingSelf;
    private List<Status> status = new ArrayList<Status>();

    public static MemoResultFilter createDefaultFilter() {
        MemoResultFilter resultFilter = new MemoResultFilter();
        resultFilter.setSortField(MemoConstants.MODIFICATION_DATE);
        resultFilter.setSortOrder(DESCENDING);
        resultFilter.getStatus().add(Status.PUBLISHED);
        resultFilter.getStatus().add(Status.ABUSE_VISIBLE);
        return resultFilter;
    }

    public JiveContainer getContainer() {
        return container;
    }

    public void setContainer(JiveContainer container) {
        this.container = container;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isViewingSelf() {
        return viewingSelf;
    }

    public void setViewingSelf(boolean viewingSelf) {
        this.viewingSelf = viewingSelf;
    }

    public void setStatus(Status... status) {
        this.status = status == null ? null : Lists.newArrayList(status);
    }
    
    public List<Status> getStatus() {
        return status;
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        
        if(!(o instanceof MemoResultFilter)) {
            return false;
        }
        
        MemoResultFilter that = (MemoResultFilter) o;
        
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(this.container, that.container);
        builder.append(this.user, that.user);
        builder.append(this.viewingSelf, that.viewingSelf);
        return builder.isEquals();
    }
}
