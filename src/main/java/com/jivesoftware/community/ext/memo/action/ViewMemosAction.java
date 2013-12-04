package com.jivesoftware.community.ext.memo.action;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.jivesoftware.community.CommunityManager;
import com.jivesoftware.community.ContentTag;
import com.jivesoftware.community.ContentTagCloudBean;
import com.jivesoftware.community.JiveConstants;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.ResultFilter;
import com.jivesoftware.community.TagSet;
import com.jivesoftware.community.TagSetManager;
import com.jivesoftware.community.action.JiveActionSupport;
import com.jivesoftware.community.action.util.Decorate;
import com.jivesoftware.community.action.util.Pageable;
import com.jivesoftware.community.action.util.Paginator;
import com.jivesoftware.community.action.util.SimplePaginator;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoResultFilter;
import com.jivesoftware.community.objecttype.impl.TagSetEnabledActionHelper;
import com.jivesoftware.community.web.struts.SetReferer;

@SetReferer(false)
@Decorate(false)
public class ViewMemosAction extends JiveActionSupport implements Pageable {

    private Iterable<Memo> memos;

    // Pageable
    private int start;
    private int numResults = 30;
    private int totalItemCount;

    protected MemoManager memoManager;

    private TagSet tagSet;
    private ContentTag tag;

    protected TagSetManager tagSetManager;
    protected TagSetEnabledActionHelper tagSetActionHelper;
    protected CommunityManager communityManager;

    public void setTagSetManager(TagSetManager tagSetManager) {
        this.tagSetManager = tagSetManager;
    }
    
    public void setTagSetActionHelper(TagSetEnabledActionHelper tagSetActionHelper) {
        this.tagSetActionHelper = tagSetActionHelper;
    }
    
    public void setCommunityManager(CommunityManager communityManager) {
        this.communityManager = communityManager;
    }

    
    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }

    @Override
    public String execute() {
        String result = super.execute();
        if (!SUCCESS.equals(result)) {
            return result;
        }

        processMemosWithAPI();

        return SUCCESS;
    }

    protected void processMemosWithAPI() {
        processMemoCountWithAPI();
        processMemoContentWithAPI();
    }

    private int processMemoCountWithAPI() {
        if (totalItemCount > 0) {
            return totalItemCount;
        }

        MemoResultFilter filter = getResultFilter();
        filter.setStartIndex(0);
        filter.setNumResults(ResultFilter.NULL_INT);
        totalItemCount = memoManager.getMemoCount(filter);
        return totalItemCount;
    }

    protected void processMemoContentWithAPI() {
        memos = memoManager.getMemos(getResultFilter());
    }

    public Iterable<Memo> getMemos() {
        return memos;
    }

    public TagSet getTagSet() {
        return tagSet;
    }

    public void setTagSet(TagSet tagSet) {
        this.tagSet = tagSet;
    }
    
    public ContentTag getTag() {
        return tag;
    }
    
    public void setTag(ContentTag tag) {
        this.tag = tag;
    }
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public MemoResultFilter getResultFilter() {
        // setup result filter
        MemoResultFilter resultFilter = MemoResultFilter.createDefaultFilter();

        if (start > 0) {
            resultFilter.setStartIndex(start);
        }
        if (numResults > 0) {
            resultFilter.setNumResults(numResults);
        }

        if (getTagSet() != null) {
            resultFilter.getTagSets().add(tagSet);
        } else {
            resultFilter.setContainer(getContainer());
        }
        
        if(tag != null) {
            resultFilter.addTag(tag);
        }
        return resultFilter;
    }

    public Iterable<TagSet> getTagSets(JiveContainer container) {
        return tagSetActionHelper.getTagSets(container);
    }

    public Long getUsageCount(TagSet tagSet, int objectType) {
        return tagSetActionHelper.getUsageCount(tagSet, objectType);
    }

    public List<ContentTagCloudBean> getTagCloud(JiveContainer jiveContainer, TagSet tagSet){
        return tagSetActionHelper.getTagCloud(jiveContainer, tagSet);
    }

    public Iterator<TagSet> getTagSets() {
        if (getContainerType() == JiveConstants.COMMUNITY && getContainerID() != communityManager.getRootCommunity().getID()) {
            return tagSetManager.getTagSets(getContainer());
        }

        return Collections.<TagSet>emptyList().iterator();
    }
    public Paginator getNewPaginator() {
        return new SimplePaginator(this, getNumResults(), isMoreResultsAvailable());
    }

    public boolean isMoreResultsAvailable() {
        return getTotalItemCount() > getStart() + getNumResults();
    }
}
