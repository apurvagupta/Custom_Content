package com.jivesoftware.community.ext.memo.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.community.Community;
import com.jivesoftware.community.CommunityManager;
import com.jivesoftware.community.ContentTag;
import com.jivesoftware.community.ContentTagCloudBean;
import com.jivesoftware.community.JiveConstants;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.TagSet;
import com.jivesoftware.community.TagSetManager;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoResultFilter;
import com.jivesoftware.community.impl.EmptyJiveIterator;
import com.jivesoftware.community.impl.ListJiveIterator;
import com.jivesoftware.community.objecttype.impl.TagSetEnabledActionHelper;
import com.opensymphony.xwork2.ActionSupport;

@RunWith(JMock.class)
public class ViewMemosActionTest {
	private Mockery context = new JUnit4Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private ViewMemosAction action = new ViewMemosAction();
	
	private JiveContainer container = context.mock(JiveContainer.class);
	private MemoManager memoManager = context.mock(MemoManager.class);
	private TagSetEnabledActionHelper tagSetActionHelper = context.mock(TagSetEnabledActionHelper.class);
	private CommunityManager communityManager = context.mock(CommunityManager.class);
	private TagSetManager tagSetManager = context.mock(TagSetManager.class);
	
	@Before
	public void doBefore() {
		action.setContainer(container);
		action.setMemoManager(memoManager);
		action.setTagSetActionHelper(tagSetActionHelper);
		action.setTagSetManager(tagSetManager);
		action.setCommunityManager(communityManager);
	}
	
	@Test
	public void execute() {
		final int totalItemCount = 22;
		final JiveIterator<Memo> memos = EmptyJiveIterator.getInstance();
		
		context.checking(new Expectations() {{
			one(memoManager).getMemoCount(with(any(MemoResultFilter.class))); will(returnValue(totalItemCount));
			one(memoManager).getMemos(with(any(MemoResultFilter.class))); will(returnValue(memos));
		}});
		
		assertEquals(ActionSupport.SUCCESS, action.execute());
		assertEquals(totalItemCount, action.getTotalItemCount());
		assertSame(memos, action.getMemos());
	}
	
	@Test
	public void getResultFilterForTagSet() {
		final int start = 25;
		final int numResults = 5;
		final TagSet tagSet = context.mock(TagSet.class);
		final ContentTag tag = context.mock(ContentTag.class);
		
		action.setStart(start);
		action.setNumResults(numResults);
		action.setTagSet(tagSet);
		action.setTag(tag);
		
		MemoResultFilter filter = action.getResultFilter();
		assertNotNull(filter);
		assertEquals(start, filter.getStartIndex());
		assertEquals(numResults, filter.getNumResults());
		assertTrue(filter.getTagSets().contains(tagSet));
		assertTrue(filter.getTags().contains(tag));
	}

	@Test
	public void getResultFilterForContainer() {
		final int start = 25;
		final int numResults = 5;
		final ContentTag tag = context.mock(ContentTag.class);
		
		action.setStart(start);
		action.setNumResults(numResults);
		action.setTag(tag);
		
		MemoResultFilter filter = action.getResultFilter();
		assertNotNull(filter);
		assertEquals(start, filter.getStartIndex());
		assertEquals(numResults, filter.getNumResults());
		assertEquals(container, filter.getContainer());
		assertTrue(filter.getTags().contains(tag));
	}
	
	@Test
	public void getTagSetsForContainer() {
		final JiveIterator<TagSet> tagSets = EmptyJiveIterator.getInstance();
		
		context.checking(new Expectations() {{
			one(tagSetActionHelper).getTagSets(container); will(returnValue(tagSets));
		}});
		
		assertSame(tagSets, action.getTagSets(container));
	}
	
	@Test
	public void getUsageCount() {
		final TagSet tagSet = context.mock(TagSet.class);
		final int objectType = 56;
		final Long count = 45L;
		
		context.checking(new Expectations() {{
			one(tagSetActionHelper).getUsageCount(tagSet, objectType); will(returnValue(count));
		}});
		
		assertEquals(count, action.getUsageCount(tagSet, objectType));
	}
	
	@Test
	public void getTagCloud() {
		final TagSet tagSet = context.mock(TagSet.class);
		final List<ContentTagCloudBean> cloud = new ArrayList<ContentTagCloudBean>();
		
		context.checking(new Expectations() {{
			one(tagSetActionHelper).getTagCloud(container, tagSet); will(returnValue(cloud));
		}});
		
		assertSame(cloud, action.getTagCloud(container, tagSet));
	}
	
	@Test
	public void getTagSetsForCommunity() {
		final long communityId = 1000L;
		final long rootCommunityId = 1L;
		final Community community = context.mock(Community.class, "community");
		final Community rootCommunity = context.mock(Community.class, "root");
		
		TagSet ts = context.mock(TagSet.class);
		final Iterator<TagSet> tagSets = new ListJiveIterator<TagSet>(Arrays.asList(ts));
		
		action.setContainerType(JiveConstants.COMMUNITY);
		action.setContainerID(communityId);
		
		context.checking(new Expectations() {{
			allowing(communityManager).getRootCommunity(); will(returnValue(rootCommunity));
			allowing(rootCommunity).getID(); will(returnValue(rootCommunityId));
			one(tagSetManager).getTagSets(container); will(returnValue(tagSets));
		}});
		
		assertSame(tagSets, action.getTagSets());
	}
}
