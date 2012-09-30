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
		Version initialVersion = model.getHeads().get(0);
		Version secondVersion = model.addVersion(initialVersion, new Change());
		Version thirdVersion = model.addVersion(initialVersion, new Change());
		Assert.assertEquals(2, model.getHeads().size());
		Assert.assertEquals(initialVersion, secondVersion.getBase());
		Assert.assertEquals(initialVersion, thirdVersion.getBase());
		
		Assert.assertEquals("1", initialVersion.getName());
		Assert.assertEquals("2", secondVersion.getName());
		Assert.assertEquals("1.1", thirdVersion.getName());
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
