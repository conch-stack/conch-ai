package com.nabob.conch.langchain.memory;

import com.nabob.conch.langchain.service.Assistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/api/chat/memory")
@RestController
public class ChatMemoryAPI {

    @Resource
    private ChatModel chatModel;

    private Assistant assistant;

    private final static ChatMemory chatMem = MessageWindowChatMemory.withMaxMessages(20);

    private final Map<String,ChatMemory> chatMemoryMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        this.assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @GetMapping("/low")
    public String chatWithLangchain(
            @RequestParam(value = "message") String message) {
        chatMem.add(UserMessage.from(message));
        ChatResponse chat = chatModel.chat(chatMem.messages());
        chatMem.add(chat.aiMessage());
        return chat.aiMessage().text();
    }

    @GetMapping("/high")
    public String highChat(@RequestParam(value = "memoryId") String memoryId, @RequestParam(value = "message") String message) {
        return assistant.chat(memoryId, message);
    }

    @GetMapping("/high2")
    public String highChat2(@RequestParam(value = "memoryId") String memoryId, @RequestParam(value = "message") String message) {
        if(!chatMemoryMap.containsKey(memoryId)) {
            MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory.withMaxMessages(10);
            messageWindowChatMemory.add(UserMessage.userMessage(message));
            chatMemoryMap.put(memoryId, messageWindowChatMemory);
        }
        List<ChatMessage> messages = chatMemoryMap.get(memoryId).messages();
        ChatResponse chat = chatModel.chat(messages);
        chatMemoryMap.get(memoryId).add(chat.aiMessage());
        return chat.aiMessage().text();

    }
}
