package com.jivesoftware.community.ext.memo.provider;

import static com.jivesoftware.community.ext.memo.test.matcher.MemoResultFilterMatcher.aMemoResultFilterContaining;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.base.User;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.impl.ListJiveIterator;

@RunWith(JMock.class)
public class MemoContainableTypeManagerTest {
	private Mockery context = new JUnit4Mockery();
	
	private MemoContainableTypeManager manager = new MemoContainableTypeManager();
	
	private MemoManager memoManager = context.mock(MemoManager.class);
	
	@Before
	public void doBefore() {
		manager.setMemoManager(memoManager);
	}
	
	@Test
	public void deleteAllContentForContainer() {
		final JiveContainer container = context.mock(JiveContainer.class);
		
		context.checking(new Expectations() {{
			one(memoManager).deleteMemos(container);
		}});
		
		manager.deleteAllContent(container);
	}
	
	@Test
	public void deleteAllContentForUser() {
		final User user = context.mock(User.class);
		
		context.checking(new Expectations() {{
			one(memoManager).deleteMemos(user);
		}});
		
		manager.deleteAllContent(user);
	}
	
	@Test 
	public void getContentCountForUser() {
		final User user = context.mock(User.class);
		final int count = 35;
		
		context.checking(new Expectations() {{
			allowing(user).getID(); will(returnValue(1000L));
			one(memoManager).getMemoCount(with(aMemoResultFilterContaining(user))); will(returnValue(count));
		}});
		
		assertEquals(count, manager.getContentCount(user));
	}
	
	@Test
	public void getContentCountForContainer() {
		final JiveContainer container = context.mock(JiveContainer.class);
		final boolean recursive = true;
		final int count = 3456;
		
		context.checking(new Expectations() {{
			allowing(container).getID(); will(returnValue(1000L));
			allowing(container).getObjectType(); will(returnValue(500));
			one(memoManager).getMemoCount(with(aMemoResultFilterContaining(container, recursive))); will(returnValue(count));
		}});
		
		assertEquals(count, manager.getContentCount(container, recursive));
	}
	
	@Test
	public void migrateAllContent() {
		final JiveContainer source = context.mock(JiveContainer.class, "source");
		final JiveContainer dest = context.mock(JiveContainer.class, "dest");
		final long sourceId = 1000L;
		final long destId = 2000L;
		final int count = 3;
		final Memo memo1 = context.mock(Memo.class, "memo1");
		final Memo memo2 = context.mock(Memo.class, "memo2");
		final Memo memo3 = context.mock(Memo.class, "memo3");
		final long memo1Id = 1L;
		final long memo2Id = 2L;
		final long memo3Id = 3L;
		
		final JiveIterator<Memo> memos = new ListJiveIterator<Memo>(Arrays.asList(memo1, memo2, memo3));
		
		context.checking(new Expectations() {{
			one(memoManager).getMemoCount(with(aMemoResultFilterContaining(source, false))); will(returnValue(count));
			one(memoManager).getMemos(with(aMemoResultFilterContaining(source, false))); will(returnValue(memos));
			allowing(memo1).getID(); will(returnValue(memo1Id));
			allowing(memo2).getID(); will(returnValue(memo2Id));
			allowing(memo3).getID(); will(returnValue(memo3Id));
			allowing(source).getID(); will(returnValue(sourceId));
			allowing(source).getObjectType(); will(returnValue(14));
			allowing(dest).getID(); will(returnValue(destId));
			allowing(dest).getObjectType(); will(returnValue(14));
			
			for(Memo memo : memos) {
				one(memoManager).moveMemo(memo, dest);
			}
		}});
		
		manager.migrateAllContent(source, dest);
	}
}
