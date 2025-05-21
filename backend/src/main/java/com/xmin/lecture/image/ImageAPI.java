package com.xmin.lecture.image;

import com.xmin.lecture.service.Assistant;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
public class ImageAPI {


    final ChatLanguageModel chatLanguageModel;

    @GetMapping("/high/chat")
    public String highChat(@RequestParam(value = "message") String message) throws IOException {

        File file = new File("D:\\lecture\\lecture-langchain\\documents\\cat.png");
        byte[] bytes = Files.readAllBytes(file.toPath());

        UserMessage userMessage = UserMessage.from(TextContent.from(message),
            ImageContent.from(Base64.getEncoder().encodeToString(bytes), "image/png"));
        return chatLanguageModel.chat(List.of(userMessage)).aiMessage().text();
    }
}
