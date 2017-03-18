package minusk.mtk.scene;

import minusk.mtk.property.IProperty;
import org.joml.Vector2d;
import org.joml.Vector2dc;

import java.util.ArrayList;

/**
 * @author MinusKelvin
 */
public abstract class Node {
	public final IProperty.InvalidateListener requestReflowListener = this::requestReflow;
	public final IProperty.InvalidateListener requestRenderListener = this::requestRender;
	
	private final Vector2d position = new Vector2d(), size = new Vector2d();
	Container parent;
	private boolean dirty = true;
	
	public void setParent(Container newParent) {
		if (parent != null)
			parent.removeChild(this);
		if (newParent != null)
			newParent.addChild(this);
	}
	
	/** Positions this node within its parent. For layout purposes, usually not called directly. */
	public void setPosition(Vector2dc p) {
		position.set(p);
	}
	
	/**
	 * Resizes this node.
	 * If the new size of this node is not equal to the current size of this node,
	 * or if the dirty flag on this node is set, <code>reflow()</code> will be called.
	 * This node will not be made smaller than its minimum size.
	 * This node will not be made larger than its minimum size unless it can expand.
	 * */
	public void resize(Vector2dc size) {
		Vector2dc msize = getMinimumSize();
		double newWidth, newHeight;
		if (size.x() < msize.x() || !canExpandX())
			newWidth = msize.x();
		else
			newWidth = size.x();
		
		if (size.y() < msize.y() || !canExpandY())
			newHeight = msize.y();
		else
			newHeight = size.y();
		
		if (newWidth != this.size.x || newHeight != this.size.y || dirty) {
			dirty = false;
			this.size.set(newWidth, newHeight);
			reflow();
		}
	}
	
	/**
	 * Called after this node was resized and its size changed or the dirty flag was set.
	 * Default implementation does nothing.
	 */
	protected void reflow() {}
	
	/** Tests if a point in this nodes's parent's coordinate space is inside this node. */
	public boolean isPointInNode(Vector2dc p) {
		return p.x() >= position.x && p.x() < position.x + size.x &&
				p.y() >= position.y && p.y() < position.y + size.y;
	}
	
	/**
	 * Gets a list of nodes that contain the specified point, ordered from top to bottom.
	 * The point is given in this node's coordinate space.
	 */
	public ArrayList<Node> findNodesByPoint(Vector2dc p) {
		ArrayList<Node> nodes = new ArrayList<>();
		findNodesByPoint(new Vector2d(p), nodes);
		return nodes;
	}
	
	void findNodesByPoint(Vector2d p, ArrayList<Node> nodes) {
		if (isPointInNode(p.add(position)))
			nodes.add(this);
		p.sub(position);
	}
	
	/** Gets the minimum size of this node. */
	public abstract Vector2dc getMinimumSize();
	/** Returns true if this node can be expanded horizontally. */
	public abstract boolean canExpandX();
	/** Returns true if this node can be expanded vertically. */
	public abstract boolean canExpandY();
	
	/**
	 * Returns true if this node should receive mouse events.
	 * Default implementation returns false.
	 */
	public boolean shouldReceiveMouseEvents() {
		return false;
	}
	
	/**
	 * Called when the user moves their mouse in this node.
	 * The point is given in this node's coordinate space.
	 * Default implementation does nothing.
	 */
	public void mouseMove(Vector2dc mpos) {}
	
	/**
	 * Called when the user moves their mouse outside this node.
	 * Default implementation does nothing.
	 */
	public void mouseExit() {}
	
	/**
	 * Called when the user presses a mouse button in this node.
	 * Default implementation does nothing.
	 */
	public void mousePressed(int button, int mods) {}
	
	/**
	 * Called when the user releases a mouse button in this node.
	 * Default implementation does nothing.
	 */
	public void mouseReleased(int button, int mods) {}
	
	/**
	 * Called when the user has pressed a mouse button in this node and has moved the mouse.
	 * The point is given in this node's coordinate space.
	 * The point may not necessarily be inside this node.
	 * Default implementation does nothing.
	 */
	public void mouseDragged(Vector2dc mpos) {}
	
	/**
	 * Returns true if this node should receive scroll events.
	 * Default implementation returns false.
	 */
	public boolean shouldReceiveScrollEvents() {
		return false;
	}
	
	/**
	 * Called when the user scrolls with their mouse wheel (or trackball)
	 * Default implementation does nothing.
	 */
	public void scroll(double horizontal, double vertical) {}
	
	/** Draws this node. Internally called. */
	protected abstract void render();
	
	/** Propagates upwards until something knows what to do with it. */
	public void requestRender() {
		if (parent != null)
			parent.requestRender();
	}
	
	/** Propagates upwards setting the dirty flag until something knows what to do with it. */
	public void requestReflow() {
		dirty = true;
		if (parent != null)
			parent.requestReflow();
	}
	
	public Vector2dc getPosition() {
		return position;
	}
	
	public Vector2d getScreenPosition() {
		if (parent == null)
			return new Vector2d();
		return parent.getScreenPosition().add(getPosition());
	}
	
	public Vector2dc getSize() {
		return size;
	}
	
	public Container getParent() {
		return parent;
	}
}
