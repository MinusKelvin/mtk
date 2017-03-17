package minusk.mtk.core;

import minusk.mtk.animation.Animation;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.PriorityQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_ANTIALIAS;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.jemalloc.JEmalloc.*;

/**
 * @author MinusKelvin
 */
public abstract class Application {
	public static final Vector2dc ZERO = new Vector2d(0,0);
	
	private static Application app;
	private static PrimaryStage primaryStage;
	private static ArrayList<Animation> animations = new ArrayList<>();
	private static ArrayList<Animation> toAdd = new ArrayList<>();
	private static PriorityQueue<Timer> timers = new PriorityQueue<>();
	private static boolean running;
	private static double frameDelta;
	private static long nvgContext, window;
	
	/** Starts the application */
	public static void launch(Application app, String[] args) {
		if (running)
			throw new RuntimeException("Application is already running");
		running = true;
		Application.app = app;
		
		glfwSetErrorCallback(Application::glfwError);
		
		if (!glfwInit())
			throw new RuntimeException("Failed to initialize GLFW");
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		
		int w = 1024, h = 576;
		window = glfwCreateWindow(w, h, "", 0, 0);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		primaryStage = new PrimaryStage(window, w, h);
		nvgContext = nvgCreate(NVG_ANTIALIAS);
		try {
			InputStream file = Application.class.getResourceAsStream("/minusk/mtk/res/DejaVuSans.ttf");
			byte[] bytes = new byte[1024];
			int count;
			ByteBuffer stuff = je_malloc(0);
			
			while ((count = file.read(bytes)) != -1) {
				int pos = stuff.remaining();
				stuff = je_realloc(stuff, pos + count);
				stuff.position(pos);
				stuff.put(bytes, 0, count);
				stuff.position(0);
			}
			nvgCreateFontMem(nvgContext, "sans", stuff, 0);
			je_free(stuff);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		app.start();
		
		primaryStage.show();
		
		double t = glfwGetTime();
		while (running) {
			double c = glfwGetTime();
			frameDelta = c - t;
			t = c;
			animations.addAll(toAdd);
			toAdd.clear();
			for (Iterator<Animation> iter = animations.iterator(); iter.hasNext();)
				if (iter.next()._tick())
					iter.remove();
			while (!timers.isEmpty() && timers.peek().time <= glfwGetTime())
				timers.poll().toRun.run();
			render();
			glfwPollEvents();
		}
	}
	
	/** Quits the application without any checks */
	public static void quit() {
		running = false;
	}
	
	/**
	 * Calls the supplied <code>Runnable</code> after seconds have passed.
	 * This is not an accurate timer, and the callback will only be called during a cycle of the event loop.
	 */
	public static void startTimer(double when, Runnable callback) {
		Timer t = new Timer();
		t.time = glfwGetTime()+when;
		t.toRun = callback;
		timers.add(t);
	}
	
	/** Called when the user tries to close the application */
	public void close() {
		quit();
	}
	
	public abstract void start();
	
	/** Internal */
	public static void _addAnimation(Animation animation) {
		if (!animations.contains(animation) && !toAdd.contains(animation))
			toAdd.add(animation);
	}
	
	private static void glfwError(int errorcode, long description) {
		throw new RuntimeException(String.format(Locale.US, "GLFW Error Code %X: %s",
				errorcode, GLFWErrorCallback.getDescription(description)));
	}
	
	static void render() {
		primaryStage.render();
		
		glBindFramebuffer(GL_READ_FRAMEBUFFER, Stage.fbo);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glBlitFramebuffer(0, 0, primaryStage.size.x, primaryStage.size.y,
				0, 0, primaryStage.size.x, primaryStage.size.y, GL_COLOR_BUFFER_BIT, GL_NEAREST);
		
		glfwSwapBuffers(window);
	}
	
	public static Application getApp() {
		return app;
	}
	
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	
	/** Gets the time in seconds since the previous frame. Used for animations. */
	public static double getDelta() {
		return frameDelta;
	}
	
	/** Get the NanoVG context. */
	public static long vg() {
		return nvgContext;
	}
	
	/** Gets the scaling factor of the application. */
	public static double getScalingFactor() {
		return 2;
	}
	
	/** Converts the given logical coordinate to a physical coordinate. */
	public static int toPhysical(double logical) {
		return (int) Math.floor(logical * getScalingFactor());
	}
	
	/** Converts the given physical coordinate to a logical coordinate. */
	public static double toLogical(int physical) {
		return physical / getScalingFactor();
	}
	
	/** Converts the given logical coordinate to a physical coordinate. */
	public static Vector2i toPhysical(Vector2dc logical) {
		return new Vector2i(toPhysical(logical.x()), toPhysical(logical.y()));
	}
	
	/** Converts the given physical coordinate to a logical coordinate. */
	public static Vector2d toLogical(Vector2ic physical) {
		return new Vector2d(toLogical(physical.x()), toLogical(physical.y()));
	}
	
	private static class Timer {
		double time;
		Runnable toRun;
	}
}
