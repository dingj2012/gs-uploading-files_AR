package hello;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.context.ServletContextAware;

@Controller
public class FileUploadController implements ServletContextAware{
	
	private ServletContext servletContext;
	
	//The controller is not abstract, override the ServletContext supertype.
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/upload")
	public String provideUploadInfo(Model model) {
		
		//List the files in the filesystem
		File rootFolder = new File(servletContext.getRealPath("/"));
		//List the files in the memory of server
		//File rootFolder = new File(Application.ROOT);
		List<String> fileNames = Arrays.stream(rootFolder.listFiles())
			.map(f -> f.getName())
			.collect(Collectors.toList());

		model.addAttribute("files",
			Arrays.stream(rootFolder.listFiles())
					.sorted(Comparator.comparingLong(f -> -1 * f.lastModified()))
					.map(f -> f.getName())
					.collect(Collectors.toList())
		);
		return "uploadForm";
	}

	
	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public String handleFileUpload(@RequestParam("name") String name,
								   @RequestParam("file") MultipartFile file,
								   RedirectAttributes redirectAttributes) {
		if (name.contains("/")) {
			redirectAttributes.addFlashAttribute("message", "Folder separators not allowed");
			return "redirect:upload";
		}
		if (name.contains("/")) {
			redirectAttributes.addFlashAttribute("message", "Relative pathnames not allowed");
			return "redirect:upload";
		}
		
		if (!file.isEmpty()) {
			try {
				
				//Store the uploaded file content in filesystem 
				File filepath = new File(servletContext.getRealPath("/") + "/" + name); //absolute path 
				file.transferTo(filepath);
				redirectAttributes.addFlashAttribute("message",
						"You successfully uploaded " + name + "to the file system!"+"--path:"+filepath.getPath());
				
				
				//Store the uploaded file content in memory on the server 
				/*
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(Application.ROOT + "/" + name)));
                FileCopyUtils.copy(file.getInputStream(), stream);
				stream.close();
				redirectAttributes.addFlashAttribute("message",
						"You successfully uploaded " + name + "!");
				*/
				
			}
			catch (Exception e) {
				redirectAttributes.addFlashAttribute("message",
						"You failed to upload " + name + " => " + e.getMessage());
			}
		}
				
		else {
			redirectAttributes.addFlashAttribute("message",
					"You failed to upload " + name + " because the file was empty");
		}
		
		return "redirect:upload";
	}
	
	//This method handle the download, modified from the upload one 
	@RequestMapping(method = RequestMethod.POST, value = "/download")
	public String handleFileDownload(@RequestParam("name") String name,
								   @RequestParam("rename") String rename,
								   RedirectAttributes redirectAttributes) {
		if ((name.contains("/")) || (rename.contains("/"))) {
			redirectAttributes.addFlashAttribute("message", "Folder separators not allowed");
			return "redirect:upload";
		}
		if ((name.contains("/")) || (rename.contains("/"))) {
			redirectAttributes.addFlashAttribute("message", "Relative pathnames not allowed");
			return "redirect:upload";
		}
		
		if (!name.isEmpty()) {
			try {
				//Convert the relative web path to absolute disk path
				File filepath = new File(servletContext.getRealPath("/"));
				if(findFile(name,filepath)){
					String newname = name;
					//If the user did not give the rename, 
					//it will rename downloaded file as '...-Copy' to avoid duplicate
					if (!rename.isEmpty()){ newname = rename;}
					else { newname = name +"-Copy";}
					FileCopyUtils.copy(new File(servletContext.getRealPath("/") + "/" + name),
									   new File(servletContext.getRealPath("/") + "/" + newname));
					redirectAttributes.addFlashAttribute("message",
						"You successfully downloaded " + name + " to the file system!"+"--path:"+filepath.getPath());
				}
				else {
					redirectAttributes.addFlashAttribute("message",
						"No such file:" + name);
				}
				
			}
			catch (Exception e) {
				redirectAttributes.addFlashAttribute("message",
						"You failed to upload " + name + " => " + e.getMessage());
			}
		}
				
		else {
			redirectAttributes.addFlashAttribute("message",
					"Please enter the file's name you want to download");
		}
		
		return "redirect:upload";
	}
	
	//helper method to find the named file in the internal file system
	public boolean findFile(String name,File file)
    {
        File[] list = file.listFiles();
        if(list!=null) {
			for (File fil : list) {
				if (fil.isDirectory()){
					findFile(name,fil);
				}
				else if (name.equalsIgnoreCase(fil.getName())){
					return true;
				}
			}
		}
		return false;
    }
	

}
