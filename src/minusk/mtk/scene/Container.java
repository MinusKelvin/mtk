package minusk.mtk.scene;

import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

import static minusk.mtk.core.Application.vg;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgTranslate;

/**
 * @author MinusKelvin
 */
public abstract class Container extends Node {
	/** Adds a node to this container. */
	public void addChild(Node node) {
		if (isChild(node))
			return;
		if (node.parent != null)
			node.parent.removeChild(node);
		node.parent = this;
		addChild_impl(node);
		requestReflow();
	}
	
	/** Must make the node a child of this container. */
	protected abstract void addChild_impl(Node node);
	
	/** Removes a node from this container. */
	public void removeChild(Node node) {
		if (!isChild(node))
			return;
		node.parent = null;
		removeChild_impl(node);
		requestReflow();
	}
	
	/** Must remove the node from this container. */
	protected abstract void removeChild_impl(Node node);
	
	/** Test if the node is a child of this container. */
	public boolean isChild(Node node) {
		for (Node n : getChildren())
			if (n == node)
				return true;
		return false;
	}
	
	@Override
	protected void render() {
		for (Node node : getChildren()) {
			nvgSave(vg());
			nvgTranslate(vg(), (float) node.getPosition().x(), (float) node.getPosition().y());
			node.render();
			nvgRestore(vg());
		}
	}
	
	@Override
	void findNodesByPoint(Vector2d p, ArrayList<Node> nodes) {
		super.findNodesByPoint(p, nodes);
		for (Node node : getChildren()) {
			node.findNodesByPoint(p.add(node.getPosition()), nodes);
			p.sub(node.getPosition());
		}
	}
	
	public abstract List<Node> getChildren();
}
