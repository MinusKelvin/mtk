package minusk.mtk.scene.stateless;

import minusk.mtk.Application;
import minusk.mtk.property.ColorProperty;
import minusk.mtk.property.FloatProperty;
import minusk.mtk.property.ObjectProperty;
import minusk.mtk.scene.StaticNode;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static minusk.mtk.Application.vg;
import static org.lwjgl.nanovg.NanoVG.*;

/**
 * A node that displays
 * 
 * @author MinusKelvin
 */
public class Text extends StaticNode {
	public final ObjectProperty<String> text;
	public final ColorProperty color = new ColorProperty();
	public final FloatProperty size;
	
	public Text() {
		this("");
	}
	
	public Text(String text) {
		this(text, 12);
	}
	
	public Text(String text, float size) {
		this.text = new ObjectProperty<>(false, text);
		this.text.addListener(this::requestReflow);
		this.size = new FloatProperty(size);
		this.size.addListener(this::requestReflow);
		color.addListener(this::requestRender);
	}
	
	@Override
	public Vector2dc getMinimumSize() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(4);
			nvgFontFace(vg(), "sans");
			nvgFontSize(vg(), size.get());
			nvgTextBounds(vg(), 0, 0, text.get(), buf);
			return new Vector2d(buf.get(2)-buf.get(0), buf.get(3)-buf.get(1));
		}
	}
	
	@Override
	public boolean canExpandX() {
		return false;
	}
	
	@Override
	public boolean canExpandY() {
		return false;
	}
	
	@Override
	protected void render() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(4);
			nvgFontFace(vg(), "sans");
			nvgFontSize(vg(), size.get());
			nvgTextBounds(vg(), 0, 0, text.get(), buf);
			nvgFillColor(vg(), color.get(NVGColor.mallocStack()));
			nvgText(vg(), -buf.get(0), -buf.get(1), text.get());
		}
		color.setR((color.getR() + (float) Application.getDelta()/5) % 1);
	}
}
