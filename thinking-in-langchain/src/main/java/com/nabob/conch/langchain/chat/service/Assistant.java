package com.nabob.conch.langchain.chat.service;

/**
 * AI Service
 * 助手
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
