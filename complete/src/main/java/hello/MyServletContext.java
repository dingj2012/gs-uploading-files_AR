package hello;

import java.io.File;
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
		return(new File(myServletContext.getRealPath("/")));
	}
	
	public File getFileinDiskPath(String filename){
		return(new File(myServletContext.getRealPath("/")+ "/" + filename));
	}
	
	/*
		Helper method, check if a named file(String) is in specific directory
	*/
	public boolean findFile(String name) {
		File[] list = absoluteDiskPath.listFiles();
	
		if (name.isEmpty()){
			throw new IllegalArgumentException("Illegal name, Stop!");
		} else {
			if(list!=null) {
				for (File fil : list) {
					if (fil.isDirectory()){
						findFile(name);
					} else if (name.equalsIgnoreCase(fil.getName())){
						return true;
					}
				}
			}
			return false;
		}
	}
	
}