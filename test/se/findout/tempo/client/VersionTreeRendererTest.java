package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class VersionTreeRendererTest {
	private VersionModel model;
	private VersionTreeRenderer versionTreeRenderer;
	private List<VersionNode> nodes;

	@Before
	public void setup() {
		model = new VersionModel();
		versionTreeRenderer = new VersionTreeRenderer(model, new VersionTreeRenderer.ShapeFactory() {
			@Override
			public void addVersion(Version version, int x, int y) {
				nodes.add(new VersionNode(version, x, y));
			}

			@Override
			public void addRelation(int x0, int y0, int x1, int y1) {
			}
		});
		versionTreeRenderer.setxScale(1);
		versionTreeRenderer.setyScale(1);
		
		nodes = new ArrayList<VersionTreeRendererTest.VersionNode>();
	}

	@Test
	public void testRender_no_branch() {
		Version v0 = model.getInitialVersion();
		Version v1 = model.addVersion(v0, new NUllChange());
		Version v2 = model.addVersion(v1, new NUllChange());
		Version v3 = model.addVersion(v2, new NUllChange());

		versionTreeRenderer.render();
		
		Assert.assertEquals(4, nodes.size());
		
		VersionNode node;
		
		node = nodes.get(0);
		Assert.assertEquals(v0, node.version);
		Assert.assertEquals(0, node.x);
		Assert.assertEquals(0, node.y);
		
		node = nodes.get(1);
		Assert.assertEquals(v1, node.version);
		Assert.assertEquals(1, node.x);
		Assert.assertEquals(0, node.y);
		
		node = nodes.get(2);
		Assert.assertEquals(v2, node.version);
		Assert.assertEquals(2, node.x);
		Assert.assertEquals(0, node.y);
		
		node = nodes.get(3);
		Assert.assertEquals(v3, node.version);
		Assert.assertEquals(3, node.x);
		Assert.assertEquals(0, node.y);
	}
	
	@Test
	public void testRender_branch() {
		Version v1 = model.getInitialVersion();
		Version v2 = model.addVersion(v1, new NUllChange());
		Version v3 = model.addVersion(v2, new NUllChange());
		Version v4 = model.addVersion(v3, new NUllChange());
		Version v31 = model.addVersion(v3, new NUllChange());

		versionTreeRenderer.render();
		
		Assert.assertEquals(5, nodes.size());
		
		VersionNode node;
		
		node = nodes.get(0);
		Assert.assertEquals(v1, node.version);
		Assert.assertEquals(0, node.x);
		Assert.assertEquals(0, node.y);
		
		node = nodes.get(1);
		Assert.assertEquals(v2, node.version);
		Assert.assertEquals(1, node.x);
		Assert.assertEquals(0, node.y);
		
		node = nodes.get(2);
		Assert.assertEquals(v3, node.version);
		Assert.assertEquals(2, node.x);
		Assert.assertEquals(0, node.y);
		
		node = nodes.get(3);
		Assert.assertEquals(v4, node.version);
		Assert.assertEquals(3, node.x);
		Assert.assertEquals(0, node.y);
		
		node = nodes.get(4);
		Assert.assertEquals(v31, node.version);
		Assert.assertEquals(3, node.x);
		Assert.assertEquals(1, node.y);
	}
	
	static class VersionNode {
		Version version;
		int x;
		int y;

		public VersionNode(Version version, int x, int y) {
			this.version = version;
			this.x = x;
			this.y = y;
		}
	}

}
