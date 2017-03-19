package minusk.mtk.property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MinusKelvin
 */
public class ObjectProperty<T> extends Property implements ReadOnlyObjectProperty<T> {
	private final List<ChangeListener<? super T>> changeListeners = new ArrayList<>();
	private final boolean allowNull;
	private T value;
	
	public ObjectProperty() {
		this(true, null);
	}
	
	public ObjectProperty(boolean allowNull, T initialValue) {
		if (!allowNull && initialValue == null)
			throw new NullPointerException("value cannot be null");
		value = initialValue;
		this.allowNull = allowNull;
	}
	
	@Override
	public void addListener(ChangeListener<? super T> listener) {
		changeListeners.add(listener);
	}
	
	@Override
	public void removeListener(ChangeListener<? super T> listener) {
		changeListeners.remove(listener);
	}
	
	public void set(T value) {
		if (value == this.value)
			return;
		if (!allowNull && value == null)
			throw new NullPointerException("value cannot be null");
		this.value = value;
		changeListeners.forEach(l -> l.onChange(value));
		invalidate();
	}
	
	@Override
	public T get() {
		return value;
	}
}
