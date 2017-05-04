package com.zx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;


@SpringBootApplication
public class ThreadApplication {

	public static void main(String[] args) {
//		AbstractQueuedSynchronizer
		SpringApplication.run(ThreadApplication.class, args);
	}
}
