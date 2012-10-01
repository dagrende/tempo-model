package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.List;


public class VersionTreeRenderer {
	private VersionModel model;
	private ShapeFactory shapeFactory;
	private int xScale = 30;
	private int yScale = 30;

	public void render() {
		List<Branch> branches = new ArrayList<Branch>();
		branches.add(new Branch(null, model.getInitialVersion(), 0));
		int xi = 0;
		while (!branches.isEmpty()) {
			drawBranches(branches, xi);
			int lastYi = branches.get(0).yi - 1;	// shift of existing branches to fit new branches
			List<Branch> nextBranches = new ArrayList<Branch>();
			for (int i = 0; i < branches.size(); i++) {
				Branch branch = branches.get(i);
				List<Version> successors = model.getVersionSuccessors(branch.version);
				for (int j = 0; j < successors.size(); j++) {
					Version succVersion = successors.get(j);
					int yi = Math.max(branch.yi, lastYi + 1);
					nextBranches.add(new Branch(branch, succVersion, yi));
					lastYi = yi;
				}
			}
			branches = nextBranches;
			xi++;
		}
	}
	
	public VersionTreeRenderer(VersionModel model, ShapeFactory shapeFactory) {
		super();
		this.model = model;
		this.shapeFactory = shapeFactory;
	}

	private void drawBranches(List<Branch> branches, int xi) {
		for (Branch branch : branches) {
			if (branch.prevBranch != null) {
				shapeFactory.addRelation((xi - 1) * getxScale(), branch.prevBranch.yi * getyScale(), xi * getxScale(), branch.yi * getyScale());
			}
			shapeFactory.addVersion(branch.version, xi * getxScale(), branch.yi * getyScale());
		}

	}
	
	public int getxScale() {
		return xScale;
	}

	public void setxScale(int xScale) {
		this.xScale = xScale;
	}

	public int getyScale() {
		return yScale;
	}

	public void setyScale(int yScale) {
		this.yScale = yScale;
	}

	public class Branch {
		Version version;	// version to draw
		int yi;				// y position index - multiply by version symbol height to get real y position
		Branch prevBranch;
		
		public Branch(Branch prevBranch, Version version, int yi) {
			this.prevBranch = prevBranch;
			this.version = version;
			this.yi = yi;
		}
	}

	public interface ShapeFactory {
		void addVersion(Version version, int x, int y);

		void addRelation(int x0, int y0, int x1, int y1);
	}

}
