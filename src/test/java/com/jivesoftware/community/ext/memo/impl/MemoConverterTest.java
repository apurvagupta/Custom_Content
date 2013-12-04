package com.jivesoftware.community.ext.memo.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.jivesoftware.base.User;
import com.jivesoftware.base.UserManager;
import com.jivesoftware.base.UserNotFoundException;
import com.jivesoftware.community.EntityDescriptor;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContainerManager;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoBean;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.impl.MemoImageHelper;
import com.jivesoftware.community.impl.RenderCacheManagerImpl;
import com.jivesoftware.community.lifecycle.spring.BeanProvider;
import com.jivesoftware.util.LongList;

@RunWith(JMock.class)
public class MemoConverterTest {
	private Mockery context = new JUnit4Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private MemoConverter converter = new MemoConverter();
	
	private BeanProvider<MemoObjectType> objectTypeProvider = context.mock(BeanProvider.class);
	private UserManager userManager = context.mock(UserManager.class);
	private JiveContainerManager containerManager = context.mock(JiveContainerManager.class);
	private RenderCacheManagerImpl renderCacheManager = context.mock(RenderCacheManagerImpl.class);
	private ViewCountProvider viewCountProvider = context.mock(ViewCountProvider.class);
	private MemoImageHelper imageHelper = context.mock(MemoImageHelper.class);

	private MemoObjectType memoObjectType = new MemoObjectType();
	private User user = context.mock(User.class);
	private JiveContainer container = context.mock(JiveContainer.class);
	
	private long id = 500L;
	private long userID = 1000L;
	private long invalidUserID = 1001L;
	private int containerType = 600;
	private long containerID = 2000L;
	private long invalidContainerID = 2001L;
	private String title = "title";
	private String description = "description";
	private Date creationDate = new Date();
	private Date modificationDate = new Date();
	private int statusID = JiveContentObject.Status.PUBLISHED.intValue();
	private Document body = null;
	private LongList imageIDs = new LongList();
	private int viewCount = 346;
	
	@Before
	public void doBefore() throws Exception {
		converter.setObjectTypeProvider(objectTypeProvider);
		converter.setUserManager(userManager);
		converter.setContainerManager(containerManager);
		converter.setRenderCacheManager(renderCacheManager);
		converter.setViewCountProvider(viewCountProvider);
		converter.setImageHelper(imageHelper);
		
		context.checking(new Expectations() {{
			allowing(objectTypeProvider).get(); will(returnValue(memoObjectType));
		}});
		
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        body = docBuilder.newDocument();
        Element root = body.createElement("body");
        body.appendChild(root);
        root.appendChild(body.createTextNode(description));
        
        imageIDs.add(20L);
        imageIDs.add(30L);
	}
	
	@Test
	public void convertNullBean() {
		assertNull(converter.convert(null));
	}
	
	@Test
	public void convertUserNotFound() throws Exception {
		MemoBean bean = new MemoBean();
		bean.setUserID(invalidUserID);
		
		context.checking(new Expectations() {{
			one(userManager).getUser(invalidUserID); will(throwException(new UserNotFoundException()));
		}});
		
		assertNull(converter.convert(bean));
	}
	
	@Test
	public void convertContainerNotFound() throws Exception {
		MemoBean bean = createBean();
		bean.setContainerID(invalidContainerID);
		
		context.checking(new Expectations() {{
			one(containerManager).getJiveContainer(containerType, invalidContainerID); will(throwException(new NotFoundException()));
		}});
		
		assertNull(converter.convert(bean));
	}
	
	@Test
	public void convertNoDescription() throws Exception {
		MemoBean bean = createBean();
		bean.setDescription(null);

		Memo memo = converter.convert(bean);
		assertConversion(memo);
		assertNull(memo.getBody());
	}
	
	@Test
	public void convert() throws Exception {
		MemoBean bean = createBean();
		
		Memo memo = converter.convert(bean);
		assertConversion(memo);
		assertNotNull(memo.getBody());
		
		assertTrue(memo.getBody().getFirstChild().getTextContent().contains(description));
	}
	
	protected MemoBean createBean() throws Exception {
		MemoBean bean = new MemoBean();
		bean.setID(id);
		bean.setUserID(userID);
		bean.setContainerType(containerType);
		bean.setContainerID(containerID);
		bean.setDescription(description);
		bean.setTitle(title);
		bean.setCreationDate(creationDate);
		bean.setModificationDate(modificationDate);
		bean.setStatusID(statusID);
		
		context.checking(new Expectations() {{
			allowing(userManager).getUser(userID); will(returnValue(user));
			allowing(containerManager).getJiveContainer(containerType, containerID); will(returnValue(container));
			allowing(renderCacheManager).retrieveXmlDocument(with(any(EntityDescriptor.class)), with(equal(description))); will(returnValue(body));
			allowing(imageHelper).getImages(id, MemoObjectType.MEMO_TYPE_ID); will(returnValue(imageIDs));
			allowing(viewCountProvider).getViewCount(with(any(Memo.class))); will(returnValue(viewCount));
		}});
		
		return bean;
	}
	
	protected void assertConversion(Memo memo) {
		assertNotNull(memo);
		assertEquals(id, memo.getID());
		assertSame(user, memo.getUser());
		assertSame(container, memo.getJiveContainer());
		assertSame(memoObjectType, memo.getJiveObjectType());
		assertEquals(imageIDs.size(), memo.getImageCount());
		assertEquals(title, memo.getTitle());
		assertSame(creationDate, memo.getCreationDate());
		assertSame(modificationDate, memo.getModificationDate());
		assertEquals(statusID, memo.getStatus().intValue());
	}
}
