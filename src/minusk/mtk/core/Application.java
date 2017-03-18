package minusk.mtk.core;

import minusk.mtk.animation.Animation;
import minusk.mtk.gl.ShaderProgram;
import minusk.mtk.scene.Node;
import org.joml.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.PriorityQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_ANTIALIAS;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.jemalloc.JEmalloc.*;

/**
 * @author MinusKelvin
 */
public abstract class Application {
	public static final Vector2dc ZERO = new Vector2d(0,0);
	
	private static Application app;
	
	private static ArrayList<Animation> animations = new ArrayList<>();
	private static ArrayList<Animation> toAdd = new ArrayList<>();
	private static PriorityQueue<Timer> timers = new PriorityQueue<>();
	
	private static PrimaryStage primaryStage;
	private static ArrayList<Stage> stages = new ArrayList<>();
	private static ShaderProgram copyShader;
	private static Matrix4f pixelSpace = new Matrix4f();
	private static int vao, vbo;
	
	private static boolean running;
	private static double frameDelta, scalingFactor=1;
	private static long nvgContext, window;
	
	private static Node mouseNode;
	private static boolean[] dragging = new boolean[3];
	private static double currentX, currentY;
	
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
		
		try {
			copyShader = new ShaderProgram(new InputStreamReader(Application.class.getResourceAsStream("/minusk/mtk/res/passthrough.vs.glsl")),
					new InputStreamReader(Application.class.getResourceAsStream("/minusk/mtk/res/copy.fs.glsl")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		vao = glGenVertexArrays();
		
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		
		primaryStage = new PrimaryStage(window, w, h);
		stages.add(primaryStage);
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
	
	static void render() {
		drawStage(primaryStage);
		stages.forEach(Application::drawStage);
		
		glfwSwapBuffers(window);
	}
	
	private static void drawStage(Stage stage) {
		stage.render();
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, primaryStage.size.x, primaryStage.size.y);
		
		if (stage.hasShadow()) {
			nvgBeginFrame(nvgContext, (int) Application.toLogical(primaryStage.size.x),
					(int) Application.toLogical(primaryStage.size.y), (float) getScalingFactor());
			nvgBeginPath(nvgContext);
			nvgRect(nvgContext, (float) Application.toLogical(stage.position.x)-10, (float) Application.toLogical(stage.position.y)-10,
					(float) Application.toLogical(stage.size.x)+20, (float) Application.toLogical(stage.size.y)+20);
			nvgRect(nvgContext, (float) Application.toLogical(stage.position.x), (float) Application.toLogical(stage.position.y),
					(float) Application.toLogical(stage.size.x), (float) Application.toLogical(stage.size.y));
			nvgPathWinding(nvgContext, NVG_CW);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				nvgFillPaint(nvgContext, nvgBoxGradient(nvgContext,
						(float) Application.toLogical(stage.position.x)-5, (float) Application.toLogical(stage.position.y)-5,
						(float) Application.toLogical(stage.size.x)+10, (float) Application.toLogical(stage.size.y)+10,
						4, 10, nvgRGBAf(0,0,0,0.25f, NVGColor.mallocStack()),
						nvgRGBAf(0,0,0,0, NVGColor.mallocStack()), NVGPaint.mallocStack()));
			}
			nvgFill(nvgContext);
			nvgEndFrame(nvgContext);
		}
		
		pixelSpace.setOrtho2D(0, primaryStage.size.x, primaryStage.size.y, 0);
		glBindVertexArray(vao);
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, new float[] {
				stage.position.x, stage.position.y,
				stage.position.x+stage.size.x,stage.position.y,
				stage.position.x,stage.position.y+stage.size.y,
				stage.position.x+stage.size.x,stage.position.y+stage.size.y
		}, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glBindTexture(GL_TEXTURE_2D, stage.texture);
		copyShader.bind();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			glUniformMatrix4fv(glGetUniformLocation(copyShader.id, "proj"), false, pixelSpace.get(stack.mallocFloat(16)));
		}
		glUniform1f(glGetUniformLocation(copyShader.id, "alpha"), 1);
		
		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
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
	
	static void addStage(Stage stage) {
		if (stage != primaryStage)
			stages.add(stage);
	}
	
	static void removeStage(Stage stage) {
		stages.remove(stage);
//		mousePosition(currentX, currentY);
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
	
	/** Sets the scaling factor */
	public static void setScalingFactor(double s) {
		scalingFactor = s;
		for (Stage stage : stages)
			stage.root.requestReflow();
	}
	
	/** Gets the scaling factor of the application. */
	public static double getScalingFactor() {
		return scalingFactor;
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
	
	/** Aligns the given logical coordinate to a physical coordinate. */
	public static double alignPhysical(double logical) {
		return toLogical(toPhysical(logical));
	}
	
	private static class Timer {
		double time;
		Runnable toRun;
	}
}
