package minusk.mtk.scene.layout;

import minusk.mtk.Application;
import minusk.mtk.scene.Container;
import minusk.mtk.scene.Node;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.List;

/**
 * A container that aligns its child to a specified alignment.
 * 
 * @author MinusKelvin
 */
public class Bin extends Container {
	private final ArrayList<Node> list = new ArrayList<>(1);
	private Alignment horizontal, vertical;
	private Node child;
	
	/** Alignments can be null (prevents expansion) */
	public Bin(Alignment horizontal, Alignment vertical) {
		this.horizontal = horizontal;
		this.vertical = vertical;
	}
	
	public void setChild(Node node) {
		addChild(node);
	}
	
	public Node getChild() {
		return child;
	}
	
	@Override
	public void resize(Vector2ic size) {
		super.resize(size);
		if (child != null) {
			child.resize(size);
			Vector2i dif = getSize().sub(child.getSize(), new Vector2i());
			if (horizontal != null) {
				switch (horizontal) {
					case LOW:
						dif.x = 0;
						break;
					case MIDDLE:
						dif.x /= 2;
						break;
				}
			}
			if (vertical != null) {
				switch (vertical) {
					case LOW:
						dif.y = 0;
						break;
					case MIDDLE:
						dif.y /= 2;
						break;
				}
			}
			child.setPosition(dif);
		}
	}
	
	@Override
	public List<Node> getChildren() {
		list.clear();
		if (child != null)
			list.add(child);
		return list;
	}
	
	@Override
	public Vector2ic getMinimumSize() {
		if (child == null)
			return Application.ZERO;
		return child.getMinimumSize();
	}
	
	@Override
	public boolean canExpandX() {
		return horizontal != null;
	}
	
	@Override
	public boolean canExpandY() {
		return vertical != null;
	}
	
	@Override
	public boolean isMouseTransparent() {
		return true;
	}
	
	@Override
	protected void addChild_impl(Node node) {
		if (child != null)
			child.setParent(null);
		child = node;
	}
	
	@Override
	protected void removeChild_impl(Node node) {
		assert child == node;
		child = null;
	}
}
