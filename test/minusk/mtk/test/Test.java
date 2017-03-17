package minusk.mtk.test;

import minusk.mtk.core.Application;
import minusk.mtk.scene.Node;
import minusk.mtk.scene.stateless.Text;
import minusk.mtk.style.TextStyle;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * @author MinusKelvin
 */
public class Test extends Application {
	Text b;
	TextStyle otherStyle;
	
	@Override
	public void start() {
		b = new Text("Testing");
		Application.getPrimaryStage().setTitle("NanoVG apparently doesn't like drawing big text");
		Application.getPrimaryStage().setChild(b);
		Application.getPrimaryStage().backgroundColor.set(1, 1, 1, 1);
//		new FloatLerpAnimation(TextStyle.DEFAULT.blur, 0, 10, 5).loop();
	}
	
	public static void main(String[] args) {
		launch(new Test(), args);
	}
	
	private static class TestNode extends Node {
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
			nvgCircle(vg(), 100, 100, 50);
			nvgPathWinding(vg(), NVG_CW);
			nvgFill(vg());
		}
	}
}
