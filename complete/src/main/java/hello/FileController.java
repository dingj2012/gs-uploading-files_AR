package hello;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileController {
	
	private MyServletContext myServletContext; 
	
	@RequestMapping(method = RequestMethod.GET, value = "/upload")
	public String provideUploadInfo(Model model) {
		
		File rootFolder = myServletContext.getabsoluteDiskPath();
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
			redirectAttributes.addFlashAttribute("message", 
										"Folder separators or Relative pathnames not allowed");
			return "redirect:upload";
		}
		
		if (!file.isEmpty()) {
			try { 
				File fileinPath = myServletContext.getFileinDiskPath(name); 
				file.transferTo(fileinPath);
				redirectAttributes.addFlashAttribute("message",
						"You successfully uploaded " + name + "to the file system!"+"--path:"+fileinPath.getPath());
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
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/download")
	public String handleFileDownload(@RequestParam("name") String name,
								   @RequestParam("rename") String rename,
								   RedirectAttributes redirectAttributes) {
									   
		if ((name.contains("/")) || (rename.contains("/"))) {
			redirectAttributes.addFlashAttribute("message", 
										"Folder separators or Relative pathnames not allowed");
			return "redirect:upload";
		}
		
		if (!name.isEmpty()) {
			try {
				File filepath = myServletContext.getabsoluteDiskPath();
				//check if the file is in the disk
				if(findFile(name,filepath)){
					String newname = name; 
					if (!rename.isEmpty()){ 
						newname = rename;
					}
					else {
						//duplicated name
						newname = name +"-Copy";
					} 
					FileCopyUtils.copy(myServletContext.getFileinDiskPath(name),
									   myServletContext.getFileinDiskPath(newname);
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
}
