package minusk.mtk.gl;

import java.io.IOException;
import java.io.Reader;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author MinusKelvin
 */
public abstract class Shader {
	public final int id;
	
	public Shader(Reader code, int shadertype) throws IOException {
		id = glCreateShader(shadertype);
		StringBuilder sb = new StringBuilder();
		int c;
		while ((c = code.read()) != -1)
			sb.append((char) c);
		glShaderSource(id, sb.toString());
		
		glCompileShader(id);
		
		if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE)
			throw new RuntimeException(glGetShaderInfoLog(id));
	}
	
	public void dispose() {
		glDeleteShader(id);
	}
	
	public static class VertexShader extends Shader {
		public VertexShader(Reader code) throws IOException {
			super(code, GL_VERTEX_SHADER);
		}
	}
	
	public static class FragmentShader extends Shader {
		public FragmentShader(Reader code) throws IOException {
			super(code, GL_FRAGMENT_SHADER);
		}
	}
}
