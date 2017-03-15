package minusk.mtk.property;

import minusk.mtk.scene.Node;
import minusk.mtk.style.Style;

/**
 * A property specialized for dealing with styles.
 * 
 * @author MinusKelvin
 */
public class StyleProperty<N extends Node, S extends Style<N>> extends ObjectProperty<S> {
	private final N node;
	
	public StyleProperty(N node, S style) {
		super(false, style);
		this.node = node;
		style.apply(node);
	}
	
	@Override
	public void set(S value) {
		get().unapply(node);
		super.set(value);
		value.apply(node);
	}
}
