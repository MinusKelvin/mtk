package minusk.mtk.test;

import minusk.mtk.Application;
import minusk.mtk.animation.ColorLerpAnimation;
import minusk.mtk.scene.StaticNode;
import minusk.mtk.scene.stateless.Text;
import minusk.mtk.style.TextStyle;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * @author MinusKelvin
 */
public class Test extends Application {
	@Override
	public void start() {
		Text b = new Text("Testing");
		TextStyle.DEFAULT.blur.set(5);
		Application.getPrimaryStage().setChild(b);
		Application.getPrimaryStage().backgroundColor.set(1, 1, 1, 1);
		final ColorLerpAnimation a1 = new ColorLerpAnimation(TextStyle.DEFAULT.color,
				new Vector4f(1,0,0,1), new Vector4f(0,0,1,1), 1);
		final ColorLerpAnimation a2 = new ColorLerpAnimation(TextStyle.DEFAULT.color,
				new Vector4f(0,0,1,1), new Vector4f(1,0,0,1), 0.25f);
		a1.addStopListener(a2::start);
		a2.addStopListener(a1::start);
		a1.start();
	}
	
	public static void main(String[] args) {
		launch(new Test(), args);
	}
	
	private static class TestNode extends StaticNode {
		@Override
		public Vector2dc getMinimumSize() {
			return new Vector2d(200, 200);
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
			nvgBeginPath(vg());
			try (MemoryStack stack = MemoryStack.stackPush()) {
				nvgFillColor(vg(), nvgRGBAf(1, 0, 1, 1, NVGColor.mallocStack()));
			}
			nvgCircle(vg(), 100, 100, 100);
			nvgFill(vg());
		}
	}
}
