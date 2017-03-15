package minusk.mtk.style;

import minusk.mtk.scene.Node;

/**
 * @author MinusKelvin
 */
public interface Style<T extends Node> {
	void apply(T node);
	void unapply(T node);
}
