package minusk.mtk.property;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author MinusKelvin
 */
public class BooleanProperty extends Property implements ReadOnlyBooleanProperty {
	private final List<WeakReference<ChangeListener>> changeListeners = new ArrayList<>();
	private boolean value;
	
	public BooleanProperty() {
		this(false);
	}
	
	public BooleanProperty(boolean initialValue) {
		value = initialValue;
	}
	
	@Override
	public void addListener(ChangeListener listener) {
		changeListeners.add(new WeakReference<>(listener));
	}
	
	@Override
	public void removeListener(ChangeListener listener) {
		changeListeners.removeIf(e -> e.get() == listener);
	}
	
	public void set(boolean value) {
		this.value = value;
		for (Iterator<WeakReference<ChangeListener>> iter = changeListeners.iterator(); iter.hasNext();) {
			ChangeListener e = iter.next().get();
			if (e == null)
				iter.remove();
			else
				e.onChange(value);
		}
		invalidate();
	}
	
	@Override
	public boolean get() {
		return value;
	}
}
