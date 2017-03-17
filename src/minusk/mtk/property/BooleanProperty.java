package minusk.mtk.property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MinusKelvin
 */
public class BooleanProperty extends Property implements ReadOnlyBooleanProperty {
	private final List<ChangeListener> changeListeners = new ArrayList<>();
	private boolean value;
	
	public BooleanProperty() {
		this(false);
	}
	
	public BooleanProperty(boolean initialValue) {
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
	
	public void set(boolean value) {
		this.value = value;
		changeListeners.forEach(l -> l.onChange(value));
		invalidate();
	}
	
	@Override
	public boolean get() {
		return value;
	}
}
