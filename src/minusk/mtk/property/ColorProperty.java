package minusk.mtk.property;

import org.lwjgl.nanovg.NVGColor;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.nvgRGBAf;

/**
 * @author MinusKelvin
 */
public class ColorProperty extends Property implements ReadOnlyColorProperty {
	private final List<ChangeListener> changeListeners = new ArrayList<>();
	private float r,g,b,a;
	
	public ColorProperty() {
		this(0, 0, 0, 1);
	}
	
	public ColorProperty(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	@Override
	public void addListener(ChangeListener listener) {
		changeListeners.add(listener);
	}
	
	@Override
	public void removeListener(ChangeListener listener) {
		changeListeners.remove(listener);
	}
	
	public void set(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		changeListeners.forEach(l -> l.onChange(r,g,b,a));
		invalidate();
	}
	
	public void setR(float r) {
		set(r, g, b, a);
	}
	
	public void setG(float g) {
		set(r, g, b, a);
	}
	
	public void setB(float b) {
		set(r, g, b, a);
	}
	
	public void setA(float a) {
		set(r, g, b, a);
	}
	
	@Override
	public float getR() {
		return r;
	}
	
	@Override
	public float getG() {
		return g;
	}
	
	@Override
	public float getB() {
		return b;
	}
	
	@Override
	public float getA() {
		return a;
	}
	
	@Override
	public NVGColor get(NVGColor dest) {
		return nvgRGBAf(r,g,b,a,dest);
	}
	
	@Override
	public FloatBuffer get(FloatBuffer dest) {
		int position = dest.position();
		dest.put(position, r);
		dest.put(position+1, g);
		dest.put(position+2, b);
		dest.put(position+3, a);
		return dest;
	}
}
