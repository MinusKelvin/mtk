package minusk.mtk.property;

import org.joml.Vector4dc;

/**
 * @author MinusKelvin
 */
public interface ReadOnlyPaddingProperty extends IProperty {
	double getTop();
	double getBottom();
	double getLeft();
	double getRight();
	Vector4dc get();
	void addListener(ChangeListener listener);
	void removeListener(ChangeListener listener);
	
	default double getWidth() {
		return getLeft() + getRight();
	}
	
	default double getHeight() {
		return getTop() + getBottom();
	}
	
	interface ChangeListener {
		void onChange(double left, double right, double top, double bottom);
	}
}
