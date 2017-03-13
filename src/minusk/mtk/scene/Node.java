package minusk.mtk.scene;

import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayList;

/**
 * @author MinusKelvin
 */
public abstract class Node {
	private final Vector2i position = new Vector2i(), size = new Vector2i();
	Container parent;
	
	public void setParent(Container newParent) {
		if (parent != null)
			parent.removeChild(this);
		if (newParent != null)
			newParent.addChild(this);
	}
	
	/** Positions this node within its parent. For layout purposes, usually not called directly. */
	public void setPosition(Vector2ic p) {
		position.set(p);
	}
	
	/**
	 * Resizes this node. Triggers the reflow of its children.
	 * This node will not be made smaller than its minimum size.
	 * This node will not be made larger than its minimum size unless it can expand.
	 * To implement reflow functionality, this method should be overridden but immediately called.
	 * */
	public void resize(Vector2ic size) {
		Vector2ic msize = getMinimumSize();
		if (size.x() < msize.x() || !canExpandX())
			this.size.x = msize.x();
		else
			this.size.x = size.x();
		
		if (size.y() < msize.y() || !canExpandY())
			this.size.y = msize.y();
		else
			this.size.y = size.y();
	}
	
	/** Tests if a point in this nodes's parent's coordinate space is inside this node. */
	public boolean isPointInNode(Vector2ic p) {
		return p.x() >= position.x && p.x() < position.x + size.x &&
				p.y() >= position.y && p.y() < position.y + size.y;
	}
	
	/** Transforms the point from this node's parent's coordinate space to this node's coordinate space. */
	public Vector2i transform(Vector2i p) {
		return p.sub(position);
	}
	
	/** Gets the minimum size of this node. */
	public abstract Vector2ic getMinimumSize();
	/** Returns true if this node can be expanded horizontally. */
	public abstract boolean canExpandX();
	/** Returns true if this node can be expanded vertically. */
	public abstract boolean canExpandY();
	
	/** Returns true if this node should not receive mouse events. */
	public abstract boolean isMouseTransparent();
	/**
	 * Called when the user moves their mouse in this node.
	 * The point is given in this node's coordinate space.
	 */
	public void mouseMove(Vector2ic mpos) {}
	/** Called when the user moves their mouse outside this node. */
	public void mouseExit() {}
	/** Called when the user presses a mouse button in this node. */
	public void mousePressed(int button, int mods) {}
	/** Called when the user releases a mouse button in this node. */
	public void mouseReleased(int button, int mods) {}
	/**
	 * Called when the user has pressed a mouse button in this node and has moved the mouse.
	 * The point is given in this node's coordinate space.
	 * The point may not necessarily be inside this node.
	 */
	public void mouseDragged(Vector2ic mpos) {}
	
	/**
	 * Gets a list of nodes that contain the specified point, ordered from top to bottom.
	 * The point is given in this node's coordinate space.
	 */
	public ArrayList<Node> findNodesByPoint(Vector2ic p) {
		ArrayList<Node> nodes = new ArrayList<>();
		findNodesByPoint(new Vector2i(p), nodes);
		return nodes;
	}
	
	void findNodesByPoint(Vector2i p, ArrayList<Node> nodes) {
		if (isPointInNode(p.add(position)))
			nodes.add(this);
		p.sub(position);
	}
	
	/** Internal */
	protected abstract void _render();
	
	/** Propagates upwards until something knows what to do with it. */
	public void requestRender() {
		if (parent != null)
			parent.requestRender();
	}
	
	/** Propagates upwards until something knows what to do with it. */
	public void requestReflow() {
		if (parent != null)
			parent.requestReflow();
	}
	
	public Vector2ic getPosition() {
		return position;
	}
	
	public Vector2ic getSize() {
		return size;
	}
	
	public Container getParent() {
		return parent;
	}
}
