package com.jivesoftware.community.ext.memo.action;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.opensymphony.xwork2.ActionSupport;

@RunWith(JMock.class)
public class DeleteMemoActionTest {
	private Mockery context = new JUnit4Mockery();
	
	private DeleteMemoAction action = new DeleteMemoAction();
	
	private MemoManager manager = context.mock(MemoManager.class);
	
	@Before
	public void doBefore() {
		action.setMemoManager(manager);
	}
	
	@Test
	public void execute() {
		final Memo memo = context.mock(Memo.class);
		action.setMemo(memo);
		
		context.checking(new Expectations() {{
			one(manager).deleteMemo(memo);
		}});
		
		assertEquals(ActionSupport.SUCCESS, action.execute());
	}
}
