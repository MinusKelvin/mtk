package minusk.mtk.style;

import minusk.mtk.core.Application;
import minusk.mtk.property.ColorProperty;
import minusk.mtk.property.FloatProperty;
import minusk.mtk.scene.stateless.Text;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static minusk.mtk.core.Application.vg;
import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Configurable text style class.
 * 
 * @author MinusKelvin
 */
public class TextStyle implements Style<Text> {
	public final ColorProperty color = new ColorProperty();
	public final FloatProperty size = new FloatProperty(60);
	public final FloatProperty blur = new FloatProperty();
	
	private final List<WeakReference<Text>> nodes = new ArrayList<>();
	
	public TextStyle() {
		color.addListener(this::requestRenderCB);
		size.addListener(this::requestReflowCB);
		blur.addListener(this::requestReflowCB);
	}
	
	/** Gets the minimum size of a piece of text. */
	public Vector2dc getMinimumSize(String text) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(4);
			nvgBeginFrame(vg(), 800, 600, (float) Application.getScalingFactor());
			nvgReset(vg());
			nvgFontFace(vg(), "sans");
			nvgFontSize(vg(), size.get());
			nvgFontBlur(vg(), blur.get());
			nvgTextBounds(vg(), 0, 0, text, buf);
			nvgCancelFrame(vg());
			return new Vector2d(buf.get(2)-buf.get(0), size.get());
		}
	}
	
	/** Renders the given text string at the origin. */
	public void render(String text) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(4);
			nvgFontFace(vg(), "sans");
			nvgFontSize(vg(), size.get());
			nvgFontBlur(vg(), blur.get());
			nvgTextBounds(vg(), 0, 0, text, buf);
			nvgFillColor(vg(), color.get(NVGColor.mallocStack()));
			nvgText(vg(), -buf.get(0), -buf.get(1), text);
		}
	}
	
	private void requestRenderCB() {
		for (Iterator<WeakReference<Text>> iter = nodes.iterator(); iter.hasNext();) {
			Text node = iter.next().get();
			if (node == null)
				iter.remove();
			else
				node.requestRender();
		}
	}
	
	private void requestReflowCB() {
		for (Iterator<WeakReference<Text>> iter = nodes.iterator(); iter.hasNext();) {
			Text node = iter.next().get();
			if (node == null)
				iter.remove();
			else
				node.requestReflow();
		}
	}
	
	@Override
	public void apply(Text node) {
		nodes.add(new WeakReference<>(node));
	}
	
	@Override
	public void unapply(Text node) {
		nodes.removeIf(e -> e.get() == node || e.get() == null);
	}
	
	/** Style intended for normal text */
	public static final TextStyle DEFAULT = new TextStyle();
}
