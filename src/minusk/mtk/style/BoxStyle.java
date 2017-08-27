package minusk.mtk.style;

import minusk.mtk.property.DoubleProperty;
import minusk.mtk.property.ObjectProperty;
import minusk.mtk.scene.layout.Box;
import minusk.mtk.scene.layout.Position;

/**
 * @author MinusKelvin
 */
public class BoxStyle extends Style<Box> {
	public final ObjectProperty<Position> alignment = new ObjectProperty<>(false, Position.CENTER);
	public final DoubleProperty gap                 = new DoubleProperty();
	
	public static final BoxStyle VBOX_DEFAULT = new BoxStyle();
	public static final BoxStyle HBOX_DEFAULT = new BoxStyle();
}
