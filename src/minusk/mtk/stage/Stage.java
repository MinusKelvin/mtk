package minusk.mtk.stage;

import minusk.mtk.property.ColorProperty;
import minusk.mtk.scene.Node;
import minusk.mtk.scene.layout.Position;
import minusk.mtk.scene.layout.Bin;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.system.MemoryStack;

import static minusk.mtk.Application.vg;
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
	protected final Vector2i position = new Vector2i(), size = new Vector2i();
	protected final StageBin root = new StageBin();
	private final int texture;
	private boolean renderRequested;
	
	private Stage(Vector2ic s) {
		size.set(s);
		root.resize(toV2d(size));
		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, size.x, size.y, 0, GL_RGBA, GL_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		renderRequested = true;
		backgroundColor.addListener(root::requestRender);
	}
	
	public Stage(int width, int height) {
		this(new Vector2i(width, height));
	}
	
	public Stage(Node node) {
		this(toV2i(node.getMinimumSize()));
		root.setChild(node);
		root.resize(toV2d(size));
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
				glClearBufferfv(GL_COLOR, 0, backgroundColor.get(stack.mallocFloat(4)));
			}
			
			nvgBeginFrame(vg(), size.x, size.y, 1);
			nvgResetTransform(vg());
			drawContent();
			nvgEndFrame(vg());
		}
	}
	
	protected void drawContent() {
		root.render();
	}
	
	public void setChild(Node node) {
		root.setChild(node);
		root.resize(toV2d(size));
	}
	
	public Node getChild() {
		return root.getChild();
	}
	
	/** Resizes this stage to something at least as large as its child's minimum size. */
	public void resize(int width, int height) {
		Vector2ic msize = toV2i(root.getMinimumSize());
		if (msize.x() > width)
			width = msize.x();
		if (msize.y() > height)
			height = msize.y();
		if (width == size.x && height == size.y)
			return;
		size.set(width, height);
		root.resize(new Vector2d(size.x, size.y));
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, size.x, size.y, 0, GL_RGBA, GL_BYTE, 0);
	}
	
	protected class StageBin extends Bin {
		private StageBin() {
			super(Position.CENTER, true, true);
		}
		
		@Override
		public void requestRender() {
			renderRequested = true;
		}
		
		@Override
		public void requestReflow() {
			Stage.this.resize(size.x, size.y);
			resize(getSize());
			requestRender();
		}
		
		@Override
		protected void render() {
			super.render();
		}
	}
	
	private static Vector2d toV2d(Vector2ic v) {
		return new Vector2d(v.x(), v.y());
	}
	
	private static Vector2i toV2i(Vector2dc v) {
		return new Vector2i((int) v.x(), (int) v.y());
	}
}
