package minusk.mtk.scene.layout;

import minusk.mtk.scene.Node;
import minusk.mtk.style.BoxStyle;
import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * @author MinusKelvin
 */
public class VBox extends Box {
	public VBox(Node... children) {
		this(BoxStyle.VBOX_DEFAULT, children);
	}
	
	public VBox(BoxStyle style, Node... children) {
		super(style);
		for (Node node : children)
			addChild(node);
	}
	
	@Override
	protected double getRelevantDimension(Vector2dc v) {
		return v.y();
	}
	
	@Override
	protected double getOrthogonalDimension(Vector2dc v) {
		return v.x();
	}
	
	@Override
	protected void orient(Vector2d v) {
		double tmp = v.x;
		v.x = v.y;
		v.y = tmp;
	}
	
	@Override
	protected boolean isCentralPosition(Position p) {
		switch (p) {
			case TOP_CENTER:
			case CENTER:
			case BOTTOM_CENTER:
				return true;
		}
		return false;
	}
	
	@Override
	protected boolean isHighPosition(Position p) {
		switch (p) {
			case TOP_RIGHT:
			case CENTER_RIGHT:
			case BOTTOM_RIGHT:
				return true;
		}
		return false;
	}
}
