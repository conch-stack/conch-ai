package com.xmin.lecture.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Assistant {

    //@SystemMessage("假如你是特朗普，接下来请以特朗普的语气来对话")
    String chat(String message);

    String chat(@MemoryId String memoryId, @UserMessage String message);

}
