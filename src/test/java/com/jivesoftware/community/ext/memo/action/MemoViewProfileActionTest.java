package com.jivesoftware.community.ext.memo.action;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoResultFilter;
import com.jivesoftware.community.impl.ListJiveIterator;

@RunWith(JMock.class)
public class MemoViewProfileActionTest {
	private Mockery context = new JUnit4Mockery();
	
	private MemoViewProfileAction action = new MemoViewProfileAction();
	
	private MemoManager manager = context.mock(MemoManager.class);
	
	@Before
	public void doBefore() {
		action.setMemoManager(manager);
	}
	
	@Test
	public void getMemos() {
		final int count = 3;
		final List<Memo> memos = new ArrayList<Memo>();
		memos.add(context.mock(Memo.class, "one"));
		memos.add(context.mock(Memo.class, "two"));
		memos.add(context.mock(Memo.class, "three"));
		final JiveIterator<Memo> iterator = new ListJiveIterator<Memo>(memos);
		
		context.checking(new Expectations() {{
			one(manager).getMemos(with(any(MemoResultFilter.class))); will(returnValue(iterator));
			one(manager).getMemoCount(with(any(MemoResultFilter.class))); will(returnValue(count));
		}});
		
		assertSame(iterator, action.getMemos());
		assertSame(iterator, action.getMemos());
		
		assertEquals(count, action.getTotalItemCount());
	}
	
	@Test
	public void isMoreResultsAvailableTrue() {
		action.setTotalItemCount(30);
		action.setStart(0);
		action.setNumResults(25);
		
		assertTrue(action.isMoreResultsAvailable());
	}
	
	@Test
	public void isMoreResultsAvailableFalse() {
		action.setTotalItemCount(30);
		action.setStart(25);
		action.setNumResults(25);
	}
}
