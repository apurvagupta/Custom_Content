package com.jivesoftware.community.ext.memo.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Date;

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
import com.jivesoftware.community.ImageException;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.event.ImageEvent;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.ext.memo.impl.MemoConverter.BodyProvider;
import com.jivesoftware.community.impl.DbImage;
import com.jivesoftware.community.impl.DbImageManager;
import com.jivesoftware.community.objecttype.JiveObjectType;
import com.jivesoftware.community.proxy.ImageProxy;
import com.jivesoftware.util.LongList;
import com.jivesoftware.util.SimpleDataSource;

@RunWith(JMock.class)
public class MemoImplTest {
	private Mockery context = new JUnit4Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private MemoImpl impl;
	
	private long id = 1000L;
	private User user = context.mock(User.class);
	private JiveContainer container = context.mock(JiveContainer.class);
	private MemoObjectType objectType = new MemoObjectType();
	private ViewCountProvider viewCountProvider = context.mock(ViewCountProvider.class);
	private BodyProvider bodyProvider = context.mock(BodyProvider.class);
	private LongList images = new LongList();
	
	private String title = "title";
	private String description = "description";
	private Date creationDate = new Date();
	private Date modificationDate = new Date();
	private Status status = JiveContentObject.Status.PUBLISHED; 
	
	private DbImageManager imageManager = context.mock(DbImageManager.class);
	
	@Before
	public void doBefore() {
		images.add(2000L);
		images.add(2001L);
	}
	
	@Test
	public void getSetEverything() {
		impl = new MemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		impl.setTitle(title);
		impl.setDescription(description);
		impl.setCreationDate(creationDate);
		impl.setModificationDate(modificationDate);
		impl.setStatus(status);
		
		assertEquals(id, impl.getID());
		assertEquals(container, impl.getJiveContainer());
		assertEquals(user, impl.getUser());
		assertEquals(status, impl.getStatus());
		assertEquals(creationDate, impl.getCreationDate());
		assertEquals(modificationDate, impl.getModificationDate());
		assertEquals(title, impl.getTitle());
		assertEquals(description, impl.getDescription());
	}
	
	@Test(expected=IllegalStateException.class)
	public void addImageWithContentObject() throws Exception {
		final Image image = context.mock(Image.class);
		final JiveContentObject object = context.mock(JiveContentObject.class);
		
		context.checking(new Expectations() {{
			allowing(image).getJiveContentObject(); will(returnValue(object));
			allowing(image).getID(); will(returnValue(2000L));
			allowing(object).getID(); will(returnValue(3000L));
			allowing(object).getObjectType(); will(returnValue(45));
		}});
		
		impl = new MemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		impl.addImage(image);
	}
	
	@Test(expected=ImageException.class)
	public void addImageTooManyImages() throws Exception {
		final Image image = context.mock(Image.class);
		
		context.checking(new Expectations() {{
			one(image).getJiveContentObject(); will(returnValue(null));
			one(imageManager).getMaxImagesPerObject(); will(returnValue(images.size() - 1));
		}});
		
		impl = new TestMemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		impl.addImage(image);
		
	}
	
	@Test
	public void addImage() throws Exception {
		final Image image = context.mock(Image.class);
		final long imageID = 500L;
		
		context.checking(new Expectations() {{
			one(image).getJiveContentObject(); will(returnValue(null));
			one(image).getID(); will(returnValue(imageID));
			one(imageManager).getMaxImagesPerObject(); will(returnValue(images.size() + 1));
		}});
		
		impl = new TestMemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		impl.addImage(image);
		
		assertTrue(images.contains(imageID));
	}
	
	@Test(expected=ImageException.class)
	public void createImageTooManyImages() throws Exception {
		final InputStream inputStream = context.mock(InputStream.class);
		
		context.checking(new Expectations() {{
			one(imageManager).getMaxImagesPerObject(); will(returnValue(images.size() - 1));
		}});
		
		impl = new TestMemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		impl.createImage("name", "contentType", inputStream);
	}
	
	@Test
	public void createImage() throws Exception {
		final InputStream inputStream = context.mock(InputStream.class);
		final Image image = context.mock(Image.class);
		final long imageID = 34L;

		impl = new TestMemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		
		context.checking(new Expectations() {{
			one(imageManager).getMaxImagesPerObject(); will(returnValue(images.size() + 1));
			one(imageManager).createImage(with(equal(impl)), with(any(SimpleDataSource.class))); will(returnValue(image));
			allowing(image).getID(); will(returnValue(imageID));
		}});
		
		assertSame(image, impl.createImage("name", "contentType", inputStream));
		assertTrue(images.contains(imageID));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void deleteImageNotContained() throws Exception {
		final Image image = context.mock(Image.class);
		final long imageID = 500L;
		
		context.checking(new Expectations() {{
			allowing(image).getID(); will(returnValue(imageID));
		}});
		
		impl = new TestMemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		impl.deleteImage(image);
	}
	
	@Test(expected=ImageException.class)
	public void deleteImageErrorGettingImage() throws Exception {
		final Image image = context.mock(Image.class);
		final long imageID = images.get(0);

		context.checking(new Expectations() {{
			allowing(image).getID(); will(returnValue(imageID));
			one(imageManager).getImage(imageID); will(throwException(new NotFoundException()));
		}});
		
		impl = new TestMemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		impl.deleteImage(image);
	}
	
	//Can't mock call to DbImage.delete(), since it's protected.  This should be fixed in DM
	@Ignore
	public void deleteImagePassingProxy() throws Exception {
		final ImageProxy imageProxy = context.mock(ImageProxy.class);
		final DbImage image = context.mock(DbImage.class);
		final long imageID = images.get(0);
		
		context.checking(new Expectations() {{
			allowing(imageProxy).getID(); will(returnValue(imageID));
			allowing(image).getID(); will(returnValue(imageID));
			one(imageManager).getImage(imageID); will(returnValue(image));
			//one(any(DbImage.class)).method("delete");
			
			
			
		}});
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getImageAlreadyContained() throws Exception {
		final DbImage image = context.mock(DbImage.class);
		final long imageID = 5000L;
		
		context.checking(new Expectations() {{
			one(imageManager).getImage(imageID); will(returnValue(image));
		}});
		
		impl = new TestMemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		impl.getImage(imageID);
	}
	
	@Test(expected=ImageException.class)
	public void getImageNullImageReturned() throws Exception {
		final long imageID = images.get(0);
		
		context.checking(new Expectations() {{
			one(imageManager).getImage(imageID); will(returnValue(null));
		}});
		
		impl = new TestMemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		impl.getImage(imageID);		
	}
	
	@Test(expected=ImageException.class)
	public void getImageNotFound() throws Exception {
		final DbImage image = context.mock(DbImage.class);
		final long imageID = images.get(0);
		
		context.checking(new Expectations() {{
			allowing(image).getID(); will(returnValue(imageID));
			one(imageManager).getImage(imageID); will(throwException(new NotFoundException()));
		}});
		
		impl = new TestMemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		impl.getImage(imageID);
	}
	
	@Test
	public void getImage() throws Exception {
		final DbImage image = context.mock(DbImage.class);
		final long imageID = images.get(0);
		
		context.checking(new Expectations() {{
			allowing(image).getID(); will(returnValue(imageID));
			one(imageManager).getImage(imageID); will(returnValue(image));
		}});
		
		impl = new TestMemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		assertSame(image, impl.getImage(imageID));
	}
	
	@Test
	public void getImageCount() {
		impl = new TestMemoImpl(id, user, container, objectType, viewCountProvider, bodyProvider, images);
		assertEquals(images.size(), impl.getImageCount());
	}
	
	class TestMemoImpl extends MemoImpl {

		public TestMemoImpl() {
			super();
		}

		public TestMemoImpl(long id, User user, JiveContainer container, JiveObjectType objectType, ViewCountProvider viewCountProvider, BodyProvider body, LongList images) {
			super(id, user, container, objectType, viewCountProvider, body, images);
		}
		
		protected DbImageManager getImageManager() {
			return imageManager;
		}
		
		protected void fireImageEvent(Image image, ImageEvent.Type type) {
			//do nothing
		}
	}
}
