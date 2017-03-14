package minusk.mtk.property;

/**
 * @author MinusKelvin
 */
public interface ReadOnlyObjectProperty<T> extends IProperty {
	void addListener(ChangeListener<? super T> listener);
	void removeListener(ChangeListener<? super T> listener);
	T get();
	
	interface ChangeListener<T> {
		void onChange(T value);
	}
}
