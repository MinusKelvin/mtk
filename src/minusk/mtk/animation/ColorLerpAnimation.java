package minusk.mtk.animation;

import minusk.mtk.Application;
import minusk.mtk.property.ColorProperty;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * @author MinusKelvin
 */
public class ColorLerpAnimation extends Animation {
	private final ColorProperty target;
	private final Vector4fc from, to;
	private final Vector4f lerper = new Vector4f();
	private final float length;
	private float alpha;
	
	public ColorLerpAnimation(ColorProperty target, Vector4fc from, Vector4fc to, float length) {
		this.target = target;
		this.from = from;
		this.to = to;
		this.length = length;
	}
	
	@Override
	protected void update() {
		alpha += Application.getDelta() / length;
		if (alpha > 1)
			alpha = 1;
		target.set(from.lerp(to, alpha, lerper));
		if (alpha == 1)
			stop();
	}
	
	@Override
	protected void finish() {
		target.set(to);
	}
	
	@Override
	protected void begin() {
		alpha = 0;
		target.set(from);
	}
}
