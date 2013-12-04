package com.jivesoftware.community.ext.memo.provider;

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

import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.aaa.authz.EntitlementTypeProvider.EntitlementType;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.jivesoftware.community.objecttype.EntitlementCheckProvider;

@RunWith(JMock.class)
public class MemoContentObjectTypeInfoProviderTest {
	private Mockery context = new JUnit4Mockery();
	
	private MemoContentObjectTypeInfoProvider provider = new MemoContentObjectTypeInfoProvider();
	
	private BeanProvider<MemoObjectType> memoObjectTypeProvider = context.mock(BeanProvider.class);
	private EntitlementCheckProvider<Memo> entitlementCheckProvider = context.mock(EntitlementCheckProvider.class);
	
	@Before
	public void doBefore() {
		provider.setMemoObjectTypeProvider(memoObjectTypeProvider);
		provider.setEntitlementCheckProvider(entitlementCheckProvider);
	}
	
	@Test
	public void getContainableType() {
		final MemoObjectType type = new MemoObjectType();
		
		context.checking(new Expectations() {{
			one(memoObjectTypeProvider).get(); will(returnValue(type));
		}});
		
		assertSame(type, provider.getContainableType());
	}
	
	@Test
	public void getCreateNewFormRelativeURL() {
		final JiveContainer container = context.mock(JiveContainer.class);
		final long containerID = 1000L;
		final int containerType = 14;
		
		context.checking(new Expectations() {{
			allowing(container).getID(); will(returnValue(containerID));
			allowing(container).getObjectType(); will(returnValue(containerType));
		}});
		
		String url = provider.getCreateNewFormRelativeURL(container, false, "", "", "");
		
		assertNotNull(url);
		assertTrue(url.contains(String.valueOf(containerID)));
		assertTrue(url.contains(String.valueOf(containerType)));
	}
	
	@Test
	public void isBinaryBodyUploadCapable() {
		//always false
		assertFalse(provider.isBinaryBodyUploadCapable());
	}
	
	@Test
	public void userHasCreatePermsFor() {
		final JiveContainer yesContainer = context.mock(JiveContainer.class, "yes");
		final JiveContainer noContainer = context.mock(JiveContainer.class, "no");
		
		context.checking(new Expectations() {{
			one(entitlementCheckProvider).isUserEntitled(yesContainer, EntitlementType.CREATE); will(returnValue(true));
			one(entitlementCheckProvider).isUserEntitled(noContainer, EntitlementType.CREATE); will(returnValue(false));
		}});
		
		assertTrue(provider.userHasCreatePermsFor(yesContainer));
		assertFalse(provider.userHasCreatePermsFor(noContainer));
	}
}
