package com.nabob.conch.langchain.chat;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对话能力
 */
@RequestMapping("/api")
@RestController
public class ChatAPI {

    @Resource
    private ChatModel chatModel;

    /**
     * Low Level API
     */
    @GetMapping("/low/chat")
    public String lowChat(@RequestParam(value = "message") String message) {
//        return chatModel.chat(UserMessage.from(message)).aiMessage().text();

        // 用户Message 系统Message
        return chatModel.chat(List.of(SystemMessage.systemMessage("假如你是特朗普，接下来请以特朗普的语气来对话"),
                UserMessage.userMessage(message)
        )).aiMessage().text();
    }

    /**
     * High Level API   todo
     */
}
