package com.nabob.conch.langchain.rag;

import com.nabob.conch.langchain.service.Assistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
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

@RequestMapping("/api/chat/rag")
@RestController
public class RAGAPI {

    @Resource
    private ChatModel chatModel;

    private Assistant easyRAGAssistant;

    @PostConstruct
    public void init() {
        // test easy RAG

        System.out.println("try to load document");
        // 1.将各种文档解析为Document对象  DocumentParser -> ApacheTikaDocumentParser   Apache Tika 库支持各种文档类型，用于检测文档类型并对其进行解析
        //      内置以下几种读取文件的方式：https://docs.langchain4j.dev/category/document-loaders/
        //          1. FileSystemDocumentLoader ： 根据文件绝对路径来读取
        //          2. ClassPathDocumentLoader  ： 根据classpath来读取
        //          3. UrlDocumentLoader ： 根据提供的url来读取
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("D:\\testdoc");
        System.out.println("load document success, file size: " + documents.size());

        // 2.定义一个内存向量数据库，用于存储embedding处理后的文档
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        System.out.println("try to split documents and embedding them into embeddingStore");
        // 3. EmbeddingStoreIngestor 使用 DocumentSplitter 将每个 Document 对象 分割成更小的片段（TextSegment），每个片段由不超过 300 个token组成，并支持 30 个token重叠（保持语义的）
        // 4. EmbeddingStoreIngestor 加载一个 EmbeddingModel 将每个 TextSegment 转换为 Embedding
        // NOTE: 我们选择 bge-small-en-v1.5 作为 Easy RAG 的默认嵌入模型。它在 MTEB 排行榜上取得了令人印象深刻的成绩，其量化版本仅占用 24 兆空间。因此，我们可以轻松地将其加载到内存中，并使用 ONNX 插件在同一进程中运行它。
        // 最终：所有 TextSegment-Embedding Pairs 都存储在 EmbeddingStore 中
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
        System.out.println("split documents and embedding them success");

        /*
            EmbeddingStoreIngestor:
                private final DocumentTransformer documentTransformer;
                private final DocumentSplitter documentSplitter;
                private final TextSegmentTransformer textSegmentTransformer;
                // Embedding模型
                private final EmbeddingModel embeddingModel;
                // Embedding向量存储 ： 也可以存在redis中 https://docs.langchain4j.dev/tutorials/embedding-stores
                private final EmbeddingStore<TextSegment> embeddingStore;
         */

        /*  DocumentSplitter:
            用于对需要写入向量数据库的文本进行一定策略上的切分，可以让rag的检索效果更好，如果需要自定义Spliter策略的话可以实现DocumentSplitter接口，以下 是langchain4j内置提供的一些切分策略
                1. DocumentByCharacterSplitter  基于指定的字符进行切分
                2. DocumentByLineSplitter  基于行切分
                3. DocumentByParagraphSplitter 基于段落切分，默认的切分方式
                4. DocumentByRegexSplitter 基于正则进行切分
                5. DocumentBySentenceSplitter 基于语义进行切分，需要依赖语义切分的模型
                6. DocumentByWordSplitter  基于单词进行切分

            文件拆分的常用参数
                maxSegmentSizeInChars : 每个文本段最大的长度
                maxOverlapSizeInChars ：两个段之间重叠的数量

            Demo:
                EmbeddingStoreIngestor.builder()
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .documentSplitter(new DocumentByLineSplitter(100, 30))
                    .build().ingest(documents);
         */

        easyRAGAssistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();
    }

    /**
     * Easy RAG
     */
    @GetMapping("/easy/rag")
    public String easyRAG(@RequestParam(value = "message") String message) {
        return easyRAGAssistant.chat(message);
    }

}
