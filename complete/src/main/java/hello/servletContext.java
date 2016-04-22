package hello;

import java.util.*;

import javax.servlet.ServletContext;

public class MyServletContext {
	private ServletContext myServletContext;
	private File absoluteDiskPath;
	
	public void setmyServletContext(ServletContext myServletContext) {
		this.myServletContext = myServletContext;
	}
	
	//Check if the given file is a directory
	public void setabsoluteDiskPath(File absoluteDiskPath) {
		if (!absoluteDiskPath.isDirectory()) {
			throw new IllegalArgumentException("Illegal path, Stop!");
		} else {
			this.absoluteDiskPath = absoluteDiskPath;
		}
	}
	
	public File getabsoluteDiskPath(){
		return(new file(myServletContext.getRealPath("/"));
	}
	
	public File getFileinDiskPath(String filename){
		return(new file(myServletContext.getRealPath("/")+ "/" + filename);
	}
	
}