package com.jivesoftware.community.ext.memo.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.base.User;
import com.jivesoftware.community.Comment;
import com.jivesoftware.community.CommentContentResource;
import com.jivesoftware.community.EntityDescriptor;
import com.jivesoftware.community.InterceptorManager;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.ext.memo.Memo;

@RunWith(JMock.class)
public class MemoCommentableTypeInfoProviderTest {
	private Mockery context = new JUnit4Mockery();
	
	private MemoCommentableTypeInfoProvider provider = new MemoCommentableTypeInfoProvider();
	
	private Memo target = context.mock(Memo.class);
	private JiveContainer container = context.mock(JiveContainer.class);
	private User user = context.mock(User.class);
	private Comment comment = context.mock(Comment.class);
	
	private long breakID = 1000L;
	private long containerID = 2000L;
	private int containerType = 14;
	
	@Before
	public void doBefore() {
		context.checking(new Expectations() {{
			allowing(target).getID(); will(returnValue(breakID));
			allowing(target).getUser(); will(returnValue(user));
			allowing(target).getJiveContainer(); will(returnValue(container));
			allowing(container).getID(); will(returnValue(containerID));
			allowing(container).getObjectType(); will(returnValue(containerType));
			allowing(comment).getCommentContentResource(); will(returnValue(target));
		}});
	}
	
	@Test
	public void getCommentStatus() throws Exception {
		final int status = 5;
		
		context.checking(new Expectations() {{
			one(target).getCommentStatus(); will(returnValue(status));
		}});
		
		assertEquals(status, provider.getCommentStatus(target));
	}
	
	@Test
	public void getInterceptorManager() throws Exception {
		final InterceptorManager interceptorManager = context.mock(InterceptorManager.class);
		
		context.checking(new Expectations() {{
			one(container).getInterceptorManager(); will(returnValue(interceptorManager));
		}});
		
		assertSame(interceptorManager, provider.getInterceptorManager(target));
	}
	
	@Test
	public void getParentObject() throws Exception {
		EntityDescriptor descriptor = provider.getParentObject(target);
		
		assertNotNull(descriptor);
		assertEquals(containerID, descriptor.getID());
		assertEquals(containerType, descriptor.getObjectType());
	}
	
	@Test
	public void isAuthorizedInBackChannel() {
		//always false
		assertFalse(provider.isAuthorizedInBackChannel(target, user));
	}
	
	@Test
	public void isCommentAttachedToCommentTarget() {
		long id1 = 1000L;
		long id2 = 2000L;
		int type = 14;
		EntityDescriptor commentTarget = new EntityDescriptor(type, id1);
		
		assertTrue(provider.isCommentAttachedToCommentTarget(commentTarget, new EntityDescriptor(type, id1)));
		assertFalse(provider.isCommentAttachedToCommentTarget(commentTarget, new EntityDescriptor(type, id2)));
	}
	
	@Test
	public void isCommentsEnabled() {
		//always true
		assertTrue(provider.isCommentsEnabled());
	}
	
	@Test
	public void isDeletableTrue() {
		final Comment orphanComment = context.mock(Comment.class, "orphan");
		final Comment anotherTargetComment = context.mock(Comment.class, "another");
		final CommentContentResource anotherTarget = context.mock(CommentContentResource.class);
		final long anotherID = 9000L;
		
		context.checking(new Expectations() {{
			allowing(orphanComment).getCommentContentResource(); will(returnValue(null));
			allowing(anotherTargetComment).getCommentContentResource(); will(returnValue(anotherTarget));
			allowing(anotherTarget).getID(); will(returnValue(anotherID));
		}});
		
		assertTrue(provider.isDeleteable(target, comment));
		assertFalse(provider.isDeleteable(target, orphanComment));
		assertFalse(provider.isDeleteable(target, anotherTargetComment));
	}
	
	@Test
	public void isInDraftState() {
		//always false
		assertFalse(provider.isInDraftState(target));
	}
	
	@Test
	public void validateOptions() {
		//always true
		assertTrue(provider.validateCommentOptions(target, comment, false));
	}
}
