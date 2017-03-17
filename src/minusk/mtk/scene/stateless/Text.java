package minusk.mtk.scene.stateless;

import minusk.mtk.property.ObjectProperty;
import minusk.mtk.scene.Node;
import minusk.mtk.style.TextStyle;
import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * A node that displays
 * 
 * @author MinusKelvin
 */
public class Text extends Node {
	public final ObjectProperty<String> text;
	private TextStyle style;
	private final Vector2d msize = new Vector2d();
	
	public Text() {
		this("");
	}
	
	public Text(String text) {
		this(text, TextStyle.DEFAULT);
	}
	
	public Text(String text, TextStyle style) {
		this.text = new ObjectProperty<>(false, text);
		this.text.addListener(requestReflowListener);
		this.text.addListener(this::calcMinSize);
		this.style = style;
		style.apply(this);
		calcMinSize();
	}
	
	public void setStyle(TextStyle style) {
		this.style.unapply(this);
		this.style = style;
		style.apply(this);
		calcMinSize();
		requestReflow();
	}
	
	private void calcMinSize() {
		msize.set(style.getMinimumSize(text.get()));
	}
	
	public TextStyle getStyle() {
		return style;
	}
	
	@Override
	public Vector2dc getMinimumSize() {
		return msize;
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
		style.render(text.get());
	}
}
