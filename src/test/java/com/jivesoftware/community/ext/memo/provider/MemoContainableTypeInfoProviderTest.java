package com.jivesoftware.community.ext.memo.provider;

import static org.junit.Assert.*;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.base.User;
import com.jivesoftware.community.JiveConstants;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.JiveObjectLoader;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.aaa.authz.EntitlementTypeProvider.EntitlementType;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.objecttype.ContentObjectType;
import com.jivesoftware.community.objecttype.EntitlementCheckProvider;

@RunWith(JMock.class)
public class MemoContainableTypeInfoProviderTest {
	private Mockery context = new JUnit4Mockery();
	
	private MemoContainableTypeInfoProvider provider = new MemoContainableTypeInfoProvider();
	private JiveObjectLoader jiveObjectLoader = context.mock(JiveObjectLoader.class);
	private EntitlementCheckProvider<Memo> entitlementCheckProvider = context.mock(EntitlementCheckProvider.class);
	
	private Memo memo = context.mock(Memo.class);
	private JiveContainer container = context.mock(JiveContainer.class);
	
	private long containerID = 1000L;
	private int containerType = 14;
	
	@Before
	public void doBefore() {
		provider.setJiveObjectLoader(jiveObjectLoader);
		provider.setEntitlementCheckProvider(entitlementCheckProvider);
		
		context.checking(new Expectations() {{
			allowing(memo).getJiveContainer(); will(returnValue(container));
			allowing(memo).getContainerType(); will(returnValue(containerType));
			allowing(memo).getContainerID(); will(returnValue(containerID));
			allowing(container).getID(); will(returnValue(containerID));
			allowing(container).getObjectType(); will(returnValue(containerType));
		}});
	}
	
	@Test
	public void getContainerFor() throws Exception {
		context.checking(new Expectations() {{
			one(jiveObjectLoader).getJiveObject(containerType, containerID); will(returnValue(container));
		}});
		
		assertSame(container, provider.getContainerFor(memo));
	}

	@Test
	public void getContainerForNotFound() throws Exception {
		context.checking(new Expectations() {{
			one(jiveObjectLoader).getJiveObject(containerType, containerID); will(throwException(new NotFoundException()));
		}});
		
		assertNull(provider.getContainerFor(memo));
	}
	
	@Test
	public void getContainerForJiveObject() {
		final JiveObject jiveObject = context.mock(JiveObject.class);
		assertNull(provider.getContainerFor(jiveObject));
	}
	
	@Test
	public void getSubContentTypes() {
		List<ContentObjectType> types = provider.getSubContentTypes();
		
		//always an empty list
		assertNotNull(types);
		assertTrue(types.isEmpty());
	}
	
	@Test
	public void getUserPersonalContainerForContentType() {
		//always null
		assertNull(provider.getUserPersonalContainerForContentType(null));
	}
	
	@Test
	public void isAvailableForContainer() {
		assertTrue(provider.isAvailableForContainer(JiveConstants.COMMUNITY));
		assertTrue(provider.isAvailableForContainer(JiveConstants.PROJECT));
		assertTrue(provider.isAvailableForContainer(JiveConstants.SOCIAL_GROUP));
		assertTrue(provider.isAvailableForContainer(JiveConstants.USER_CONTAINER));
		assertFalse(provider.isAvailableForContainer(JiveConstants.DOCUMENT));
	}
	
	@Test
	public void isEnabledByDefaultForContainer() {
		//always true
		assertTrue(provider.isEnabledByDefaultForContainer(0));
	}
	
	@Test
	public void isRequiredForContainer() {
		//always false
		assertFalse(provider.isRequiredForContainer(0));
	}
	
	@Test
	public void userHasCreatePermsFor() {
		final boolean entitled = true;
		final User user = context.mock(User.class);
		
		context.checking(new Expectations() {{
			one(entitlementCheckProvider).isUserEntitled(container, EntitlementType.CREATE); will(returnValue(entitled));
		}});
		
		assertEquals(entitled, provider.userHasCreatePermsFor(container, user));
	}
}
