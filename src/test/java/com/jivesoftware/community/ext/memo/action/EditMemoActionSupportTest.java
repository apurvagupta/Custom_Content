package com.jivesoftware.community.ext.memo.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoBean;
import com.jivesoftware.community.ext.memo.impl.MemoConverter;
import com.jivesoftware.community.moderation.JiveObjectModerator;
import com.jivesoftware.community.objecttype.impl.TagSetEnabledActionHelper;
import com.jivesoftware.community.tags.TagActionUtil;
import com.jivesoftware.community.validation.InvalidPropertyException;

@RunWith(JMock.class)
public class EditMemoActionSupportTest {
	private Mockery context = new JUnit4Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private EditMemoActionSupport action = new TestEditMemoActionSupport();
	
	private JiveContainer container = context.mock(JiveContainer.class);
	
	private AuthenticationProvider authProvider = context.mock(AuthenticationProvider.class);
	private TagActionUtil tagActionUtil = context.mock(TagActionUtil.class);
	private TagSetEnabledActionHelper tagSetActionHelper = context.mock(TagSetEnabledActionHelper.class);
	private MemoConverter memoConverter = context.mock(MemoConverter.class);
	private Memo memo = context.mock(Memo.class);
	private Memo tempMemo = context.mock(Memo.class, "temp");
	private User user = new UserTemplate();
	private JiveObjectModerator jiveObjectModerator = context.mock(JiveObjectModerator.class);
	
	private long memoId = 1000L;
	private long tempMemoId = -1L;
	private long containerId = 2000L;
	private int containerType = 14;
	
	@Before
	public void doBefore() {
		action.setTagActionUtil(tagActionUtil);
		action.setTagSetActionHelper(tagSetActionHelper);
		action.setMemoConverter(memoConverter);
		action.setAuthenticationProvider(authProvider);
		action.setJiveObjectModerator(jiveObjectModerator);
		action.setSession(new HashMap());

		context.checking(new Expectations() {{
			allowing(container).getID(); will(returnValue(containerId));
			allowing(container).getObjectType(); will(returnValue(containerType));
			allowing(authProvider).getJiveUser(); will(returnValue(user));
			allowing(memoConverter).convert(with(any(MemoBean.class))); will(returnValue(tempMemo));
			allowing(memo).getID(); will(returnValue(memoId));
			allowing(tempMemo).getID(); will(returnValue(tempMemoId));
		}});
	}
	
	@Test
	public void validateNoTags() {
		action.setTags("");
		action.validate();
		
		assertNotNull(action.getFieldErrors());
		assertTrue(action.getFieldErrors().isEmpty());
	}
	
	@Test
	public void validateInvalidPropertyExceptionLength() {
		action.setTags("tags");
		
		final InvalidPropertyException ipe = new InvalidPropertyException(new Object(), InvalidPropertyException.Error.length);
		
		context.checking(new Expectations() {{
			one(tagActionUtil).getValidTags(action.getTags()); will(throwException(ipe));
		}});
		
		action.validate();
		
		assertNotNull(action.getFieldErrors());
		assertEquals(1, action.getFieldErrors().size());
		assertNotNull(action.getFieldErrors().get("tags"));
	}
	
	@Test
	public void validateInvalidPropertyExceptionOther() {
		action.setTags("tags");
		
		final InvalidPropertyException ipe = new InvalidPropertyException(new Object(), InvalidPropertyException.Error.characters);
		
		context.checking(new Expectations() {{
			one(tagActionUtil).getValidTags(action.getTags()); will(throwException(ipe));
		}});
		
		action.validate();
		
		assertNotNull(action.getFieldErrors());
		assertEquals(1, action.getFieldErrors().size());
		assertNotNull(action.getFieldErrors().get("tags"));
	}
	
	@Test
	public void validate() {
		final String tags = "tags";
		final Set<String> validatedTags = new HashSet<String>();
		action.setTags(tags);
		
		context.checking(new Expectations() {{
			one(tagActionUtil).getValidTags(tags); will(returnValue(validatedTags));
		}});
		
		action.validate();
		
		assertNotNull(action.getFieldErrors());
		assertTrue(action.getFieldErrors().isEmpty());
	}
	
	@Test
	public void doImagePickerNoMemoSession() {		
		action.setMemo(null);
		action.getSession().put(action.SESSION_MEMO_KEY, memo);
		
		assertEquals("image-picker", action.doImagePicker());
		assertEquals(memo, action.getMemo());
		assertEquals(memo, action.getSession().get(action.SESSION_MEMO_KEY));
	}
	
	@Test
	public void doImagePickerNoMemoTemp() {
		action.setMemo(null);
	
		assertEquals("image-picker", action.doImagePicker());
		assertEquals(tempMemo, action.getMemo());
		assertEquals(tempMemo, action.getSession().get(action.SESSION_MEMO_KEY));
	}
	
	@Test 
	public void doImagePicker() {
		action.setMemo(memo);
		
		assertEquals("image-picker", action.doImagePicker());
		assertEquals(memo, action.getMemo());
		assertEquals(memo, action.getSession().get(action.SESSION_MEMO_KEY + memoId));
	}
	
	@Test
	public void getSessionKeyNoMemo() {
		action.setMemo(null);
		
		assertEquals(action.SESSION_MEMO_KEY, action.getSessionKey());
		assertEquals(tempMemo, action.getMemo());
	}
	
	@Test
	public void getSessionKey() {
		action.setMemo(memo);
		
		assertEquals(action.SESSION_MEMO_KEY + memoId, action.getSessionKey());
	}

	@Test
	public void getTemporaryMemoMemoPopulatedAndInSession() throws Exception {
		action.setMemo(memo);
		action.getSession().put(action.SESSION_MEMO_KEY + memoId, memo);
		
		assertSame(memo, action.getTemporaryMemo());
	}
	
	@Test
	public void getTemporaryMemoMemoPopulateButNotInSession() throws Exception {
		action.setMemo(memo);
		
		assertEquals(tempMemo, action.getTemporaryMemo());
	}
	
	@Test
	public void getTemporaryMemoMemoNotPopulated() throws Exception {
		action.setMemo(null);
		
		assertEquals(tempMemo, action.getTemporaryMemo());
	}
	
	@Test
	public void getPopularTags() {
		final List<String> popularTags = new ArrayList<String>();
		context.checking(new Expectations() {{
			one(tagActionUtil).getPopularTags(with(equal(container)), with(any(Integer.class))); will(returnValue(popularTags));
		}});
		
		assertSame(popularTags, action.getPopularTags());
		
		//execute again to make sure the previous call resulted in tags being stored in member variable
		assertSame(popularTags, action.getPopularTags());
	}
	
	@Test
	public void getObjectTagSetIDs() {
		final List<Long> ids = new ArrayList<Long>();
		
		context.checking(new Expectations() {{
			one(tagSetActionHelper).getObjectTagSetIDs(memo); will(returnValue(ids));
		}});
		
		assertSame(ids, action.getObjectTagSetIDs(memo));
	}
	
	@Test
	public void isMemoModeratedModerationOffAndNoMemo() {
		context.checking(new Expectations() {{
			one(jiveObjectModerator).isModerationEnabled(with(equal(container)), with(any(Memo.class)), with(any(User.class))); will(returnValue(false));
		}});
		
		assertFalse(action.isMemoModerated());		
	}
	
	@Test
	public void isMemoModeratedModerationOffAndMemoPublished() {
		action.setMemo(memo);
		
		context.checking(new Expectations() {{
			one(jiveObjectModerator).isModerationEnabled(with(equal(container)), with(any(Memo.class)), with(any(User.class))); will(returnValue(false));
			one(memo).getStatus(); will(returnValue(JiveContentObject.Status.PUBLISHED));
		}});
		
		assertFalse(action.isMemoModerated());		
	}
	
	@Test
	public void isMemoModeratedModerationOffAndMemoAlreadyInModeration() {
		action.setMemo(memo);
		
		context.checking(new Expectations() {{
			one(jiveObjectModerator).isModerationEnabled(with(equal(container)), with(any(Memo.class)), with(any(User.class))); will(returnValue(false));
			one(memo).getStatus(); will(returnValue(JiveContentObject.Status.AWAITING_MODERATION));
		}});
		
		assertTrue(action.isMemoModerated());
	}
	
	@Test
	public void isMemoModeratedModerationOnAndNoMemo() {
		context.checking(new Expectations() {{
			one(jiveObjectModerator).isModerationEnabled(with(equal(container)), with(any(Memo.class)), with(any(User.class))); will(returnValue(true));
		}});
		
		assertTrue(action.isMemoModerated());		
	}
	
	@Test
	public void isMemoModeratedModerationOnAndMemoPublished() {
		action.setMemo(memo);
		
		context.checking(new Expectations() {{
			one(jiveObjectModerator).isModerationEnabled(with(equal(container)), with(any(Memo.class)), with(any(User.class))); will(returnValue(true));
			one(memo).getStatus(); will(returnValue(JiveContentObject.Status.PUBLISHED));
		}});
		
		assertTrue(action.isMemoModerated());				
	}
	
	@Test
	public void isMemoModeratedModerationOnAndMemoAlreadyInModeration() {
		action.setMemo(memo);
		
		context.checking(new Expectations() {{
			one(jiveObjectModerator).isModerationEnabled(with(equal(container)), with(any(Memo.class)), with(any(User.class))); will(returnValue(true));
			one(memo).getStatus(); will(returnValue(JiveContentObject.Status.AWAITING_MODERATION));
		}});
		
		assertTrue(action.isMemoModerated());		
	}
	
	class TestEditMemoActionSupport extends EditMemoActionSupport {

		@Override
		public JiveContainer getContainer() {
			return container;
		}

		@Override
		public boolean isEdit() {
			return false;
		}
		
		@Override
		public String getText(String key, String[] args) {
			return key;
		}
		
	}
}
