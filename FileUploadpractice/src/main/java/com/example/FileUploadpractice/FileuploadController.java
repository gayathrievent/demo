package com.example.FileUploadpractice;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/files")
public class FileuploadController {
	
	private final Path uploadDir=Paths.get("fileuploads"); // define the folder name as a fileuploads in our project root
	
	public FileuploadController() throws IOException { // using constructor check the upload folder is exists or not 
		 
		if(!Files.exists(uploadDir)) // Check the Files is not exists.
		{
			Files.createDirectories(uploadDir); // suppose the folder is not there it will create the upload folder.
		}
	}
	
	
	@PostMapping("/upload")
	public ResponseEntity<String> UploadFile(@RequestParam("file") MultipartFile file) throws IOException  
	{
		Path filePath=uploadDir.resolve(file.getOriginalFilename()); // define the filePath  from the upload directory.
		Files.copy(file.getInputStream(), filePath , StandardCopyOption.REPLACE_EXISTING); // check the file name already there replace the content .
		return ResponseEntity.ok("File uploaded Successsfully: "  + file.getOriginalFilename());
	}

	
	
	@GetMapping("/download/{filename:.+}")
	public ResponseEntity<UrlResource> DownloadFile(@PathVariable String filename) throws IOException
	{
		
		Path filePath =uploadDir.resolve(filename).normalize(); // define the Path to download
		UrlResource resource= new  UrlResource(filePath.toUri()); // define the URL to check using the filePath
		
		if(!resource.exists())
		{
			return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // suppose the URL is incorrect it will return the 404 error.
			
		}
		
		
		String contentType=Files.probeContentType(filePath); // This tries to automatically detect the file's content type (e.g., image/png, application/pdf, text/plain) based on its extension or content.
		if(contentType==null)
		{
			contentType="application/octet-stream";   //This is a generic binary stream type . "I don't know what this is, just download it as a file."
		}
		
		
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
		
		// Sets the content type (so browser knows what kind of file it is).

        //Content-Disposition makes it downloadable (as an attachment).

        //Returns the actual file as the response body.
	}
}



/*
 public ResponseEntity<UrlResource> DownloadFile(@PathVariable String filename) throws IOException ----why PathVariable is String -File names are typically text (e.g., "document.pdf", "image.png").

 .normalize(): Cleans up the path by removing any redundant path elements like . or .. to avoid path traversal issues.
 
 What is MultipartFile?
 
 MultipartFile is a Spring Framework interface used to handle file uploads in HTTP requests.
 
 file.getOriginalFilename() – Get the original name of the file.

file.getSize() – Get the file size.

file.getInputStream() – Read the content.

file.getBytes() – Read content as byte array.

file.getContentType() – Get MIME type (like image/jpeg


MultipartFile makes it easy to:

Accept file uploads

Access the file content

Store it or process it


File is Predefined class? 

Yes, Files is a predefined utility class in Java provided by the java.nio.file package. It contains static methods for file and directory operations such as:

Files.exists(path) → checks if a file/directory exists

Files.createDirectories(path) → creates a directory and its parents if not present

Files.copy(inputStream, targetPath, option) → copies file content

Files.probeContentType(path) → detects MIME type (e.g., image/jpeg)
*/