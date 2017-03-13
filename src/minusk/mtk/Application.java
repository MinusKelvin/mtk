package minusk.mtk;

import minusk.mtk.stage.PrimaryStage;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.Locale;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_ANTIALIAS;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;

/**
 * @author MinusKelvin
 */
public abstract class Application {
	public static final Vector2ic ZERO = new Vector2i(0,0);
	
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
		app.start();
		
		nvgContext = nvgCreate(NVG_ANTIALIAS);
		
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
