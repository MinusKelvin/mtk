package minusk.mtk.property;

import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.nanovg.NVGColor;

import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.nvgRGBAf;

/**
 * @author MinusKelvin
 */
public class ColorProperty extends Property implements ReadOnlyColorProperty {
	private final List<WeakReference<ChangeListener>> changeListeners = new ArrayList<>();
	private final Vector4f color = new Vector4f();
	
	/** Initializes to black */
	public ColorProperty() {
		this(0, 0, 0, 1);
	}
	
	public ColorProperty(float r, float g, float b, float a) {
		color.set(r,g,b,a);
	}
	
	public ColorProperty(Vector4fc color) {
		this.color.set(color);
	}
	
	@Override
	public void addListener(ChangeListener listener) {
		changeListeners.add(new WeakReference<>(listener));
	}
	
	@Override
	public void removeListener(ChangeListener listener) {
		changeListeners.removeIf(e -> e.get() == listener);
	}
	
	public void set(Vector4fc color) {
		set(color.x(), color.y(), color.z(), color.w());
	}
	
	public void set(float r, float g, float b, float a) {
		color.set(r,g,b,a);
		for (Iterator<WeakReference<ChangeListener>> iter = changeListeners.iterator(); iter.hasNext();) {
			ChangeListener e = iter.next().get();
			if (e == null)
				iter.remove();
			else
				e.onChange(r,g,b,a);
		}
		invalidate();
	}
	
	public void setR(float r) {
		set(r, color.y, color.z, color.w);
	}
	
	public void setG(float g) {
		set(color.x, g, color.z, color.w);
	}
	
	public void setB(float b) {
		set(color.x, color.y, b, color.w);
	}
	
	public void setA(float a) {
		set(color.x, color.y, color.z, a);
	}
	
	@Override
	public float getR() {
		return color.x;
	}
	
	@Override
	public float getG() {
		return color.y;
	}
	
	@Override
	public float getB() {
		return color.z;
	}
	
	@Override
	public float getA() {
		return color.w;
	}
	
	@Override
	public NVGColor get(NVGColor dest) {
		return nvgRGBAf(color.x,color.y,color.z,color.w,dest);
	}
	
	@Override
	public FloatBuffer get(FloatBuffer dest) {
		return color.get(dest);
	}
	
	@Override
	public Vector4fc get() {
		return color;
	}
}
