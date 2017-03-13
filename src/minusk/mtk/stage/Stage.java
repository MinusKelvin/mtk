package minusk.mtk.stage;

import minusk.mtk.scene.Node;
import minusk.mtk.scene.layout.Alignment;
import minusk.mtk.scene.layout.Bin;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.system.MemoryStack;

import static minusk.mtk.Application.vg;
import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVG.nvgResetTransform;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 * @author MinusKelvin
 */
public abstract class Stage {
	static int fbo;
	
	protected final Vector2i position = new Vector2i(), size = new Vector2i();
	protected final StageBin root = new StageBin();
	private final int texture;
	private boolean renderRequested;
	private float r,g,b;
	
	private Stage(Vector2ic s) {
		size.set(s);
		root.resize(size);
		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, size.x, size.y, 0, GL_RGBA, GL_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		renderRequested = true;
	}
	
	public Stage(int width, int height) {
		this(new Vector2i(width, height));
	}
	
	public Stage(Node node) {
		this(node.getMinimumSize());
		root.setChild(node);
		root.resize(size);
	}
	
	public void setBackgroundColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	/** Internal */
	public void _render() {
		if (renderRequested) {
			if (fbo == 0)
				fbo = glGenFramebuffers();
			glBindFramebuffer(GL_FRAMEBUFFER, fbo);
			glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, texture, 0);
			glDrawBuffers(GL_COLOR_ATTACHMENT0);
			
			glViewport(0, 0, size.x, size.y);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				glClearBufferfv(GL_COLOR, 0, stack.floats(r, g, b, 1));
			}
			
			nvgBeginFrame(vg(), size.x, size.y, 1);
			nvgResetTransform(vg());
			drawContent();
			nvgEndFrame(vg());
		}
	}
	
	protected void drawContent() {
		root._render();
	}
	
	public void setChild(Node node) {
		root.setChild(node);
		root.resize(size);
	}
	
	public Node getChild() {
		return root.getChild();
	}
	
	/** Resizes this stage to something at least as large as its child's minimum size. */
	public void resize(int width, int height) {
		Vector2ic msize = root.getMinimumSize();
		if (msize.x() > width)
			width = msize.x();
		if (msize.y() > height)
			height = msize.y();
		if (width == size.x && height == size.y)
			return;
		size.set(width, height);
		root.resize(size);
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, size.x, size.y, 0, GL_RGBA, GL_BYTE, 0);
	}
	
	protected class StageBin extends Bin {
		private StageBin() {
			super(Alignment.MIDDLE, Alignment.MIDDLE);
		}
		
		@Override
		public void requestRender() {
			renderRequested = true;
		}
		
		@Override
		public void requestReflow() {
			Stage.this.resize(size.x, size.y);
			resize(getSize());
		}
		
		@Override
		protected void _render() {
			super._render();
		}
	}
}
