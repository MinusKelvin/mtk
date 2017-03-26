package minusk.mtk.core;

import minusk.mtk.animation.Animation;
import minusk.mtk.gl.ShaderProgram;
import minusk.mtk.scene.Node;
import minusk.mtk.scene.layout.Bin;
import minusk.mtk.scene.stateless.Text;
import minusk.mtk.style.BinStyle;
import minusk.mtk.style.TextStyle;
import org.joml.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
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
	public static final Vector2dc NEGONE = new Vector2d(-1,-1);
	
	private static Application app;
	
	private static ArrayList<Animation> animations = new ArrayList<>();
	private static ArrayList<Animation> toAdd = new ArrayList<>();
	private static PriorityQueue<Timer> timers = new PriorityQueue<>();
	
	private static PrimaryStage primaryStage;
	private static PopupStage tooltipStage;
//	private static ArrayList<ToolStage> toolStages = new ArrayList<>();
//	private static ArrayList<ModalStage> modalStages = new ArrayList<>();
	private static ArrayList<PopupStage> menuStages = new ArrayList<>();
	private static ShaderProgram copyShader;
	private static Matrix4f pixelSpace = new Matrix4f();
	private static int vao, vbo;
	
	private static boolean running;
	private static double frameDelta, scalingFactor=1;
	private static long nvgContext, window;
	
	private static Timer tooltipTimer;
	private static Text tooltipText;
	private static Node mouseNode, scrollNode, tooltipNode;
	private static boolean[] dragging = new boolean[3];
	private static Vector2d mpos = new Vector2d();
	
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
		glfwSwapInterval(0);
		
		try {
			copyShader = new ShaderProgram(new InputStreamReader(Application.class.getResourceAsStream("/minusk/mtk/res/passthrough.vs.glsl")),
					new InputStreamReader(Application.class.getResourceAsStream("/minusk/mtk/res/copy.fs.glsl")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		vao = glGenVertexArrays();
		
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		
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
		
		primaryStage = new PrimaryStage(window, w, h);
		tooltipText = new Text(300, TOOLTIP_TEXT_STYLE);
		tooltipStage = new PopupStage(new Bin(tooltipText, TOOLTIP_BIN_STYLE), 0,0, true);
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
			if (!animations.isEmpty())
				glfwWaitEventsTimeout(0.01);
			else if (!timers.isEmpty()) {
				double timeout = timers.peek().time-glfwGetTime();
				if (timeout <= 0)
					glfwPollEvents();
				else
					glfwWaitEventsTimeout(timeout);
			} else
				glfwWaitEvents();
		}
	}
	
	static void render() {
		drawStage(primaryStage);
//		toolStages.forEach(Application::drawStage);
		menuStages.forEach(Application::drawStage);
		if (tooltipStage.isShown())
			drawStage(tooltipStage);
		
		glfwSwapBuffers(window);
	}
	
	private static void drawStage(Stage stage) {
		stage.render();
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, primaryStage.getPhysicalSize().x(), primaryStage.getPhysicalSize().y());
		
		pixelSpace.setOrtho2D(0, primaryStage.getPhysicalSize().x(), primaryStage.getPhysicalSize().y(), 0);
		glBindVertexArray(vao);
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, new float[] {
				stage.getPhysicalPosition().x(), stage.getPhysicalPosition().y(),
				stage.getPhysicalPosition().x()+stage.getPhysicalSize().x(),stage.getPhysicalPosition().y(),
				stage.getPhysicalPosition().x(),stage.getPhysicalPosition().y()+stage.getPhysicalSize().y(),
				stage.getPhysicalPosition().x()+stage.getPhysicalSize().x(),stage.getPhysicalPosition().y()+stage.getPhysicalSize().y()
		}, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glBindTexture(GL_TEXTURE_2D, stage.getTexture());
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
	public static Timer startTimer(double when, Runnable callback) {
		Timer t = new Timer();
		t.time = glfwGetTime()+when;
		t.toRun = callback;
		timers.add(t);
		return t;
	}
	
	public static void stopTimer(Timer t) {
		timers.remove(t);
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
	
	private static void pickNodes(Vector2dc pos) {
		mouseNode = null;
		scrollNode = null;
		tooltipNode = null;
		if (tooltipStage.isShown() && tooltipStage.isPointInStage(pos))
			tooltipStage.close();
		pickFrom(pos, primaryStage);
	}
	
	private static void pickFrom(Vector2dc pos, Stage stage) {
		List<Node> nodes = stage.root.findNodesByPoint(new Vector2d(pos).sub(stage.getPosition()));
		for (Node node : nodes) {
			if (node.shouldReceiveMouseEvents())
				mouseNode = node;
			if (node.shouldReceiveScrollEvents())
				scrollNode = node;
			if (node.tooltip.get() != null)
				tooltipNode = node;
		}
	}
	
	static void mousePosition(double x, double y) {
		if (tooltipTimer != null)
			stopTimer(tooltipTimer);
		Node mnode = mouseNode;
		Node tnode = tooltipNode;
		mpos.set(x, y);
		pickNodes(mpos);
		if (mnode != mouseNode && mnode != null)
			mnode.mouseExit();
		if (mouseNode != null)
			mouseNode.mouseMove(mpos.sub(mouseNode.getScreenPosition()));
		mpos.set(x,y);
		if (tnode != tooltipNode) {
			tooltipStage.close();
		}
		if (!tooltipStage.isShown() && tooltipNode != null) {
			tooltipStage.setPreferredPosition(x+10, y+10);
			tooltipText.text.set(tooltipNode.tooltip.get());
			tooltipTimer = startTimer(0.5, () -> {
				tooltipStage.show();
			});
		}
	}
	
	static void addStage(PopupStage stage) {
		menuStages.add(stage);
	}
	
	static void removeStage(PopupStage stage) {
		menuStages.remove(stage);
	}
	
	static void windowReflow() {
		for (Stage stage : menuStages)
			stage.root.requestReflow();
	}
	
	private static void glfwError(int errorcode, long description) {
		throw new RuntimeException(String.format(Locale.US, "GLFW Error Code %X: %s",
				errorcode, GLFWErrorCallback.getDescription(description)));
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
		windowReflow();
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
	
	public static class Timer {
		private double time;
		private Runnable toRun;
	}
	
	/** Style used by tooltip text */
	public static final TextStyle TOOLTIP_TEXT_STYLE = new TextStyle();
	/** Style used by the tooltip bin */
	public static final BinStyle TOOLTIP_BIN_STYLE = new BinStyle();
	
	static {
		TOOLTIP_BIN_STYLE.padding.set(2,2,2,2);
	}
}
