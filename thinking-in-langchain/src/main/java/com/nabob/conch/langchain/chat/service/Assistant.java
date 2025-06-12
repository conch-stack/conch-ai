package com.nabob.conch.langchain.chat.service;

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

//    //@SystemMessage("假如你是特朗普，接下来请以特朗普的语气来对话")
//    String chat(String message);
//
//    String chat(@MemoryId String memoryId, @UserMessage String message);

}
