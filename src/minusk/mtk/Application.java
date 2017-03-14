package minusk.mtk;

import minusk.mtk.stage.PrimaryStage;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Locale;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_ANTIALIAS;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.system.jemalloc.JEmalloc.je_free;
import static org.lwjgl.system.jemalloc.JEmalloc.je_malloc;
import static org.lwjgl.system.jemalloc.JEmalloc.je_realloc;

/**
 * @author MinusKelvin
 */
public abstract class Application {
	public static final Vector2dc ZERO = new Vector2d(0,0);
	
	private static Application app;
	private static PrimaryStage primaryStage;
	private static boolean running;
	private static double frameDelta;
	private static long nvgContext;
	
	/** Starts the application */
	public static void launch(Application app, String[] args) {
		if (running)
			throw new RuntimeException("Application is already running");
		running = true;
		Application.app = app;
		
		glfwSetErrorCallback(Application::glfwError);
		
		if (!glfwInit())
			throw new RuntimeException("Failed to initialize GLFW");
		
		primaryStage = PrimaryStage._create();
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
			primaryStage._render();
			glfwPollEvents();
		}
	}
	
	/** Quits the application without any checks */
	public static void quit() {
		running = false;
	}
	
	/** Called when the user tries to close the application */
	public void close() {
		quit();
	}
	
	public abstract void start();
	
	private static void glfwError(int errorcode, long description) {
		throw new RuntimeException(String.format(Locale.US, "GLFW Error Code %X: %s",
				errorcode, GLFWErrorCallback.getDescription(description)));
	}
	
	public static Application getApp() {
		return app;
	}
	
	public static PrimaryStage getPrimaryStage() {
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
}
