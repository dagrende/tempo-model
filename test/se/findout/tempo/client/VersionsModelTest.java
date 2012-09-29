package se.findout.tempo.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
		assertEquals(1, model.getHeads().size());
		assertNull(model.getHeads().get(0).getBase());
		assertNull(model.getHeads().get(0).getChange());
	}

	@Test
	public void testAddVersion_fromEmptyNoBranch() {
		model.addVersion(model.getHeads().get(0), new Change());
		assertEquals(1, model.getHeads().size());
	}

	@Test
	public void testAddVersion_makeBranch() {
		Version initialVersion = model.getHeads().get(0);
		Version secondVersion = model.addVersion(initialVersion, new Change());
		Version thirdVersion = model.addVersion(initialVersion, new Change());
		assertEquals(2, model.getHeads().size());
		assertEquals(initialVersion, secondVersion.getBase());
		assertEquals(initialVersion, thirdVersion.getBase());
	}

}
