package com.jivesoftware.community.ext.memo.action;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.community.ext.memo.Memo;

@RunWith(JMock.class)
public class MemoActionSupportTest {
	private Mockery context = new JUnit4Mockery();
	private MemoActionSupport action = new MemoActionSupport() {};
	
	@Test 
	public void getSetMemo() {
		Memo memo = context.mock(Memo.class);
		
		action.setMemo(memo);
		assertSame(memo, action.getMemo());
	}
}
