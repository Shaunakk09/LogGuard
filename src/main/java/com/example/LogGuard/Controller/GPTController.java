package com.example.LogGuard.Controller;

import com.example.LogGuard.Model.GPTRequest;
import com.example.LogGuard.Model.GPTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/gpt")
public class GPTController {
    @Value("${openai.model}")
    private String model;

    @Value(("${openai.api.url}"))
    private String apiURL;

    @Autowired
    private RestTemplate template;
    @GetMapping("/chat/completions")
    public String chat(@RequestParam("prompt") String prompt){
        GPTRequest request=new GPTRequest(model, prompt);
        GPTResponse chatGptResponse = template.postForObject(apiURL, request, GPTResponse.class);
        return chatGptResponse.getChoices().get(0).getMessage().getContent();
    }
}