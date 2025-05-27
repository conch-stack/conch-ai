package com.nabob.conch.langchain.chat;

import com.nabob.conch.langchain.chat.service.Assistant;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dev.langchain4j.model.LambdaStreamingResponseHandler.onPartialResponse;
import static dev.langchain4j.model.LambdaStreamingResponseHandler.onPartialResponseAndError;

import java.util.List;

/**
 * 对话能力
 * <pre>
 * - ChatModel
 *      这些接受多个ChatMessage作为输入，并返回一个单独的AiMessage作为输出。ChatMessage通常包含文本，但一些LLMs也支持其他模态（例如，图像、音频等）。此类聊天模型的例子包括OpenAI的gpt-4o-mini和Google的gemini-1.5-pro
 *
 * - UserMessage：
 *      这是一条来自用户的消息。用户可以是您应用程序的最终用户（人类）或者您的应用程序本身。根据LLM支持的模态，用户消息可以包含纯文本（字符串）或其他模态。
 * - AiMessage：
 *      这是一条由AI生成的消息，通常是对用户消息的响应。如您所注意到的，generate方法返回的是一个包含在Response中的AI消息。AI消息可以包含文本响应（字符串）或执行工具的请求（ToolExecutionRequest）。我们将在另一节中探讨工具。
 * - ToolExecutionResultMessage：
 *      这是ToolExecutionRequest的结果。
 * - SystemMessage：
 *      这是一条来自系统的消息。通常，您作为开发者应该定义这条消息的内容。
 *      通常，您会在这里写下关于LLM在这次对话中的角色、它应该如何表现、应该以何种风格回答等的说明。LLM被训练成比其他类型的消息更关注系统消息，所以请小心，最好不要让最终用户自由定义或注入某些输入到系统消息中。通常，它位于对话的开始。
 * - CustomMessage：
 *      这是一种可以包含任意属性的自定义消息。这种消息类型只能由支持它的ChatModel实现使用（目前只有Ollama）
 *
 * - TokenUsage：
 *      ChatResponse also contains ChatResponseMetadata. ChatResponseMetadata contains TokenUsage,
 *      which contains stats about how many tokens the input (all the ChatMessages that you provided to the generate method) contained,
 *      how many tokens were generated as output (in the AiMessage), and the total (input + output)
 *
 * - FinishReason：
 *      which is an enum with various reasons why generation has stopped. Usually, it will be FinishReason.STOP, if the LLM decided to stop generation itself.
 *
 * - Multiple ChatMessages：
 *      大模型是无状态的，如果需要进行多轮对话，就需要管理对话信息
 *
 * - ChatMemory:
 *      手动管理多个messages很繁琐（cumbersome）, 所以利用ChatMemory进行管理
 *
 * - MultiModality：多模态
 *      UserMessage 可以包含多种类型的内容：
 *          TextContent
 *          ImageContent：  图片可以是链接，也可以是Base64-encoded二进制数据，这取决于你底层的大模型能力
 *          AudioContent：  与ImageContent相似
 *          VideoContent：  与ImageContent相似
 *          PdfFileContent：  与ImageContent相似
 *
 * </pre>
 */
@RequestMapping("/api")
@RestController
public class ChatAPI {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    private Assistant assistant;

    @PostConstruct
    public void init() {
        this.assistant = AiServices.create(Assistant.class, chatModel);
    }

    /**
     * Low Level API
     */
    @GetMapping("/low/chat")
    public String lowChat(@RequestParam(value = "message") String message) {
//        return chatModel.chat(UserMessage.from(message)).aiMessage().text();

//        ChatRequest chatRequest = ChatRequest.builder().messages(new ChatMessage[]{UserMessage.from(userMessage)}).build();

//        ChatRequest chatRequest = ChatRequest.builder()
//                .messages(...)
//                .modelName(...)
//                .temperature(...)
//                .topP(...)
//                .topK(...)
//                .frequencyPenalty(...)
//                .presencePenalty(...)
//                .maxOutputTokens(...)
//                .stopSequences(...)
//                .toolSpecifications(...)
//                .toolChoice(...)
//                .responseFormat(...)
//                .parameters(...) // you can also set common or provider-specific parameters all at once
//                .build();

        // Multiple ChatMessages 大模型是无状态的，如果需要进行多轮对话，就需要管理对话信息
//        UserMessage firstUserMessage = UserMessage.from("Hello, my name is Klaus");
//        AiMessage firstAiMessage = chatModel.chat(firstUserMessage).aiMessage(); // Hi Klaus, how can I help you?
//        UserMessage secondUserMessage = UserMessage.from("What is my name?");
//        AiMessage secondAiMessage = chatModel.chat(firstUserMessage, firstAiMessage, secondUserMessage).aiMessage(); // Klaus
//        return secondAiMessage.text();

        // MultiModality 多模态  ImageContent 图片可以是链接，也可以是Base64-encoded二进制数据，这取决于你底层的大模型能力
//        UserMessage userMessage = UserMessage.from(
//                TextContent.from("Describe the following image"),
//                ImageContent.from("https://example.com/cat.jpg")
//        );
//        ChatResponse response = chatModel.chat(userMessage);

//        ChatMemory chatMemory = MessageWindowChatMemory.builder()
//                .id("12345")
//                .maxMessages(10)
//                .chatMemoryStore(new PersistentChatMemoryStore())
//                .build();

//        String userMessage = "Tell me a joke";
//        streamingChatModel.chat(userMessage, new StreamingChatResponseHandler() {
//
//            @Override
//            public void onPartialResponse(String partialResponse) {
//                System.out.println("onPartialResponse: " + partialResponse);
//            }
//
//            @Override
//            public void onCompleteResponse(ChatResponse completeResponse) {
//                System.out.println("onCompleteResponse: " + completeResponse);
//            }
//
//            @Override
//            public void onError(Throwable error) {
//                error.printStackTrace();
//            }
//        });
//        streamingChatModel.chat("Tell me a joke", onPartialResponse(System.out::print));
//        streamingChatModel.chat("Tell me a joke", onPartialResponseAndError(System.out::print, Throwable::printStackTrace));

        // 用户Message 系统Message
        return chatModel.chat(SystemMessage.from("假如你是特朗普，接下来请以特朗普的语气来对话"), UserMessage.from(message)).aiMessage().text();
//        return "success";
    }

    /**
     * High Level API
     */
    @GetMapping("/high/chat")
    public String highChat(@RequestParam(value = "message") String message) {
        return assistant.chat(message);
    }


    // private

    class PersistentChatMemoryStore implements ChatMemoryStore {

        @Override
        public List<ChatMessage> getMessages(Object memoryId) {
            // TODO: Implement getting all messages from the persistent store by memory ID.
            // ChatMessageDeserializer.messageFromJson(String) and
            // ChatMessageDeserializer.messagesFromJson(String) helper methods can be used to
            // easily deserialize chat messages from JSON.
            return null;
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            // TODO: Implement updating all messages in the persistent store by memory ID.
            // ChatMessageSerializer.messageToJson(ChatMessage) and
            // ChatMessageSerializer.messagesToJson(List<ChatMessage>) helper methods can be used to
            // easily serialize chat messages into JSON.
        }

        @Override
        public void deleteMessages(Object memoryId) {
            // TODO: Implement deleting all messages in the persistent store by memory ID.
        }
    }

}
