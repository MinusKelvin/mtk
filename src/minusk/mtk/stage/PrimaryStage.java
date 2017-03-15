package minusk.mtk.stage;

import minusk.mtk.Application;
import org.joml.Vector2dc;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author MinusKelvin
 */
public final class PrimaryStage extends Stage {
	private final long window;
	
	private PrimaryStage(long win) {
		super(800, 600);
		window = win;
		
		glfwSetWindowCloseCallback(window, this::close);
		glfwSetFramebufferSizeCallback(window, this::fboSize);
		
		Vector2dc msize = root.getMinimumSize();
		glfwSetWindowSizeLimits(window, (int) msize.x(), (int) msize.y(), GLFW_DONT_CARE, GLFW_DONT_CARE);
	}
	
	public void show() {
		glfwShowWindow(window);
	}
	
	/** Internal */
	@Override
	public void _render() {
		super._render();
		
		glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glBlitFramebuffer(0, 0, size.x, size.y, 0, 0, size.x, size.y, GL_COLOR_BUFFER_BIT, GL_NEAREST);
		
		glfwSwapBuffers(window);
		
		System.gc();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		glfwSetWindowSize(window, size.x, size.y);
		Vector2dc msize = root.getMinimumSize();
		glfwSetWindowSizeLimits(window, (int) msize.x(),  (int) msize.y(), GLFW_DONT_CARE, GLFW_DONT_CARE);
	}
	
	private void close(long window) {
		Application.getApp().close();
	}
	
	private void fboSize(long window, int w, int h) {
		super.resize(w, h);
	}
	
	/** Internal */
	public static PrimaryStage _create() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		
		long window = glfwCreateWindow(800, 600, "", 0, 0);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		return new PrimaryStage(window);
	}
}
