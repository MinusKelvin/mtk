package minusk.mtk.core;

import minusk.mtk.scene.Node;
import minusk.mtk.style.BinStyle;

/**
 * @author MinusKelvin
 */
public class PopupStage extends Stage {
	public PopupStage(double x, double y) {
		super(0, 0);
		root.setStyle(STYLE);
		position.set(Application.toPhysical(x), Application.toPhysical(y));
	}
	
	public PopupStage(Node node, double x, double y) {
		super(node);
		root.setStyle(STYLE);
		position.set(Application.toPhysical(x), Application.toPhysical(y));
	}
	
	public final static BinStyle STYLE = new BinStyle();
	static {
		STYLE.expandX.set(false);
		STYLE.expandY.set(false);
		STYLE.borderSize.set(1);
	}
}
