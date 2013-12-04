package com.jivesoftware.community.ext.memo.action;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.community.Community;
import com.jivesoftware.community.CommunityManager;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.opensymphony.xwork2.ActionSupport;

@RunWith(JMock.class)
public class MainMemosActionTest {
	private Mockery context = new JUnit4Mockery();
	
	private MainMemosAction action = new MainMemosAction();
	
	private MemoManager memoManager = context.mock(MemoManager.class);
	private BeanProvider<MemoObjectType> memoObjectTypeProvider = context.mock(BeanProvider.class);
	private CommunityManager communityManager = context.mock(CommunityManager.class);
	private Community rootCommunity = context.mock(Community.class);
	
	
	@Before
	public void doBefore() {
		action.setMemoManager(memoManager);
		action.setMemoObjectTypeProvider(memoObjectTypeProvider);
		action.setCommunityManager(communityManager);

		context.checking(new Expectations() {{
			one(communityManager).getRootCommunity(); will(returnValue(rootCommunity));
		}});		
	}
	
	@Test
	public void prepare() throws Exception {
		action.prepare();
		
		assertEquals(rootCommunity, action.getCommunity());
	}
	
	@Test
	public void setCommunity() throws Exception {
		Community community = context.mock(Community.class, "newCommunity");
		
		action.prepare();
		action.setCommunity(community);
		
		//setCommunity does nothing, so make sure we're still set to use the root community
		assertEquals(rootCommunity, action.getCommunity());
	}
	
	@Test
	public void execute() throws Exception {
		final int count = 100;
		
		context.checking(new Expectations() {{
			one(memoManager).getMemoCount(rootCommunity); will(returnValue(count));
		}});
		
		action.prepare();
		assertEquals(ActionSupport.SUCCESS, action.execute());
		
		assertEquals(count, action.getMemoCount());
	}
}
