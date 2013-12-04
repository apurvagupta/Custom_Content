package com.jivesoftware.community.ext.memo.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.base.User;
import com.jivesoftware.base.UserTemplate;
import com.jivesoftware.base.aaa.AuthenticationProvider;
import com.jivesoftware.community.Image;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.action.JiveActionSupport;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoBean;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.impl.EmptyJiveIterator;
import com.jivesoftware.community.moderation.JiveObjectModerator;
import com.jivesoftware.community.objecttype.impl.TagSetEnabledActionHelper;
import com.jivesoftware.community.tags.TagActionUtil;
import com.opensymphony.xwork2.ActionSupport;

@RunWith(JMock.class)
public class CreateMemoActionTest {
	private Mockery context = new JUnit4Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private CreateMemoAction action = new TestCreateMemoAction();
	
	private MemoManager memoManager = context.mock(MemoManager.class);
	private JiveContainer container = context.mock(JiveContainer.class);
	private AuthenticationProvider authProvider = context.mock(AuthenticationProvider.class);
	private UserTemplate user = new UserTemplate();
	private TagActionUtil tagActionUtil = context.mock(TagActionUtil.class);
	private TagSetEnabledActionHelper tagSetActionHelper = context.mock(TagSetEnabledActionHelper.class);
	private JiveObjectModerator moderator = context.mock(JiveObjectModerator.class);
	
	private long containerID = 1000L;
	private int containerType = 14;
	
	@Before
	public void doBefore() {
		action.setMemoManager(memoManager);
		action.setContainer(container);
		action.setAuthenticationProvider(authProvider);
		action.setTagActionUtil(tagActionUtil);
		action.setTagSetActionHelper(tagSetActionHelper);
		action.setJiveObjectModerator(moderator);
		
		context.checking(new Expectations() {{
			allowing(container).getID(); will(returnValue(containerID));
			allowing(container).getObjectType(); will(returnValue(containerType));
			allowing(authProvider).getJiveUser(); will(returnValue(user));
		}});
	}
	
	@Test
	public void execute() {
		final long memoId = 2000L;
		final Memo memo = context.mock(Memo.class, "memo");
		final Memo tempMemo = context.mock(Memo.class, "temp");
		final JiveIterator<Image> images = EmptyJiveIterator.getInstance();
		final String tagSets = "tagSets";
		
		action.setTitle("title");
		action.setDescription("description");
		action.setSession(new HashMap());
		action.getSession().put(action.SESSION_MEMO_KEY, tempMemo);
		action.setContentTagSets(tagSets);
		
		context.checking(new Expectations() {{
			allowing(memo).getID(); will(returnValue(memoId));
			allowing(memo).getStatus(); will(returnValue(JiveContentObject.Status.PUBLISHED));
			allowing(tempMemo).getImages(); will(returnValue(images));
			one(memoManager).saveMemo(with(any(MemoBean.class)), with(equal(images))); will(returnValue(memo));
			one(tagActionUtil).saveTags(with(equal(memo)), with(any(Set.class)));
			one(tagSetActionHelper).setTagSets(memo, tagSets);
			allowing(moderator).isModerationEnabled(with(equal(container)), with(any(Memo.class)), with(any(User.class))); will(returnValue(false));
		}});
		
		assertEquals(ActionSupport.SUCCESS, action.execute());
		assertSame(memo, action.getMemo());
		assertEquals(memoId, action.getMemoId());
	}
	
	@Test
	public void cancel() {
		assertEquals(JiveActionSupport.CANCEL, action.cancel());
	}
	
	class TestCreateMemoAction extends CreateMemoAction {
		public String getSessionKey() {
			return action.SESSION_MEMO_KEY;
		}
		
		protected void cleanSession() {
			
		}
	}
}
