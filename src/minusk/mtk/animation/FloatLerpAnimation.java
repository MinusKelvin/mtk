package minusk.mtk.animation;

import minusk.mtk.core.Application;
import minusk.mtk.property.FloatProperty;
import org.joml.Vector4f;

/**
 * @author MinusKelvin
 */
public class FloatLerpAnimation extends Animation {
	private final FloatProperty target;
	private final float from, to;
	private final Vector4f lerper = new Vector4f();
	private final float length;
	private float alpha;
	
	public FloatLerpAnimation(FloatProperty target, float from, float to, float length) {
		super(true);
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
		target.set(alpha * to + from * (1-alpha));
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
