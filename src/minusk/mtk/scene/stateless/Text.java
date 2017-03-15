package minusk.mtk.scene.stateless;

import minusk.mtk.property.ObjectProperty;
import minusk.mtk.property.StyleProperty;
import minusk.mtk.scene.StaticNode;
import minusk.mtk.style.TextStyle;
import org.joml.Vector2dc;

/**
 * A node that displays
 * 
 * @author MinusKelvin
 */
public class Text extends StaticNode {
	public final ObjectProperty<String> text;
	public final StyleProperty<Text, ? extends TextStyle> style;
	
	public Text() {
		this("");
	}
	
	public Text(String text) {
		this(text, TextStyle.DEFAULT);
	}
	
	public Text(String text, TextStyle style) {
		this.text = new ObjectProperty<>(false, text);
		this.text.addListener(requestReflowListener);
		this.style = new StyleProperty<>(this, style);
		this.style.addListener(requestReflowListener);
	}
	
	@Override
	public Vector2dc getMinimumSize() {
		return style.get().getMinimumSize(text.get());
	}
	
	@Override
	public boolean canExpandX() {
		return false;
	}
	
	@Override
	public boolean canExpandY() {
		return false;
	}
	
	@Override
	protected void render() {
		style.get().render(text.get());
	}
}
