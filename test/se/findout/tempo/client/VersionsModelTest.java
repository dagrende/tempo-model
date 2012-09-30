package se.findout.tempo.client;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

public class VersionsModelTest {
	private VersionsModel model;

	@Before
	public void setup() {
		model = new VersionsModel();
	}
	
	@Test
	public void testInitialState_oneNullHead() {
		Assert.assertEquals(1, model.getHeads().size());
		Assert.assertNull(model.getHeads().get(0).getBase());
		Assert.assertNull(model.getHeads().get(0).getChange());
	}

	@Test
	public void testAddVersion_fromEmptyNoBranch() {
		model.addVersion(model.getHeads().get(0), new Change());
		Assert.assertEquals(1, model.getHeads().size());
	}

	@Test
	public void testAddVersion_makeBranch() {
		Version v1 = model.getHeads().get(0);
		Version v2 = model.addVersion(v1, new Change());
		Version v11 = model.addVersion(v1, new Change());
		Version v12 = model.addVersion(v11, new Change());
		Assert.assertEquals(2, model.getHeads().size());
		Assert.assertEquals(v1, v2.getBase());
		Assert.assertEquals(v1, v11.getBase());
		
		Assert.assertEquals("1", v1.getName());
		Assert.assertEquals("2", v2.getName());
		Assert.assertEquals("1.1", v11.getName());
	}
	
	@Test
	public void testGetInitialVersion() {
		Version initialVersion = model.getInitialVersion();
		Assert.assertNotNull(initialVersion);
		Assert.assertNull(initialVersion.getBase());
		Assert.assertNull(initialVersion.getChange());
	}	

	@Test
	public void testIncVersion() {
		Assert.assertEquals("2", model.incVersion("1"));
		Assert.assertEquals("1.2", model.incVersion("1.1"));
		
	}
}
