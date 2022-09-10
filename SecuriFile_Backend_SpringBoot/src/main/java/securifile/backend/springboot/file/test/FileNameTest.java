package securifile.backend.springboot.file.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * used to test the difference between getName and getOriginalFileName in MULTIPARTFILE
 * getName returns the name passed in the request
 * getOriginalFileName returns the actual name of the file as it is saved
 */
@RestController
@RequestMapping("testmultipartfile")
public class FileNameTest {
    @PostMapping(path = "filename")
    public void upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        System.out.println(multipartFile.getOriginalFilename() + "     ++++++++++   " + multipartFile.getResource().getFile().getPath());


    }

}
