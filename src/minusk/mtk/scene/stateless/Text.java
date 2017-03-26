package minusk.mtk.scene.stateless;

import minusk.mtk.property.DoubleProperty;
import minusk.mtk.property.ObjectProperty;
import minusk.mtk.scene.Node;
import minusk.mtk.style.TextStyle;
import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * A node that displays a string of text.
 * 
 * @author MinusKelvin
 */
public class Text extends Node {
	public final ObjectProperty<String> text;
	/** Puts all the text on a single line if width <= 0 */
	public final DoubleProperty width;
	
	private final Vector2d msize = new Vector2d();
	private TextStyle style;
	private boolean needsToCalcMinSize = true;
	
	public Text() {
		this("");
	}
	
	public Text(TextStyle style) {
		this("", style);
	}
	
	public Text(double width, TextStyle style) {
		this("", width, TextStyle.DEFAULT);
	}
	
	public Text(String text) {
		this(text, 0, TextStyle.DEFAULT);
	}
	
	public Text(String text, TextStyle style) {
		this(text, 0, style);
	}
	
	public Text(String text, double width) {
		this(text, width, TextStyle.DEFAULT);
	}
	
	public Text(String text, double width, TextStyle style) {
		this.text = new ObjectProperty<>(false, text);
		this.text.addListener(requestReflowListener);
		this.width = new DoubleProperty(width);
		this.width.addListener(requestReflowListener);
		this.style = style;
		style.apply(this);
	}
	
	public void setStyle(TextStyle style) {
		this.style.unapply(this);
		this.style = style;
		style.apply(this);
		requestReflow();
	}
	
	private void calcMinSize() {
		needsToCalcMinSize = false;
		msize.set(style.getSize(text.get(), width.get()));
	}
	
	public TextStyle getStyle() {
		return style;
	}
	
	@Override
	public Vector2dc getMinimumSize() {
		if (needsToCalcMinSize)
			calcMinSize();
		return msize;
	}
	
	public Vector2dc getMaximumSize() {
		if (needsToCalcMinSize)
			calcMinSize();
		return msize;
	}
	
	@Override
	public void requestReflow() {
		needsToCalcMinSize = true;
		super.requestReflow();
	}
	
	@Override
	protected void render() {
		style.render(text.get(), width.get());
	}
}
