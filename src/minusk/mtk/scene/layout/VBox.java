package minusk.mtk.scene.layout;

import minusk.mtk.scene.Container;
import minusk.mtk.scene.Node;
import org.joml.Vector2dc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author MinusKelvin
 */
public class VBox extends Container {
	private static final ArrayList<Node> children = new ArrayList<>();
	
	private VBox() {
		// WIP
	}
	
	@Override
	public List<Node> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	@Override
	public Vector2dc getMinimumSize() {
		return null;
	}
	
	@Override
	public boolean canExpandX() {
		for (Node n : children)
			if (n.canExpandX())
				return true;
		return false;
	}
	
	@Override
	public boolean canExpandY() {
		for (Node n : children)
			if (n.canExpandY())
				return true;
		return false;
	}
	
	@Override
	protected void addChild_impl(Node node) {
		children.add(node);
	}
	
	@Override
	protected void removeChild_impl(Node node) {
		children.remove(node);
	}
}
