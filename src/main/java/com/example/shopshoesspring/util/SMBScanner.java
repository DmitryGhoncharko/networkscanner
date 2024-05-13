package com.example.shopshoesspring.util;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
@Component
public class SMBScanner {
    private static final int NUM_THREADS = 20;

    public List<String> scanNetwork(String subnet) {
        List<String> list = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 1; i <= 255; i++) {
            String ip = subnet + "." + i;
            executor.submit(() -> {
                try {
                    InetAddress address = InetAddress.getByName(ip);
                    if (address.isReachable(200)) {
                        if (isPortOpen(ip, 445)) {
                            list.add(ip);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static boolean isPortOpen(String ip, int port) {
        try (Socket ignored = new Socket(ip, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
