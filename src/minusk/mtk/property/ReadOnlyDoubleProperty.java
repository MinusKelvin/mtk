package minusk.mtk.property;

/**
 * @author MinusKelvin
 */
public interface ReadOnlyDoubleProperty extends IProperty {
	void addListener(ChangeListener listener);
	void removeListener(ChangeListener listener);
	double get();
	
	interface ChangeListener {
		void onChange(double value);
	}
}
