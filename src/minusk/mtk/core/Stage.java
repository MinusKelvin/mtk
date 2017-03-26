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
	
	public final ColorProperty backgroundColor = new ColorProperty(1,1,1,1);
	final StageBin root = new StageBin();
	private final Vector2i position = new Vector2i(), size = new Vector2i();
	private final Vector2d lsize = new Vector2d();
	private int texture = -1;
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
			onReflow();
			root.resize(Application.toLogical(size));
			if (!Application.toPhysical(root.getSize()).equals(size)) {
				size.x = Application.toPhysical(root.getSize().x());
				size.y = Application.toPhysical(root.getSize().y());
				lsize.set(Application.toLogical(size));
				needTextureResize = true;
			}
			reflowRequested = false;
			renderRequested = true;
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
			root.render();
			nvgEndFrame(vg());
		}
	}
	
	/**
	 * Sets the size of this stage.
	 * Will not ge made larger than its child's maximum size
	 * nor smaller than its child's minimum size.
	 */
	public void resize(double width, double height) {
		Vector2dc minsize = root.getMinimumSize();
		if (width < minsize.x())
			width = minsize.x();
		if (height < minsize.y())
			height = minsize.y();
		Vector2dc maxsize = root.getMaximumSize();
		if (width > maxsize.x() && maxsize.x() != -1)
			width = maxsize.x();
		if (height > maxsize.y() && maxsize.y() != -1)
			height = maxsize.y();
		lsize.set(width, height);
		if (Application.toPhysical(width) == size.x && Application.toPhysical(height) == size.y)
			return;
		size.set(Application.toPhysical(width), Application.toPhysical(height));
		root.requestReflow();
		needTextureResize = true;
	}
	
	/** Creates GL resources for this stage */
	void show() {
		if (isShown())
			return;
		texture = glGenTextures();
		stencilTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		needTextureResize = true;
		renderRequested = true;
		root.requestReflow();
	}
	
	/** Releases GL resources held by this stage */
	void close() {
		if (!isShown())
			return;
		glDeleteTextures(texture);
		glDeleteTextures(stencilTex);
		texture = -1;
		stencilTex = -1;
	}
	
	boolean isPointInStage(Vector2dc pos) {
		double x = Application.toLogical(position.x);
		double y = Application.toLogical(position.y);
		double w = Application.toLogical(size.x);
		double h = Application.toLogical(size.y);
		return pos.y() >= y && pos.y() < y + h && pos.x() >= x && pos.x() < x+w;
	}
	
	void onReflow() {}
	
	public void setTitle(String title) {}
	
	public void setChild(Node node) {
		root.setChild(node);
	}
	
	public Node getChild() {
		return root.getChild();
	}
	
	public void setPosition(double x, double y) {
		position.set(Application.toPhysical(x), Application.toPhysical(y));
	}
	
	public Vector2ic getPhysicalPosition() {
		return position;
	}
	
	public Vector2dc getPosition() {
		return Application.toLogical(position);
	}
	
	public Vector2ic getPhysicalSize() {
		return size;
	}
	
	public Vector2dc getSize() {
		return Application.toLogical(size);
	}
	
	public boolean isShown() {
		return texture != -1;
	}
	
	int getTexture() {
		return texture;
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
