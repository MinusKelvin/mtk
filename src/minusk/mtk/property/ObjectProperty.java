package minusk.mtk.property;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author MinusKelvin
 */
public class ObjectProperty<T> extends Property implements ReadOnlyObjectProperty<T> {
	private final List<WeakReference<ChangeListener<? super T>>> changeListeners = new ArrayList<>();
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
		changeListeners.add(new WeakReference<>(listener));
	}
	
	@Override
	public void removeListener(ChangeListener<? super T> listener) {
		changeListeners.removeIf(e -> e.get() == listener);
	}
	
	public void set(T value) {
		if (!allowNull && value == null)
			throw new NullPointerException("value cannot be null");
		this.value = value;
		for (Iterator<WeakReference<ChangeListener<? super T>>> iter = changeListeners.iterator(); iter.hasNext();) {
			ChangeListener<? super T> e = iter.next().get();
			if (e == null)
				iter.remove();
			else
				e.onChange(value);
		}
		invalidate();
	}
	
	@Override
	public T get() {
		return value;
	}
}
