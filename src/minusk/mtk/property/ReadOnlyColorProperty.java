package minusk.mtk.property;

import org.joml.Vector4fc;
import org.lwjgl.nanovg.NVGColor;

import java.nio.FloatBuffer;

/**
 * @author MinusKelvin
 */
public interface ReadOnlyColorProperty extends IProperty {
	float getR();
	float getG();
	float getB();
	float getA();
	NVGColor get(NVGColor dest);
	FloatBuffer get(FloatBuffer dest);
	Vector4fc get();
	void addListener(ChangeListener listener);
	void removeListener(ChangeListener listener);
	
	interface ChangeListener {
		void onChange(float r, float g, float b, float a);
	}
}
