package com.jivesoftware.community.ext.memo.action;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.base.User;
import com.jivesoftware.base.UserTemplate;
import com.jivesoftware.base.aaa.AuthenticationProvider;
import com.jivesoftware.base.event.v2.EventDispatcher;
import com.jivesoftware.community.ContentTag;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.JiveObjectLoader;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.TagManager;
import com.jivesoftware.community.WatchManager;
import com.jivesoftware.community.bridge.BridgeManager;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoEvent;
import com.jivesoftware.community.impl.EmptyJiveIterator;
import com.opensymphony.xwork2.ActionSupport;

@RunWith(JMock.class)
public class ViewMemoActionTest {
	private Mockery context = new JUnit4Mockery();
	
	private ViewMemoAction action = new ViewMemoAction();
	
	private Memo memo = context.mock(Memo.class);
	private JiveContainer container = context.mock(JiveContainer.class);
	private TagManager tagManager = context.mock(TagManager.class);
	private EventDispatcher eventDispatcher = context.mock(EventDispatcher.class);
	private JiveObjectLoader jiveObjectLoader = context.mock(JiveObjectLoader.class);
	private BridgeManager bridgeManager = context.mock(BridgeManager.class);
	private AuthenticationProvider authProvider = context.mock(AuthenticationProvider.class);
	private WatchManager watchManager = context.mock(WatchManager.class);
	
	private long memoId = 1000L;
	private int memoObjectType = 333;
	private long containerId = 2000L;
	private int containerType = 44;
	
	@Before
	public void doBefore() {
		action.setMemo(memo);
		action.setTagManager(tagManager);
		action.setJiveEventDispatcher(eventDispatcher);
		action.setJiveObjectLoader(jiveObjectLoader);
		action.setBridgeManager(bridgeManager);
		action.setAuthenticationProvider(authProvider);
		action.setWatchManager(watchManager);
		
		context.checking(new Expectations() {{
			allowing(memo).getID(); will(returnValue(memoId));
			allowing(memo).getObjectType(); will(returnValue(memoObjectType));
			allowing(memo).getJiveContainer(); will(returnValue(container));
			allowing(memo).getContainerType(); will(returnValue(containerType));
			allowing(memo).getContainerID(); will(returnValue(containerId));
			allowing(container).getID(); will(returnValue(containerId));
			allowing(container).getObjectType(); will(returnValue(containerType));
		}});
	}
	
	@Test
	public void executeNoMemo() {
		action.setMemo(null);
		
		assertEquals(ActionSupport.ERROR, action.execute());
	}
	
	@Test
	public void executeNoEventDispatcher() {
		final JiveIterator<ContentTag> tags = EmptyJiveIterator.getInstance();
		
		action.setJiveEventDispatcher(null);
		
		context.checking(new Expectations() {{
			one(tagManager).getTags(memo); will(returnValue(tags));
		}});
		
		assertEquals(ActionSupport.SUCCESS, action.execute());
		assertSame(tags, action.getTags());
	}
	
	@Test
	public void execute() {
		final JiveIterator<ContentTag> tags = EmptyJiveIterator.getInstance();
		
		context.checking(new Expectations() {{
			one(tagManager).getTags(memo); will(returnValue(tags));
			one(eventDispatcher).fire(with(any(MemoEvent.class)));
		}});
		
		assertEquals(ActionSupport.SUCCESS, action.execute());
		assertSame(tags, action.getTags());
		
	}
	
	@Test
	public void getContainerNoMemo() {
		action.setMemo(null);
		
		assertNull(action.getContainer());
	}
	
	@Test
	public void getContainerNotFound() throws Exception {
		action.setContainer(container);
		
		context.checking(new Expectations() {{
			one(jiveObjectLoader).getJiveContainer(containerType, containerId); will(throwException(new NotFoundException()));
		}});
		
		assertSame(container, action.getContainer());
	}
	
	@Test
	public void getContainer() throws Exception {
		context.checking(new Expectations() {{
			one(jiveObjectLoader).getJiveContainer(containerType, containerId); will(returnValue(container));
		}});
		
		assertSame(container, action.getContainer());
	}

	@Test
	public void isWatchedNoUser() {
		context.checking(new Expectations() {{
			allowing(authProvider).getJiveUser(); will(returnValue(null));
		}});
		
		assertFalse(action.isWatched());
	}
	
	@Test
	public void isWatchedTrue() {
		final User user = new UserTemplate();
		
		context.checking(new Expectations() {{
			allowing(authProvider).getJiveUser(); will(returnValue(user));
			one(watchManager).isWatched(with(any(User.class)), with(equal(memo))); will(returnValue(true));
		}});
		
		assertTrue(action.isWatched());
	}
	
	@Test
	public void isWatchedFalse() {
		final User user = new UserTemplate();
		
		context.checking(new Expectations() {{
			allowing(authProvider).getJiveUser(); will(returnValue(user));
			one(watchManager).isWatched(with(any(User.class)), with(equal(memo))); will(returnValue(false));
		}});
		
		assertFalse(action.isWatched());
	}
}
