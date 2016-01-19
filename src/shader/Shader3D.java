package shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import game.Game;
import game.Settings;

public class Shader3D {
	
	private Game game;
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	public Shader3D(Game game){
		this.game = game;
	}
	
	public void validateAndCompile(){
		vertexShaderID = loadShader("src\\vertexshader", GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader("src\\fragmentshader", GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		GL20.glUseProgram(programID);
		checkShaderStatus(vertexShaderID, "Error while linking vertex shader.");
		checkShaderStatus(fragmentShaderID, "Error while linking fragment shader");
	}
	
	public void cleanUp(){
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
		game.stop();
	}

	/*
	 * Input file /src/...
	 * Input int GLuInt GL20.(shader type here)
	 * Output shader id
	 */
	private int loadShader(String file, int type){
        StringBuilder shaderSource = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                shaderSource.append(line).append("\n");
            }
            reader.close();
        }catch(IOException exception){
           System.err.println("Error while loading the shader file.");
           if(Settings.enableInternalErrors){
        	   exception.printStackTrace();
           }
           game.stop();
        }
        
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        checkShaderStatus(shaderID, "Could not compile shader.");
        return shaderID;   
    }
	
	private void checkShaderStatus(int shaderID, String errorMessage){
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS)== GL11.GL_FALSE){ //glgetshaderi returns int, must use gl_false instead of going through java->lwjgl->opengl
        	System.err.println(errorMessage);
        	if(Settings.enableInternalErrors){
        		System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
        	}
            game.stop();
        }
	}
	
}
