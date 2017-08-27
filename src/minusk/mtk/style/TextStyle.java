package minusk.mtk.style;

import minusk.mtk.core.Application;
import minusk.mtk.property.ColorProperty;
import minusk.mtk.property.FloatProperty;
import minusk.mtk.scene.stateless.Text;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static minusk.mtk.core.Application.vg;
import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Configurable text style class.
 * 
 * @author MinusKelvin
 */
public class TextStyle extends Style<Text> {
	public final ColorProperty color = new ColorProperty();
	public final FloatProperty size  = new FloatProperty(16);
	public final FloatProperty blur  = new FloatProperty();
	
	public TextStyle() {
		color.addListener(this::requestRenderCB);
		size.addListener(this::requestReflowCB);
		blur.addListener(this::requestReflowCB);
	}
	
	/** Gets the size of a piece of text. */
	public Vector2dc getSize(String text, double width) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(4);
			nvgBeginFrame(vg(), 800, 600, (float) Application.getScalingFactor());
			nvgReset(vg());
			nvgFontFace(vg(), "sans");
			nvgFontSize(vg(), size.get());
			nvgFontBlur(vg(), blur.get());
			if (width > 0)
				nvgTextBoxBounds(vg(), 0, 0, (float) width, text, buf);
			else
				nvgTextBounds(vg(), 0, 0, text, buf);
			nvgCancelFrame(vg());
			return new Vector2d(buf.get(2)-buf.get(0), buf.get(3)-buf.get(1));
		}
	}
	
	/** Renders the given text string at the origin. */
	public void render(String text, double width) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(4);
			nvgFontFace(vg(), "sans");
			nvgFontSize(vg(), size.get());
			nvgFontBlur(vg(), blur.get());
			nvgFillColor(vg(), color.get(NVGColor.mallocStack()));
			nvgSave(vg());
			FloatBuffer xform = stack.mallocFloat(6);
			nvgCurrentTransform(vg(), xform);
			nvgResetTransform(vg());
			if (width > 0) {
				nvgTextBoxBounds(vg(), 0, 0, (float) width, text, buf);
				FloatBuffer x = stack.mallocFloat(1), y = stack.mallocFloat(1);
				nvgTransformPoint(x, y, xform, -buf.get(0), -buf.get(1));
				nvgTextBox(vg(), (float) Application.alignPhysical(x.get(0)), (float) Application.alignPhysical(y.get(0)), (float) width, text);
			} else {
				nvgTextBounds(vg(), 0, 0, text, buf);
				FloatBuffer x = stack.mallocFloat(1), y = stack.mallocFloat(1);
				nvgTransformPoint(x, y, xform, -buf.get(0), -buf.get(1));
				nvgText(vg(), (float) Application.alignPhysical(x.get(0)), (float) Application.alignPhysical(y.get(0)), text);
			}
			nvgRestore(vg());
		}
	}
	
	/** Style intended for normal text */
	public static final TextStyle DEFAULT = new TextStyle();
}
