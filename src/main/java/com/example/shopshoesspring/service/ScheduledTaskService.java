package com.example.shopshoesspring.service;

import com.example.shopshoesspring.entity.Delayed;
import com.example.shopshoesspring.repository.DelayedRepository;
import com.example.shopshoesspring.util.NetWorkHashScanner;
import com.example.shopshoesspring.util.SMBScanner;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ScheduledTaskService {

    @Autowired
    private DelayedRepository delayedRepository;

    @Qualifier("applicationTaskExecutor")
    @Autowired
    private  TaskExecutor taskExecutor;
    @Autowired
    private NetWorkHashScanner netWorkHashScanner;
    @Autowired
    private SMBScanner smbScanner;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void executeScheduledTasks() {
        List<Delayed> tasksToExecute = delayedRepository.findAll();
        for(Delayed delayed : tasksToExecute) {
            if(delayed.getScanDate().isAfter(Instant.now())){
                delayedRepository.deleteById(delayed.getId());
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(delayed.getSubnet().equals("EMPTY")){
                           List<String> strings = smbScanner.scanNetwork(delayed.getIpSmb());
                           for(String string : strings){
                               netWorkHashScanner.scanHash(string, delayed.getLogin(),delayed.getPassword(),delayed.getPath());
                           }
                        }else {
                            netWorkHashScanner.scanHash(delayed.getSubnet(),delayed.getLogin(),delayed.getPassword(),delayed.getPath());
                        }
                    }
                });
            }
        }
    }
}
