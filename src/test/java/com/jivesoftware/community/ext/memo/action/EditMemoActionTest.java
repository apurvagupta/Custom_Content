package com.jivesoftware.community.ext.memo.action;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.base.User;
import com.jivesoftware.community.Image;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.TagManager;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.impl.EmptyJiveIterator;
import com.jivesoftware.community.moderation.JiveObjectModerator;
import com.jivesoftware.community.objecttype.impl.TagSetEnabledActionHelper;
import com.jivesoftware.community.tags.TagActionUtil;
import com.opensymphony.xwork2.ActionSupport;

@RunWith(JMock.class)
public class EditMemoActionTest {
	private Mockery context = new JUnit4Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private EditMemoAction action = new TestEditMemoAction();
	
	private Memo memo = context.mock(Memo.class);
	private JiveContainer container = context.mock(JiveContainer.class);
	private MemoManager memoManager = context.mock(MemoManager.class);
	private TagManager tagManager = context.mock(TagManager.class);
	private TagActionUtil tagActionUtil = context.mock(TagActionUtil.class);
	private TagSetEnabledActionHelper tagSetActionHelper = context.mock(TagSetEnabledActionHelper.class);
	private JiveObjectModerator jiveObjectModerator = context.mock(JiveObjectModerator.class);
	
	private long memoID = 1000L;
	
	@Before
	public void doBefore() {
		action.setMemo(memo);
		action.setMemoManager(memoManager);
		action.setTagManager(tagManager);
		action.setTagActionUtil(tagActionUtil);
		action.setTagSetActionHelper(tagSetActionHelper);
		action.setJiveObjectModerator(jiveObjectModerator);
		
		context.checking(new Expectations() {{
			allowing(memo).getID(); will(returnValue(memoID));
			allowing(memo).getJiveContainer(); will(returnValue(container));
			allowing(memo).getStatus(); will(returnValue(JiveContentObject.Status.PUBLISHED));
			allowing(jiveObjectModerator).isModerationEnabled(with(equal(container)), with(any(Memo.class)), with(any(User.class))); will(returnValue(false));
		}});
	}
	
	@Test
	//Delete this method when you remove the @Ignore annotations from the input() and execute() methods
	public void test() {
	}
	
	@Ignore
	public void input() {
		final String title = "title";
		final String description = "description";
		//Add your values for your properties here
		final String tags = "tags";
		
		context.checking(new Expectations() {{
			one(memo).getTitle(); will(returnValue(title));
			one(memo).getDescription(); will(returnValue(description));
			//Add expectations for your properties here
			one(tagManager).getTagsAsString(memo); will(returnValue(tags));
		}});
		
		assertEquals(ActionSupport.INPUT, action.input());
		assertEquals(title, action.getTitle());
		assertEquals(description, action.getDescription());
		//Add assertions for your properties here
		assertEquals(tags, action.getTags());
	}
	
	@Ignore
	public void execute() {
		final Memo tempMemo = context.mock(Memo.class, "temp");
		final JiveIterator<Image> images = EmptyJiveIterator.getInstance();
		final String tagSets = "tagSets";
		
		action.setTitle("title");
		action.setDescription("description");
		//Add setter invocations for your properties here
		action.setSession(new HashMap());
		action.getSession().put(action.SESSION_MEMO_KEY, tempMemo);
		action.setContentTagSets(tagSets);
		
		context.checking(new Expectations() {{
			one(memo).setTitle(action.getTitle());
			one(memo).setDescription(action.getDescription());
			//Add expectations for your properties here
			one(memo).setModificationDate(with(any(Date.class)));
			one(tempMemo).getImages(); will(returnValue(images));
			one(memoManager).updateMemo(memo, images);
			one(tagActionUtil).saveTags(with(equal(memo)), with(any(Set.class)));
			one(tagSetActionHelper).setTagSets(memo, tagSets);
		}});
		
		assertEquals(ActionSupport.SUCCESS, action.execute());
	}
	
	class TestEditMemoAction extends EditMemoAction {
		public String getSessionKey() {
			return action.SESSION_MEMO_KEY;
		}
		
		public void cleanSession() {
			
		}
	}
}
