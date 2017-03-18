package minusk.mtk.core;

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
		
		Vector2ic msize = Application.toPhysical(root.getMinimumSize());
		int x = GLFW_DONT_CARE, y = GLFW_DONT_CARE;
		if (!root.canExpandX())
			x = msize.x();
		if (!root.canExpandY())
			y = msize.y();
		glfwSetWindowSizeLimits(window, msize.x(), msize.y(), x, y);
	}
	
	@Override
	public void show() {
		if (shown)
			return;
		super.show();
		glfwShowWindow(window);
	}
	
	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
		glfwSetWindowSize(window, size.x, size.y);
		Vector2ic msize = Application.toPhysical(root.getMinimumSize());
		int x = GLFW_DONT_CARE, y = GLFW_DONT_CARE;
		if (!root.canExpandX())
			x = msize.x();
		if (!root.canExpandY())
			y = msize.y();
		glfwSetWindowSizeLimits(window, msize.x(), msize.y(), x, y);
	}
	
	@Override
	protected void onReflow() {
		Vector2ic msize = Application.toPhysical(root.getMinimumSize());
		int x = GLFW_DONT_CARE, y = GLFW_DONT_CARE;
		if (!root.canExpandX())
			x = msize.x();
		if (!root.canExpandY())
			y = msize.y();
		glfwSetWindowSizeLimits(window, msize.x(), msize.y(), x, y);
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
	
	@Override
	boolean hasShadow() {
		return false;
	}
	
	private void close(long window) {
		Application.getApp().close();
	}
	
	private void fboSize(long window, int w, int h) {
		super.resize(Application.toLogical(w), Application.toLogical(h));
	}
	
	private void winRefresh(long window) {
		Application.render();
	}
}
