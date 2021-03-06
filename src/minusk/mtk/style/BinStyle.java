package minusk.mtk.style;

import minusk.mtk.property.*;
import minusk.mtk.scene.layout.Bin;
import minusk.mtk.scene.layout.Position;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import static minusk.mtk.core.Application.vg;
import static org.lwjgl.nanovg.NanoVG.*;

/**
 * @author MinusKelvin
 */
public class BinStyle extends Style<Bin> {
	public final ObjectProperty<Position> alignment = new ObjectProperty<>(false, Position.CENTER);
	public final BooleanProperty expandX            = new BooleanProperty(true);
	public final BooleanProperty expandY            = new BooleanProperty(true);
	public final PaddingProperty padding            = new PaddingProperty(0, 0, 0, 0);
	public final FloatProperty borderSize           = new FloatProperty(0);
	public final ColorProperty borderColor          = new ColorProperty();
	public final ColorProperty backgroundColor      = new ColorProperty(0, 0, 0, 0);
	
	public BinStyle() {
		alignment.addListener(this::requestReflowCB);
		expandX.addListener(this::requestReflowCB);
		expandY.addListener(this::requestReflowCB);
		padding.addListener(this::requestReflowCB);
		borderSize.addListener(this::requestReflowCB);
		borderColor.addListener(this::requestRenderCB);
		backgroundColor.addListener(this::requestRenderCB);
	}
	
	/** Renders the background and border of a Bin at the origin given its size. */
	public void render(Vector2dc size) {
		if (backgroundColor.getA() != 0) {
			nvgBeginPath(vg());
			try (MemoryStack stack = MemoryStack.stackPush()) {
				nvgFillColor(vg(), backgroundColor.get(NVGColor.mallocStack()));
			}
			nvgRect(vg(), 0, 0, (float) size.x(), (float) size.y());
			nvgFill(vg());
		}
		if (borderSize.get() != 0 && borderColor.getA() != 0) {
			nvgBeginPath(vg());
			nvgRect(vg(), borderSize.get()/2, borderSize.get()/2,
					(float) size.x() - borderSize.get(),
					(float) size.y() - borderSize.get());
			try (MemoryStack stack = MemoryStack.stackPush()) {
				nvgStrokeColor(vg(), borderColor.get(NVGColor.mallocStack()));
			}
			nvgStrokeWidth(vg(), borderSize.get());
			nvgStroke(vg());
		}
	}
	
	/** Adds the value of the padding and border size to the given size */
	public Vector2d addExtraSize(Vector2d childSize) {
		return childSize.add(padding.getWidth(), padding.getHeight()).add(borderSize.get()*2, borderSize.get()*2);
	}
	
	public double getExtraWidth() {
		return padding.getWidth() + borderSize.get()*2;
	}
	
	public double getExtraHeight() {
		return padding.getHeight() + borderSize.get()*2;
	}
	
	/** Subtracts the value of the padding and border size from the given size */
	public Vector2d subExtraSize(Vector2d fullSize) {
		return fullSize.sub(padding.getWidth(), padding.getHeight()).sub(borderSize.get()*2, borderSize.get()*2);
	}
	
	public double getLeftOffset() {
		return padding.getLeft() + borderSize.get();
	}
	
	public double getTopOffset() {
		return padding.getTop() + borderSize.get();
	}
	
	public static final BinStyle DEFAULT = new BinStyle();
}
