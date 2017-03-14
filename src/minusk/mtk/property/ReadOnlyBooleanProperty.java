package minusk.mtk.property;

/**
 * @author MinusKelvin
 */
public interface ReadOnlyBooleanProperty extends IProperty {
	void addListener(ChangeListener listener);
	
	void removeListener(ChangeListener listener);
	
	boolean get();
	
	interface ChangeListener {
		void onChange(boolean value);
	}
}
