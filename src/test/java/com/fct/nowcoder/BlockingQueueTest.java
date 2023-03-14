package com.fct.nowcoder;

import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class BlockingQueueTest {
    public static void main(String[] args) {
        BlockingQueue blockingQueue = new ArrayBlockingQueue(10);

        new Thread(new Produce(blockingQueue)).start();
        new Thread(new Customer(blockingQueue)).start();
        new Thread(new Customer(blockingQueue)).start();
        new Thread(new Customer(blockingQueue)).start();

    }

    static class Produce implements Runnable{

        private BlockingQueue<Integer> blockingQueue;

        public Produce(BlockingQueue blockingQueue){
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            try{
                for(int i = 0; i < 100; i++){
                    Thread.sleep(20);
                    blockingQueue.put(i);
                    log.info("{}",Thread.currentThread().getName() + "生产" + blockingQueue.size());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class Customer implements Runnable{
        private BlockingQueue<Integer> blockingQueue;

        public Customer(BlockingQueue blockingQueue){
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(new Random().nextInt(1000));
                    blockingQueue.take();
                    log.info("{}",Thread.currentThread().getName() + "消费:" + blockingQueue.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
