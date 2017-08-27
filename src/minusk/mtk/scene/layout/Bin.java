package minusk.mtk.scene.layout;

import minusk.mtk.core.Application;
import minusk.mtk.scene.Container;
import minusk.mtk.scene.Node;
import minusk.mtk.style.BinStyle;
import org.joml.Vector2d;
import org.joml.Vector2dc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A container that aligns its child to a specified alignment.
 * 
 * @author MinusKelvin
 */
public class Bin extends Container {
	private final ArrayList<Node> list = new ArrayList<>(1);
	private final List<Node> roView = Collections.unmodifiableList(list);
	private Node child;
	private BinStyle style;
	
	public Bin() {
		this(BinStyle.DEFAULT);
	}
	
	public Bin(BinStyle style) {
		this(null, style);
	}
	
	public Bin(Node node) {
		this(node, BinStyle.DEFAULT);
	}
	
	public Bin(Node node, BinStyle style) {
		this.style = style;
		style.apply(this);
		if (node != null)
			setChild(node);
	}
	
	@Override
	protected void reflow() {
		if (child != null) {
			child.resize(style.subExtraSize(new Vector2d(getSize())));
			Vector2d dif = style.subExtraSize(new Vector2d(getSize())).sub(child.getSize());
			if (style.expandX.get()) {
				switch (style.alignment.get()) {
					case TOP_LEFT:case CENTER_LEFT:case BOTTOM_LEFT:
						dif.x = 0;
						break;
					case TOP_CENTER:case CENTER:case BOTTOM_CENTER:
						dif.x /= 2;
						break;
				}
			}
			if (style.expandY.get()) {
				switch (style.alignment.get()) {
					case TOP_LEFT:case TOP_CENTER:case TOP_RIGHT:
						dif.y = 0;
						break;
					case CENTER_LEFT:case CENTER:case CENTER_RIGHT:
						dif.y /= 2;
						break;
				}
			}
			dif.add(style.getLeftOffset(), style.getTopOffset());
			child.setPosition(dif);
		}
	}
	
	@Override
	protected void render() {
		style.render(getSize());
		super.render();
	}
	
	@Override
	public Vector2dc getMinimumSize() {
		if (child == null)
			return Application.ZERO;
		return style.addExtraSize(new Vector2d(child.getMinimumSize()));
	}
	
	@Override
	public Vector2dc getMaximumSize() {
		Vector2d s = new Vector2d(child != null ? child.getMaximumSize() : Application.ZERO);
		if (style.expandX.get())
			s.x = -1;
		else if (s.x != -1)
			s.x += style.getExtraWidth();
		
		if (style.expandY.get())
			s.y = -1;
		else if (s.y != -1)
			s.y += style.getExtraHeight();
		return s;
	}
	
	public void setStyle(BinStyle style) {
		this.style.unapply(this);
		this.style = style;
		style.apply(this);
		requestReflow();
	}
	
	public BinStyle getStyle() {
		return style;
	}
	
	public void setChild(Node node) {
		addChild(node);
	}
	
	public Node getChild() {
		return child;
	}
	
	@Override
	protected void addChild_impl(Node node) {
		if (child != null)
			child.setParent(null);
		child = node;
	}
	
	@Override
	protected void removeChild_impl(Node node) {
		assert child == node;
		child = null;
	}
	
	@Override
	public List<Node> getChildren() {
		list.clear();
		if (child != null)
			list.add(child);
		return roView;
	}
}
