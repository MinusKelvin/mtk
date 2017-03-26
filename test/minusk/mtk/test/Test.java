package minusk.mtk.test;

import minusk.mtk.core.Application;
import minusk.mtk.core.Stage;
import minusk.mtk.scene.Node;
import minusk.mtk.scene.layout.Bin;
import minusk.mtk.scene.stateless.Text;
import minusk.mtk.style.BinStyle;
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
		BinStyle o = new BinStyle();
		o.backgroundColor.set(1, 1, 0.5f, 1);
		o.expandX.set(false);
		o.expandY.set(false);
		Text t = new Text("Hello World!\nI am a multi-line text string!", 150);
		t.tooltip.set("I also have a tooltip! It can be as long as it needs to be because it can be multi-line!");
		b.setChild(new Bin(t, o));
		Application.getPrimaryStage().setTitle("Testaholic");
		Application.getPrimaryStage().setChild(b);
		Application.getPrimaryStage().backgroundColor.set(1, 1, 1, 1);
		Application.setScalingFactor(1);
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
			return new Vector2d(100, 100);
		}
		
		public Vector2dc getMaximumSize() { 
			return new Vector2d(200, 200);
		}
		
		@Override
		protected void render() {
			nvgBeginPath(vg());
			try (MemoryStack stack = MemoryStack.stackPush()) {
				nvgFillColor(vg(), nvgRGBAf(1, 0, 1, 1, NVGColor.mallocStack()));
			}
			nvgCircle(vg(), (float) getSize().x()/2, (float) getSize().y()/2, (float) Math.min(getSize().x(), getSize().y())/2);
			nvgCircle(vg(), (float) getSize().x()/2, (float) getSize().y()/2, 50);
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
