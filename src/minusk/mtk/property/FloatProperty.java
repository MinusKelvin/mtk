package minusk.mtk.property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MinusKelvin
 */
public class FloatProperty extends Property implements ReadOnlyFloatProperty {
	private final List<ChangeListener> changeListeners = new ArrayList<>();
	private float value;
	
	public FloatProperty() {
		this(0);
	}
	
	public FloatProperty(float initialValue) {
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
	
	public void set(float value) {
		if (value == this.value)
			return;
		this.value = value;
		changeListeners.forEach(l -> l.onChange(value));
		invalidate();
	}
	
	@Override
	public float get() {
		return value;
	}
}
