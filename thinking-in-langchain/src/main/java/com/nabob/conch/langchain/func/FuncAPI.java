package com.nabob.conch.langchain.func;

import com.nabob.conch.langchain.service.Assistant;
import com.nabob.conch.langchain.util.JsonUtil;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/func")
@RestController
public class FuncAPI {

    @Resource
    private ChatModel chatModel;

    private Assistant assistant;

    @PostConstruct
    public void init() {
        this.assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .tools(new HighLevelCalculator())
                .build();
    }

    @GetMapping("/low/chat")
    public String lowChat(@RequestParam(value = "message") String message) {

        ToolSpecification specifications = ToolSpecification.builder()
            .name("Calculator")
            .description("输入两个数，对这两个数求和")
            .parameters(JsonObjectSchema.builder()
                .addIntegerProperty("a","第一个参数")
                .addIntegerProperty("b","第二个参数")
                .required("a","b")
                .build())
            .build();

        ChatResponse chatResponse = chatModel.doChat(ChatRequest.builder()
            .messages(List.of(UserMessage.from(message)))
            .parameters(ChatRequestParameters.builder()
                .toolSpecifications(specifications)
                .build())
            .build());

        chatResponse.aiMessage().toolExecutionRequests().forEach(toolExecutionRequest -> {

            System.out.println(toolExecutionRequest.name());
            System.out.println(toolExecutionRequest.arguments());

            try {
                Class<?> aClass = Class.forName("com.xmin.lecture.func." + toolExecutionRequest.name());
                Runnable runnable = (Runnable) JsonUtil.toJsonObject(toolExecutionRequest.arguments(), aClass);
                runnable.run();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        return chatResponse.aiMessage().text();
    }

    @GetMapping("/high/chat")
    public String highChat(@RequestParam(value = "message") String message) {
        return assistant.chat(message);
    }

}
