package minusk.mtk.test;

import minusk.mtk.core.Application;
import minusk.mtk.core.PopupStage;
import minusk.mtk.core.Stage;
import minusk.mtk.scene.Node;
import minusk.mtk.scene.layout.Bin;
import minusk.mtk.style.BinStyle;
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
	@Override
	public void start() {
		BinStyle.DEFAULT.expandX.set(false);
		BinStyle.DEFAULT.expandY.set(false);
		BinStyle style = new BinStyle();
		style.borderSize.set(10);
		style.padding.set(20, 20, 20, 20);
		Bin b =  new Bin(style);
		TextStyle s = new TextStyle();
		s.size.set(60);
		b.setChild(new TestNode(Application.getPrimaryStage()));
		Application.getPrimaryStage().setTitle("NanoVG apparently doesn't like drawing big text");
		Application.getPrimaryStage().setChild(b);
		Application.getPrimaryStage().backgroundColor.set(1, 1, 1, 1);
		Application.setScalingFactor(1.5);
		Stage popup = new PopupStage(40, 40);
		popup.backgroundColor.set(1,1,1,1);
		popup.setChild(new TestNode(popup));
		popup.show();
	}
	
	public static void main(String[] args) {
		launch(new Test(), args);
	}
	
	private static class TestNode extends Node {
		private Stage s;
		public TestNode(Stage s) {
			this.s = s;
		}
		
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
		
		@Override
		public boolean shouldReceiveMouseEvents() {
			return true;
		}
		
		@Override
		public void mouseMove(Vector2dc mpos) {
			s.backgroundColor.set(1,0,0,1);
		}
		
		@Override
		public void mouseExit() {
			s.backgroundColor.set(1,1,1,1);
		}
	}
}
