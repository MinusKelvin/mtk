package minusk.mtk.scene.layout;

import minusk.mtk.scene.Node;
import minusk.mtk.style.BoxStyle;
import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * @author MinusKelvin
 */
public class HBox extends Box {
	public HBox(Node... children) {
		this(BoxStyle.HBOX_DEFAULT, children);
	}
	
	public HBox(BoxStyle style, Node... children) {
		super(style);
		for (Node node : children)
			addChild(node);
	}
	
	@Override
	protected double getRelevantDimension(Vector2dc v) {
		return v.x();
	}
	
	@Override
	protected double getOrthogonalDimension(Vector2dc v) {
		return v.y();
	}
	
	@Override
	protected void orient(Vector2d v) {}
	
	@Override
	protected boolean isCentralPosition(Position p) {
		switch (p) {
			case CENTER_LEFT:
			case CENTER:
			case CENTER_RIGHT:
				return true;
		}
		return false;
	}
	
	@Override
	protected boolean isHighPosition(Position p) {
		switch (p) {
			case BOTTOM_LEFT:
			case BOTTOM_CENTER:
			case BOTTOM_RIGHT:
				return true;
		}
		return false;
	}
}
