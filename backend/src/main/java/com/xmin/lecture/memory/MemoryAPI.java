package com.xmin.lecture.memory;

import com.sun.jna.Memory;
import com.xmin.lecture.service.Assistant;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.MemoryId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/mem")
@RequiredArgsConstructor
@RestController
public class MemoryAPI {

    final ChatLanguageModel chatLanguageModel;

    private final ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

    private final Map<String,ChatMemory> chatMemoryMap = new ConcurrentHashMap<>();

    @GetMapping("/low/chat")
    public String chat(@RequestParam(value = "message") String message) {

        chatMemory.add(UserMessage.userMessage(message));
        ChatResponse response = chatLanguageModel.chat(chatMemory.messages());
        chatMemory.add(response.aiMessage());
        return response.aiMessage().text();

    }

    final Assistant assistant;

    @GetMapping("/high/chat")
    public String highChat(@RequestParam(value = "memoryId") String memoryId, @RequestParam(value = "message") String message) {
        return assistant.chat(memoryId, message);
    }

    @GetMapping("/high/chat2")
    public String highChat2(@RequestParam(value = "memoryId") String memoryId, @RequestParam(value = "message") String message) {
        if(!chatMemoryMap.containsKey(memoryId)) {
            MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory.withMaxMessages(10);
            messageWindowChatMemory.add(UserMessage.userMessage(message));
            chatMemoryMap.put(memoryId, messageWindowChatMemory);
        }
        List<ChatMessage> messages = chatMemoryMap.get(memoryId).messages();
        ChatResponse chat = chatLanguageModel.chat(messages);
        chatMemoryMap.get(memoryId).add(chat.aiMessage());
        return chat.aiMessage().text();

    }
}
