package minusk.mtk.core;

import org.joml.Vector2dc;
import org.joml.Vector2ic;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author MinusKelvin
 */
final class PrimaryStage extends Stage {
	private final long window;
	
	PrimaryStage(long win, int w, int h) {
		super(w, h);
		window = win;
		
		glfwSetWindowCloseCallback(window, this::close);
		glfwSetFramebufferSizeCallback(window, this::fboSize);
		glfwSetWindowRefreshCallback(window, this::winRefresh);
		glfwSetCursorPosCallback(window, this::cursorPos);
	}
	
	@Override
	public void show() {
		super.show();
		glfwShowWindow(window);
	}
	
	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
		glfwSetWindowSize(window, getPhysicalSize().x(), getPhysicalSize().y());
	}
	
	@Override
	protected void onReflow() {
		Vector2ic minsize = Application.toPhysical(root.getMinimumSize());
		Vector2dc maxsize = root.getMaximumSize();
		glfwSetWindowSizeLimits(window, minsize.x(), minsize.y(),
				maxsize.x() == -1 ? -1 : Application.toPhysical(maxsize.x()),
				maxsize.y() == -1 ? -1 : Application.toPhysical(maxsize.y()));
	}
	
	@Override
	public void setPosition(double x, double y) {
		throw new IllegalStateException("Cannot change the position of the primary stage.");
	}
	
	@Override
	public void close() {
		Application.getApp().close();
	}
	
	@Override
	public void setTitle(String title) {
		glfwSetWindowTitle(window, title);
	}
	
	private void close(long window) {
		Application.getApp().close();
	}
	
	private void fboSize(long window, int w, int h) {
		super.resize(Application.toLogical(w), Application.toLogical(h));
		Application.windowReflow();
	}
	
	private void winRefresh(long window) {
		Application.render();
	}
	
	private void cursorPos(long window, double x, double y) {
		Application.mousePosition(Application.toLogical((int) x), Application.toLogical((int) y));
	}
}
