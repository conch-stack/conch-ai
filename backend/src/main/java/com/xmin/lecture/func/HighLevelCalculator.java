package com.xmin.lecture.func;

import dev.langchain4j.agent.tool.Tool;

public class HighLevelCalculator {


    @Tool("计算两数之和")
    public int sum(int a, int b) {
        return a + b;
    }

    @Tool("计算两数之差")
    public int sub(int a, int b) {

        return a - b;
    }

}
