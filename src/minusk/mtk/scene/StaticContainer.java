package minusk.mtk.scene;

import org.joml.Vector2dc;

/**
 * @author MinusKelvin
 */
public abstract class StaticContainer extends Container {
	@Override
	public boolean shouldReceiveScrollEvents() {
		return false;
	}
	
	@Override
	public boolean shouldReceiveMouseEvents() {
		return false;
	}
	
	@Override public void mousePressed(int button, int mods) {}
	@Override public void mouseDragged(Vector2dc mpos) {}
	@Override public void mouseExit() {}
	@Override public void mouseMove(Vector2dc mpos) {}
	@Override public void mouseReleased(int button, int mods) {}
	@Override public void scroll(double horizontal, double vertical) {}
}
