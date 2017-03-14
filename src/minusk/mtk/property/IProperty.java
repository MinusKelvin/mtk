package minusk.mtk.property;

/**
 * @author MinusKelvin
 */
public interface IProperty {
	void addListener(InvalidateListener listener);
	void removeListener(InvalidateListener listener);
	
	interface InvalidateListener {
		void invalidate();
	}
}
