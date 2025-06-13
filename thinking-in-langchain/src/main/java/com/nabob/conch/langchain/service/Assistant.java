package com.nabob.conch.langchain.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Service
 * 助手
 *
 * 使用官方 SpringBoot Starter：https://docs.langchain4j.dev/tutorials/spring-boot-integration/#spring-boot-starter-for-declarative-ai-services
 *
 * dev.langchain4j.service
 */
public interface Assistant {

    // simple chat
    String chat(String message);

    @SystemMessage("假如你是特朗普，接下来请以特朗普的语气来对话")
    String chatWithSystemMessage(String message);

    // 利用MemoryId隔离回话
    String chat(@MemoryId String memoryId, @UserMessage String message);

}
