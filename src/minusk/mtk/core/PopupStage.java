package minusk.mtk.core;

import minusk.mtk.scene.Node;
import minusk.mtk.style.BinStyle;
import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * @author MinusKelvin
 */
public class PopupStage extends Stage {
	private double prefX, prefY;
	private final boolean add;
	
	PopupStage(Node node, double x, double y, boolean skipAdd) {
		super(node);
		add = !skipAdd;
		root.setStyle(STYLE);
		setPosition(x, y);
		prefX = x;
		prefY = y;
		Vector2dc s = node.getMaximumSize();
		Vector2dc m = node.getMinimumSize();
		resize(s.x() == -1 ? m.x() + root.getStyle().getExtraWidth() : s.x() + root.getStyle().getExtraWidth(),
				s.y() == -1 ? m.y() + root.getStyle().getExtraHeight() : s.y() + root.getStyle().getExtraHeight());
	}
	
	@Override
	public void resize(double width, double height) {
		Vector2dc s = Application.getPrimaryStage().getSize();
		if (width > s.x())
			width = s.x();
		if (height > s.y())
			height = s.y();
		super.resize(width, height);
	}
	
	@Override
	void onReflow() {
		Vector2dc msize = Application.getPrimaryStage().getSize();
		Vector2dc size = root.getMaximumSize();
		Vector2d p = new Vector2d(prefX, prefY);
		if (p.x < 0)
			p.x = 0;
		if (p.y < 0)
			p.y = 0;
		if (p.x + size.x() > msize.x())
			p.x = msize.x() - size.x();
		if (p.y + size.y() > msize.y())
			p.y = msize.y() - size.y();
		System.out.println("pos calced: "+p.x+", "+p.y);
		if (p.x < 0 || p.y < 0 || size.x() > getSize().x() || size.y() > getSize().y()) {
			resize(p.x < 0 ? msize.x() : size.x(), p.y < 0 ? msize.y() : size.y());
			setPosition(p.x < 0 ? 0 : p.x, p.y < 0 ? 0 : p.y);
		} else if (p.x != getPosition().x() || p.y != getPosition().y())
			setPosition(p.x, p.y);
	}
	
	public void setPreferredPosition(double x, double y) {
		prefX = x;
		prefY = y;
		onReflow();
	}
	
	@Override
	public void show() {
		if (getTexture() != -1)
			return;
		super.show();
		if (add)
			Application.addStage(this);
	}
	
	@Override
	public void close() {
		if (getTexture() == -1)
			return;
		super.close();
		if (add)
			Application.removeStage(this);
	}
	
	@Override
	public void setChild(Node node) {
		super.setChild(node);
		Vector2dc s = node.getMaximumSize();
		Vector2dc m = node.getMinimumSize();
		resize(s.x() == -1 ? m.x() + root.getStyle().getExtraWidth() : s.x() + root.getStyle().getExtraWidth(),
				s.y() == -1 ? m.y() + root.getStyle().getExtraHeight() : s.y() + root.getStyle().getExtraHeight());
	}
	
	public final static BinStyle STYLE = new BinStyle();
	static {
		STYLE.expandX.set(false);
		STYLE.expandY.set(false);
		STYLE.borderSize.set(1);
	}
}
