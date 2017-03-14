package minusk.mtk.test;

import minusk.mtk.Application;
import minusk.mtk.scene.StaticNode;
import minusk.mtk.scene.layout.Bin;
import minusk.mtk.scene.layout.Position;
import minusk.mtk.scene.stateless.Text;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * @author MinusKelvin
 */
public class Test extends Application {
	@Override
	public void start() {
		Bin b = new Bin(Position.CENTER, true, true);
		b.setChild(new Text("Testing", 250));
		Application.getPrimaryStage().setChild(b);
		Application.getPrimaryStage().backgroundColor.set(0, 1, 0, 1);
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
