package hello;

import java.util.*;

import javax.servlet.ServletContext;

public class servletContext  {
	private ServletContext servletContext;
	private String absoluteDiskPath;
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public void setabsoluteDiskPath(String absoluteDiskPath) {
		this.absoluteDiskPath = absoluteDiskPath;
	}
	
	public String getabsoluteDiskPath(){
		return(servletContext.getRealPath("/"));
	}
}