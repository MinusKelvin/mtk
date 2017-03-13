package minusk.mtk.test;

import minusk.mtk.Application;
import minusk.mtk.scene.Node;
import minusk.mtk.scene.layout.Bin;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * @author MinusKelvin
 */
public class Test extends Application {
	@Override
	public void start() {
		Application.getPrimaryStage().resize(100, 100);
		Bin c = new Bin(null, null);
		Application.getPrimaryStage().setChild(c);
		c.setChild(new TestNode());
		Application.getPrimaryStage().setBackgroundColor(0, 1, 0);
	}
	
	public static void main(String[] args) {
		launch(new Test(), args);
	}
	
	private static class TestNode extends Node {
		
		@Override
		public Vector2ic getMinimumSize() {
			return new Vector2i(200, 200);
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
		public boolean isMouseTransparent() {
			return true;
		}
		
		@Override
		protected void _render() {
			nvgBeginPath(vg());
			try (MemoryStack stack = MemoryStack.stackPush()) {
				nvgFillColor(vg(), nvgRGBAf(1, 0, 1, 1, NVGColor.mallocStack()));
			}
			nvgCircle(vg(), 100, 100, 100);
			nvgFill(vg());
		}
	}
}
