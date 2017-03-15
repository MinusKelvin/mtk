package minusk.mtk.property;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author MinusKelvin
 */
public abstract class Property implements IProperty {
	private final List<WeakReference<InvalidateListener>> invalidateListeners = new ArrayList<>();
	
	@Override
	public void addListener(InvalidateListener listener) {
		invalidateListeners.add(new WeakReference<>(listener));
	}
	
	@Override
	public void removeListener(InvalidateListener listener) {
		invalidateListeners.removeIf(e -> e.get() == listener);
	}
	
	protected void invalidate() {
		for (Iterator<WeakReference<InvalidateListener>> iter = invalidateListeners.iterator(); iter.hasNext();) {
			InvalidateListener e = iter.next().get();
			if (e == null)
				iter.remove();
			else
				e.invalidate();
		}
	}
}
