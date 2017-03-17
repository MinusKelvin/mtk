package minusk.mtk.property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MinusKelvin
 */
public abstract class Property implements IProperty {
	private final List<InvalidateListener> invalidateListeners = new ArrayList<>();
	
	@Override
	public void addListener(InvalidateListener listener) {
		invalidateListeners.add(listener);
	}
	
	@Override
	public void removeListener(InvalidateListener listener) {
		invalidateListeners.remove(listener);
	}
	
	protected void invalidate() {
		invalidateListeners.forEach(InvalidateListener::invalidate);
	}
}
