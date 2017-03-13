package minusk.mtk.gl;

import java.io.IOException;
import java.io.Reader;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author MinusKelvin
 */
public class ShaderProgram {
	public final int id;
	
	public ShaderProgram(Shader.VertexShader vertex, Shader.FragmentShader fragment, Shader... additional) {
		id = glCreateProgram();
		Shader[] shaders = new Shader[additional.length+2];
		shaders[0] = vertex;
		shaders[1] = fragment;
		System.arraycopy(additional, 0, shaders, 2, additional.length);
		build(shaders);
	}
	
	public ShaderProgram(Reader vertex, Reader fragment) throws IOException {
		id = glCreateProgram();
		Shader.VertexShader v = new Shader.VertexShader(vertex);
		Shader.FragmentShader f = new Shader.FragmentShader(fragment);
		build(v, f);
		v.dispose();
		f.dispose();
	}
	
	private void build(Shader... shaders) {
		for (Shader s : shaders)
			glAttachShader(id, s.id);
		
		glLinkProgram(id);
		
		if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE)
			throw new RuntimeException(glGetProgramInfoLog(id));
	}
	
	public void bind() {
		glUseProgram(id);
	}
	
	public void dispose() {
		glDeleteProgram(id);
	}
}
