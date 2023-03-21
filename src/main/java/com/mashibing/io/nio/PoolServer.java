package com.mashibing.io.nio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// NIO多线程模型
// 单线程处理ssc接收，线程池处理handle
public class PoolServer {
    ExecutorService pool = Executors.newFixedThreadPool(50);



}
