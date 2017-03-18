package minusk.mtk.core;

import minusk.mtk.property.ColorProperty;
import minusk.mtk.scene.Node;
import minusk.mtk.scene.layout.Bin;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.system.MemoryStack;

import static minusk.mtk.core.Application.vg;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 * @author MinusKelvin
 */
public abstract class Stage {
	static int fbo;
	
	public final ColorProperty backgroundColor = new ColorProperty();
	final StageBin root = new StageBin();
	final Vector2i position = new Vector2i(), size = new Vector2i();
	boolean shown = false;
	private final Vector2d lsize = new Vector2d();
	int texture = -1;
	private int stencilTex = -1;
	private boolean renderRequested = true, reflowRequested, needTextureResize = true;
	
	Stage(Vector2ic s) {
		size.set(s);
		lsize.set(Application.toLogical(size));
		backgroundColor.addListener(root.requestRenderListener);
		root.requestReflow();
	}
	
	Stage(int width, int height) {
		this(new Vector2i(width, height));
	}
	
	Stage(Node node) {
		this(Application.toPhysical(node.getMinimumSize()));
		root.setChild(node);
	}
	
	void render() {
		if (reflowRequested) {
			Vector2dc ms = root.getMinimumSize();
			lsize.set(Application.toLogical(size));
			double w = lsize.x, h = lsize.y;
			if (ms.x() > lsize.x)
				w = Application.alignPhysical(ms.x());
			if (ms.y() > lsize.y)
				h = Application.alignPhysical(ms.y());
			if (w != lsize.x || h != lsize.y)
				resize(w, h);
			onReflow();
			reflowRequested = false;
			renderRequested = true;
			root.resize(Application.toLogical(size));
			if (!root.getSize().equals(Application.toLogical(size)))
				resize(root.getSize().x(), root.getSize().y());
		}
		if (renderRequested) {
			if (needTextureResize) {
				needTextureResize = false;
				glBindTexture(GL_TEXTURE_2D, texture);
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, size.x, size.y, 0, GL_RGBA, GL_BYTE, 0);
				glBindTexture(GL_TEXTURE_2D, stencilTex);
				glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_STENCIL, size.x, size.y, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
			}
			renderRequested = false;
			if (fbo == 0)
				fbo = glGenFramebuffers();
			glBindFramebuffer(GL_FRAMEBUFFER, fbo);
			glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, texture, 0);
			glFramebufferTexture(GL_FRAMEBUFFER, GL_STENCIL_ATTACHMENT, stencilTex, 0);
			glDrawBuffers(GL_COLOR_ATTACHMENT0);
			
			glViewport(0, 0, size.x, size.y);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				glClearBufferfv(GL_COLOR, 0, backgroundColor.get(stack.mallocFloat(4)));
				glClearBufferiv(GL_STENCIL, 0, stack.ints(0));
			}
			
			nvgBeginFrame(vg(), (int) Application.toLogical(size.x), (int) Application.toLogical(size.y), (float) Application.getScalingFactor());
			nvgResetTransform(vg());
			drawContent();
			nvgEndFrame(vg());
		}
	}
	
	public void show() {
		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		stencilTex = glGenTextures();
		needTextureResize = true;
		renderRequested = true;
		root.requestReflow();
		Application.addStage(this);
		shown = true;
	}
	
	public void close() {
		if (!shown)
			return;
		shown = false;
		glDeleteTextures(texture);
		glDeleteTextures(stencilTex);
		Application.removeStage(this);
	}
	
	void onReflow() {}
	
	void drawContent() {
		root.render();
	}
	
	boolean hasShadow() {
		return true;
	}
	
	public void setTitle(String title) {}
	
	public void setChild(Node node) {
		root.setChild(node);
	}
	
	public Node getChild() {
		return root.getChild();
	}
	
	/** Resizes this stage to something at least as large as its child's minimum size. */
	public void resize(double width, double height) {
		Vector2dc msize = root.getMinimumSize();
		if (msize.x() > width)
			width = msize.x();
		if (msize.y() > height)
			height = msize.y();
		lsize.set(Application.alignPhysical(width), Application.alignPhysical(height));
		if (Application.toPhysical(width) == size.x && Application.toPhysical(height) == size.y)
			return;
		size.set(Application.toPhysical(width), Application.toPhysical(height));
		needTextureResize = true;
		reflowRequested = true;
		renderRequested = true;
	}
	
	public void setPosition(double x, double y) {
		position.set(Application.toPhysical(x), Application.toPhysical(y));
	}
	
	public Vector2ic getPhysicalSize() {
		return size;
	}
	
	class StageBin extends Bin {
		@Override
		public void requestRender() {
			renderRequested = true;
		}
		
		@Override
		public void requestReflow() {
			super.requestReflow();
			reflowRequested = true;
		}
		
		@Override
		protected void render() {
			super.render();
		}
		
		@Override
		public Vector2d getScreenPosition() {
			return Application.toLogical(position).add(getPosition());
		}
	}
}
