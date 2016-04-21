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
	
	private servletContext sContext; 
	
	@RequestMapping(method = RequestMethod.GET, value = "/upload")
	public String provideUploadInfo(Model model) {
		
		File rootFolder = new File(sContext.getabsoluteDiskPath());
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
				//Store the uploaded file content in filesystem 
				File filepath = new File(sContext.getabsoluteDiskPath()+ "/" + name); 
				file.transferTo(filepath);
				redirectAttributes.addFlashAttribute("message",
						"You successfully uploaded " + name + "to the file system!"+"--path:"+filepath.getPath());
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
				File filepath = new File(sContext.getabsoluteDiskPath());
				if(findFile(name,filepath)){
					String newname = name; 
					if (!rename.isEmpty()){ newname = rename;}
					else { newname = name +"-Copy";}
					FileCopyUtils.copy(new File(sContext.getabsoluteDiskPath() + "/" + name),
									   new File(sContext.getabsoluteDiskPath() + "/" + newname));
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
