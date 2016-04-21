package hello;

import java.util.*;
import java.io.File;

public boolean findFile(String name,File file) {
	File[] list = file.listFiles();
	if (!name.isEmpty() && !file.isEmpty()){
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
	} else {
		System.out.println("FILE NOT ILLEGAL, STOP!");
	}
}