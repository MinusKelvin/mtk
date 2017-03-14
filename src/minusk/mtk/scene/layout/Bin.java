package minusk.mtk.scene.layout;

import minusk.mtk.Application;
import minusk.mtk.property.BooleanProperty;
import minusk.mtk.property.ObjectProperty;
import minusk.mtk.scene.Node;
import minusk.mtk.scene.StaticContainer;
import org.joml.Vector2d;
import org.joml.Vector2dc;

import java.util.ArrayList;
import java.util.List;

/**
 * A container that aligns its child to a specified alignment.
 * 
 * @author MinusKelvin
 */
public class Bin extends StaticContainer {
	public final ObjectProperty<Position> alignment;
	public final BooleanProperty expandX, expandY;
	private final ArrayList<Node> list = new ArrayList<>(1);
	private Node child;
	
	public Bin(Position alignment, boolean expandX, boolean expandY) {
		this.alignment = new ObjectProperty<>(false, alignment);
		this.alignment.addListener(this::requestReflow);
		this.expandX = new BooleanProperty(expandX);
		this.expandX.addListener(this::requestReflow);
		this.expandY = new BooleanProperty(expandY);
		this.expandY.addListener(this::requestReflow);
	}
	
	public void setChild(Node node) {
		addChild(node);
	}
	
	public Node getChild() {
		return child;
	}
	
	@Override
	public void resize(Vector2dc size) {
		super.resize(size);
		if (child != null) {
			child.resize(size);
			Vector2d dif = getSize().sub(child.getSize(), new Vector2d());
			if (expandX.get()) {
				switch (alignment.get()) {
					case TOP_LEFT:case CENTER_LEFT:case BOTTOM_LEFT:
						dif.x = 0;
						break;
					case TOP_CENTER:case CENTER:case BOTTOM_CENTER:
						dif.x /= 2;
						break;
				}
			}
			if (expandY.get()) {
				switch (alignment.get()) {
					case TOP_LEFT:case TOP_CENTER:case TOP_RIGHT:
						dif.y = 0;
						break;
					case CENTER_LEFT:case CENTER:case CENTER_RIGHT:
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
	public Vector2dc getMinimumSize() {
		if (child == null)
			return Application.ZERO;
		return child.getMinimumSize();
	}
	
	@Override
	public boolean canExpandX() {
		return expandX.get();
	}
	
	@Override
	public boolean canExpandY() {
		return expandY.get();
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
