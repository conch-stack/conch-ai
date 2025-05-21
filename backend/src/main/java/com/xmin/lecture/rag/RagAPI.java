package com.xmin.lecture.rag;

import com.xmin.lecture.service.Assistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/rag")
@RequiredArgsConstructor
@RestController
public class RagAPI {


    final Assistant assistant;

    @GetMapping("/high/chat")
    public String lowChat(@RequestParam(value = "message") String message) {
        return assistant.chat(message);
    }


    final EmbeddingStore<TextSegment> embeddingStore;

    final EmbeddingModel embeddingModel;

    @GetMapping("/load")
    public String load(){
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("D:\\lecture\\lecture-langchain\\documents");
       // EmbeddingStoreIngestor.ingest(documents,embeddingStore);
        EmbeddingStoreIngestor.builder().embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .documentSplitter(new DocumentByLineSplitter(30,20))
            .build().ingest(documents);

        ;
        return "success";
    }

}
