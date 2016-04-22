package hello;

import java.util.*;
import java.io.File;

/*
	Return true if named file is in the give path(disk path particularly);
	otherwise return false.
*/
public boolean findFile(String name,File file) {
	
	File[] list = file.listFiles();
	
	if (name.isEmpty() || file.isEmpty()){
		throw new IllegalArgumentException("Illegal File and name, Stop!");
	} else {
		if(list!=null) {
			for (File fil : list) {
				if (fil.isDirectory()){
					findFile(name,fil);
				} else if (name.equalsIgnoreCase(fil.getName())){
					return true;
				}
			}
		}
		return false;
	}
}