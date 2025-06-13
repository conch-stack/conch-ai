package com.nabob.conch.langchain.websearch;

import com.nabob.conch.langchain.service.Assistant;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.web.search.WebSearchTool;
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/web/search")
@RestController
public class WebSearchAPI {

    @Resource
    private ChatModel chatModel;

    private Assistant assistant;

    @PostConstruct
    public void init() {

        // 联网搜索能力
        // 首先需要注册 https://www.searchapi.io 并申请到API key
        SearchApiWebSearchEngine searchApiWebSearchEngine = initWebSearchEngine();

        this.assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .tools(new WebSearchTool(searchApiWebSearchEngine))
                .build();
    }

    public SearchApiWebSearchEngine initWebSearchEngine() {
        return SearchApiWebSearchEngine.builder()
                .engine("")
                .apiKey("")
                .build();
    }

}
