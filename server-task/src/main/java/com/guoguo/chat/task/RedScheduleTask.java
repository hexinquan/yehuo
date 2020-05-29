package com.guoguo.chat.task;

import com.guoguo.chat.repository.AccountRecordRepository;
import com.guoguo.chat.repository.RedPacketRepository;
import com.guoguo.chat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

@Slf4j
@Configuration
@EnableScheduling
public class RedScheduleTask implements SchedulingConfigurer {

    @Autowired
    RedPacketRepository redPacketRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRecordRepository accountRecordRepository;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 执行定时任务
     * @param scheduledTaskRegistrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {

        scheduledTaskRegistrar.addTriggerTask(
                    //1.添加任务内容(Runnable)
                    () -> new RedPacketRollbackRunnable(redPacketRepository,userRepository,accountRecordRepository,redisTemplate).run(),
                    //2.设置执行周期(Trigger)
                    triggerContext -> {
//
                        return new CronTrigger("0/1 * * * * ?").nextExecutionTime(triggerContext);
                    }
            );
    }
}
