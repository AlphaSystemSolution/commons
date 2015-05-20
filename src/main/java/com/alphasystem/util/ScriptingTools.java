/**
 * 
 */
package com.alphasystem.util;

import java.io.File;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.alphasystem.ApplicationException;

/**
 * @author sali
 * 
 */
public class ScriptingTools {

	private ScriptEngine engine;

	public ScriptingTools(File file) throws ScriptException, ApplicationException {
		this(AppUtil.readFile(file));
	}

	public ScriptingTools(String script) throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		engine = manager.getEngineByName("js");
		engine.eval(script);
	}
	
	

	public Object invokeFunction(String name, Object... args) {
		Invocable inv = (Invocable) engine;
		try {
			return inv.invokeFunction(name, args);
		} catch (ScriptException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
