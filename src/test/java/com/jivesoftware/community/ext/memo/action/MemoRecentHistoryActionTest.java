package com.jivesoftware.community.ext.memo.action;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.community.EntityDescriptor;
import com.jivesoftware.community.RecentHistoryManager;
import com.jivesoftware.community.RecentHistoryManager.HistoryArchetype;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.opensymphony.xwork2.ActionSupport;

@RunWith(JMock.class)
public class MemoRecentHistoryActionTest {
	private Mockery context = new JUnit4Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private MemoRecentHistoryAction action = new TestMemoRecentHistoryAction();
	
	private RecentHistoryManager recentHistoryManager = context.mock(RecentHistoryManager.class);
	private BeanProvider<MemoObjectType> memoObjectTypeProvider = context.mock(BeanProvider.class);
	private MemoManager memoManager = context.mock(MemoManager.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	
	@Before
	public void doBefore() {
		action.setRecentHistoryManager(recentHistoryManager);
		action.setMemoObjectTypeProvider(memoObjectTypeProvider);
		action.setMemoManager(memoManager);
		action.setServletRequest(request);
		
		context.checking(new Expectations() {{
			allowing(memoObjectTypeProvider).get(); will(returnValue( new MemoObjectType()));
		}});
	}
	
	@Test
	public void executeNoHistory() {
		context.checking(new Expectations() {{
			one(recentHistoryManager).getRecentHistory(request); will(returnValue(null));
		}});
		
		assertEquals(ActionSupport.SUCCESS, action.execute());
		assertNotNull(action.getMemoHistory());
		assertTrue(action.getMemoHistory().isEmpty());
	}
	
	@Test
	public void executeEmptyHistoryForType() {
		final Map<HistoryArchetype, List<EntityDescriptor>> history = new HashMap<HistoryArchetype, List<EntityDescriptor>>();
		
		context.checking(new Expectations() {{
			one(recentHistoryManager).getRecentHistory(request); will(returnValue(history));
		}});

		assertEquals(ActionSupport.SUCCESS, action.execute());
		assertNotNull(action.getMemoHistory());
		assertTrue(action.getMemoHistory().isEmpty());
	}
	
	@Test
	public void execute() {
		final long memo1000Id = 1000L;
		final long memo2000Id = 2000L;
		final long memo3000Id = 3000L;
		
		final Memo memo1000 = context.mock(Memo.class, "memo1000");
		final Memo memo2000 = context.mock(Memo.class, "memo2000");
		final Memo memo3000 = context.mock(Memo.class, "memo3000");
		
		final Map<HistoryArchetype, List<EntityDescriptor>> history = new HashMap<HistoryArchetype, List<EntityDescriptor>>();
		final List<EntityDescriptor> descriptors = new ArrayList<EntityDescriptor>();
		descriptors.add(new EntityDescriptor(MemoObjectType.MEMO_TYPE_ID, memo1000Id));
		descriptors.add(new EntityDescriptor(MemoObjectType.MEMO_TYPE_ID, memo2000Id));
		descriptors.add(new EntityDescriptor(MemoObjectType.MEMO_TYPE_ID, memo3000Id));
		
		history.put(HistoryArchetype.Content, descriptors);
		
		context.checking(new Expectations() {{
			one(recentHistoryManager).getRecentHistory(request); will(returnValue(history));
			one(memoManager).getMemo(memo1000Id); will(returnValue(memo1000));
			one(memoManager).getMemo(memo2000Id); will(returnValue(memo2000));
			one(memoManager).getMemo(memo3000Id); will(returnValue(memo3000));
		}});
		
		assertEquals(ActionSupport.SUCCESS, action.execute());
		
		List<Memo> result = action.getMemoHistory();
		assertNotNull(result);
		assertEquals(3, result.size());
		assertTrue(result.contains(memo1000));
		assertTrue(result.contains(memo2000));
		assertTrue(result.contains(memo3000));
	}
	
	class TestMemoRecentHistoryAction extends MemoRecentHistoryAction {
		protected HistoryArchetype getRecentHistoryArchetype(int objectTypeId) {
			return HistoryArchetype.Content;
		}
	}
}
