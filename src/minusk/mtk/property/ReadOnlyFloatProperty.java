package minusk.mtk.property;

/**
 * @author MinusKelvin
 */
public interface ReadOnlyFloatProperty extends IProperty {
	void addListener(ChangeListener listener);
	void removeListener(ChangeListener listener);
	float get();
	
	interface ChangeListener {
		void onChange(float value);
	}
}
