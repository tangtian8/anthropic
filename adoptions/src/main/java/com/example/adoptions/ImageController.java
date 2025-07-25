package com.example.adoptions;
//import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author tangtian
 * @date 2025-07-18 11:17
 */
@Controller
@ResponseBody
public class ImageController {
//	@Autowired
//	private ImageModel imageModel;


//	@GetMapping("/{user}/image/assistant")
//	String image(@PathVariable String user, @RequestParam String question) {
//		var options = ImageOptionsBuilder.builder().height(1024).width(1024).build();
//		ImagePrompt imagePrompt = new ImagePrompt(question, options);
//		ImageResponse imageResponse = this.imageModel.call(imagePrompt);
//		return imageResponse.getResult().getOutput().getUrl();
//	}
}
