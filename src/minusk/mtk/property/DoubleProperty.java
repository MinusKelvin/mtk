package minusk.mtk.property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MinusKelvin
 */
public class DoubleProperty extends Property implements ReadOnlyDoubleProperty {
	private final List<ChangeListener> changeListeners = new ArrayList<>();
	private double value;
	
	public DoubleProperty() {
		this(0);
	}
	
	public DoubleProperty(double initialValue) {
		value = initialValue;
	}
	
	@Override
	public void addListener(ChangeListener listener) {
		changeListeners.add(listener);
	}
	
	@Override
	public void removeListener(ChangeListener listener) {
		changeListeners.remove(listener);
	}
	
	public void set(double value) {
		if (value == this.value)
			return;
		this.value = value;
		changeListeners.forEach(l -> l.onChange(value));
		invalidate();
	}
	
	@Override
	public double get() {
		return value;
	}
}
