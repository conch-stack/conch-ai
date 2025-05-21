package com.xmin.lecture.json;

import com.xmin.lecture.service.Assistant;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/json")
@RequiredArgsConstructor
@RestController
public class JsonAPI {


    final Assistant assistant;

    final ChatLanguageModel chatLanguageModel;

    @GetMapping("/high/chat")
    public String highChat(@RequestParam(value = "message") String message) {
        PersonService personService = AiServices.create(PersonService.class, chatLanguageModel);
        Person person = personService.extractPerson(message);
        return person.toString();

    }


    @GetMapping("/low/chat")
    public String lowChat(@RequestParam(value = "message") String message) {

        ResponseFormat responseFormat = ResponseFormat.builder()
            .type(ResponseFormatType.JSON)
            .jsonSchema(JsonSchema.builder()
                .rootElement(JsonObjectSchema.builder()
                    .addIntegerProperty("age", "the person's age")
                    .addIntegerProperty("weight", "the person's weight")
                    .required("age", "weight")
                    .build())
                .build())
            .build();

        ChatResponse chat = chatLanguageModel.chat(ChatRequest.builder()
            .messages(List.of(UserMessage.from(message)))
            .parameters(ChatRequestParameters.builder()
                .responseFormat(responseFormat)
                .build())
            .build());
        return chat.aiMessage().text();
    }
}
