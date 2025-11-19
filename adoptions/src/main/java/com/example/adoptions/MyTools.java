package com.example.adoptions;

import org.springframework.ai.tool.annotation.Tool;

/**
 * @author tangtian
 * @date 2025-07-26 10:29
 */
public class MyTools {
	@Tool(description = "我想吃鱼了")
	public void eatFish(){
		System.out.println("我想吃鱼了");
	}
}
