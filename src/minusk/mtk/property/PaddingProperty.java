package minusk.mtk.property;

import org.joml.Vector4d;
import org.joml.Vector4dc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MinusKelvin
 */
public class PaddingProperty extends Property implements ReadOnlyPaddingProperty {
	private final List<ChangeListener> changeListeners = new ArrayList<>();
	private final Vector4d padding = new Vector4d();
	
	/** Initializes to none */
	public PaddingProperty() {
		this(0, 0, 0, 0);
	}
	
	public PaddingProperty(double left, double right, double top, double bottom) {
		padding.set(left, right, top, bottom);
	}
	
	public PaddingProperty(Vector4dc padding) {
		this.padding.set(padding);
	}
	
	@Override
	public void addListener(ChangeListener listener) {
		changeListeners.add(listener);
	}
	
	@Override
	public void removeListener(ChangeListener listener) {
		changeListeners.remove(listener);
	}
	
	public void set(Vector4dc padding) {
		set(padding.x(), padding.y(), padding.z(), padding.w());
	}
	
	public void set(double left, double right, double top, double bottom) {
		if (left == padding.x && right == padding.y && top == padding.z && bottom == padding.w)
			return;
		padding.set(left, right, top, bottom);
		changeListeners.forEach(l -> l.onChange(left, right, top, bottom));
		invalidate();
	}
	
	public void setLeft(double left) {
		set(left, padding.y, padding.z, padding.w);
	}
	
	public void setRight(double right) {
		set(padding.x, right, padding.z, padding.w);
	}
	
	public void setTop(double top) {
		set(padding.x, padding.y, top, padding.w);
	}
	
	public void setBottom(double bottom) {
		set(padding.x, padding.y, padding.z, bottom);
	}
	
	@Override
	public double getLeft() {
		return padding.x;
	}
	
	@Override
	public double getRight() {
		return padding.y;
	}
	
	@Override
	public double getTop() {
		return padding.z;
	}
	
	@Override
	public double getBottom() {
		return padding.w;
	}
	
	@Override
	public Vector4dc get() {
		return padding;
	}
}
