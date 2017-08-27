package minusk.mtk.scene.layout;

import minusk.mtk.scene.Container;
import minusk.mtk.scene.Node;
import minusk.mtk.style.BoxStyle;
import org.joml.Vector2d;
import org.joml.Vector2dc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author MinusKelvin
 */
public abstract class Box extends Container {
	private final ArrayList<Node> children = new ArrayList<>();
	private final List<Node> roView = Collections.unmodifiableList(children);
	private BoxStyle style;
	
	public Box(BoxStyle style) {
		this.style = style;
	}
	
	@Override
	protected void reflow() {
		if (children.size() == 0)
			return;
		double spaceLeft = getRelevantDimension(getSize()) - style.gap.get()*(children.size()-1);
		double[] sizes = new double[children.size()];
		// Array for pass-by-reference (horrible style, I know)
		int[] canExpand = new int[] {0};
		// Initialize sizing data to children's minimum sizes
		for (int i = 0; i < children.size(); i++) {
			sizes[i] = getRelevantDimension(children.get(i).getMinimumSize());
			spaceLeft -= sizes[i];
			if (getRelevantDimension(children.get(i).getMaximumSize()) != sizes[i])
				canExpand[0]++;
		}
		// Expand children evenly to at most their maximum size
		while (spaceLeft > 0) {
			spaceLeft = iterate(spaceLeft, sizes, canExpand);
		}
		
		double relevantPos = 0;
		for (int i = 0; i < children.size(); i++) {
			Node node = children.get(i);
			Vector2d size = new Vector2d();
			Vector2d pos = new Vector2d(relevantPos, 0);
			size.x = sizes[i];
			if (getOrthogonalDimension(node.getMaximumSize()) == -1 ||
					getOrthogonalDimension(node.getMaximumSize()) >= getOrthogonalDimension(getSize())) {
				size.y = getOrthogonalDimension(getSize());
			} else {
				size.y = getOrthogonalDimension(node.getMaximumSize());
				if (isCentralPosition(style.alignment.get()))
					pos.y = (getOrthogonalDimension(getSize()) - size.y) / 2;
				else if (isHighPosition(style.alignment.get()))
					pos.y = getOrthogonalDimension(getSize()) - size.y;
			}
			orient(size);
			orient(pos);
			node.resize(size);
			node.setPosition(pos);
			relevantPos += sizes[i] + style.gap.get();
		}
	}
	
	private double iterate(double spaceLeft, double[] sizes, int[] canExpand) {
		// canExpand is an array fot pass-by-reference
		double allotedSpace = spaceLeft / canExpand[0];
		for (int i = 0; i < children.size(); i++) {
			double maxSize = getRelevantDimension(children.get(i).getMaximumSize());
			if (maxSize == sizes[i])
				continue; // Nothing to do here; child is already big
			if (maxSize == -1 || sizes[i]+allotedSpace < maxSize) {
				// Child can expand infinitely, so give it its alloted space
				sizes[i] += allotedSpace;
				spaceLeft -= allotedSpace;
			} else {
				// Child cannot expand by its alloted space, prepare for that space to be allocated elsewhere
				spaceLeft -= maxSize - sizes[i];
				sizes[i] = maxSize;
				canExpand[0]--;
			}
		}
		return spaceLeft;
	}
	
	@Override
	public Vector2dc getMinimumSize() {
		Vector2d msize = new Vector2d();
		if (children.size() != 0)
			msize.x = -style.gap.get();
		for (Node node : children) {
			Vector2dc nodeMSize = node.getMinimumSize();
			msize.x += getRelevantDimension(nodeMSize) + style.gap.get();
			msize.y = Math.max(msize.y, getOrthogonalDimension(nodeMSize));
		}
		orient(msize);
		return msize;
	}
	
	@Override
	public Vector2dc getMaximumSize() {
		Vector2d maxsize = new Vector2d();
		for (Node node : children) {
			Vector2dc nodeMaxSize = node.getMaximumSize();
			if (getRelevantDimension(nodeMaxSize) == -1)
				maxsize.x = -1;
			else if (maxsize.x != -1)
				maxsize.x += getRelevantDimension(nodeMaxSize) + style.gap.get();
			if (getOrthogonalDimension(nodeMaxSize) == -1)
				maxsize.y = -1;
			else
				maxsize.y = Math.max(maxsize.y, getOrthogonalDimension(nodeMaxSize));
		}
		if (children.size() != 0 && maxsize.x != -1)
			maxsize.x -= style.gap.get();
		orient(maxsize);
		return maxsize;
	}
	
	/** Gets the component of the vector parallel to the direction children are stacked */
	protected abstract double getRelevantDimension(Vector2dc v);
	/** Gets the other component of the vector compared to {@link Box#getRelevantDimension} */
	protected abstract double getOrthogonalDimension(Vector2dc v);
	/** Inverse of {@link Box#getRelevantDimension} and {@link Box#getOrthogonalDimension}.
	 * {@code v.x} is the relevant dimension, {@code v.y} is the orthogonal dimension. */
	protected abstract void orient(Vector2d v);
	/** Indicates that for the supplied {@code Position}, the node should be placed in the center of blank space. */
	protected abstract boolean isCentralPosition(Position p);
	/** Indicates that for the supplied {@code Position}, the node should be placed at the end of blank space. */
	protected abstract boolean isHighPosition(Position p);
	
	public void setStyle(BoxStyle style) {
		this.style.unapply(this);
		this.style = style;
		style.apply(this);
		requestReflow();
	}
	
	public BoxStyle getStyle() {
		return style;
	}
	
	@Override
	protected void addChild_impl(Node node) {
		children.add(node);
	}
	
	@Override
	protected void removeChild_impl(Node node) {
		children.remove(node);
	}
	
	@Override
	public List<Node> getChildren() {
		return roView;
	}
}
