package com.bigblue.scheduler.domain;

import com.bigblue.scheduler.base.enums.TaskStatus;
import com.bigblue.scheduler.service.INodeTask;
import com.bigblue.scheduler.test.MyNodeTask;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @Author: TheBigBlue
 * @Description: 任务信息，不同类型的任务需各自实现
 * @Date: 2020/6/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class NodeTask implements Callable<TaskResult>, INodeTask {

    private static Logger logger = LoggerFactory.getLogger(MyNodeTask.class);

    private String id;
    //依赖的nodeTask id
    private Set<String> dependences = Sets.newConcurrentHashSet();
    //当前Task运行状态
    private TaskStatus taskStatus = TaskStatus.init;
    //当前Task运行结果
    private TaskResult taskResult;
    //任务类型 TODO
    private String type;
    //task元数据：可以是Json或其他  TODO
    private Map<String, Object> metadata;
    //元数据解析器 TODO
    private String metadataParserKey;
    //依赖节点的运行结果
    private Map<String, TaskResult> dependencesResult;
    //最长运行时间, -1:表示无时间限制 TODO
    private long maxRuntimeInSec = -1;

    public NodeTask(String id) {
        this.id = id;
    }

    /**
     * @param id          nodeTaskId
     * @param dependences 依赖
     */
    public NodeTask(String id, Set<String> dependences) {
        this.id = id;
        this.dependences = dependences;
    }

    public NodeTask(String id, Set<String> dependences, String type, Map<String, Object> metadata) {
        this.id = id;
        this.dependences = dependences;
        this.type = type;
        this.metadata = metadata;
    }

    /**
     * 多线程调度，子类需覆盖此方法
     * @throws Exception
     */
    @Override
    public TaskResult call() throws Exception {
        //根据Type的不同，调用不同类去处理 TODO

        //捕获异常？TODO
        long startTime = System.currentTimeMillis();
        logger.info("begin to run task: {}, timestamp: {}", this.id, startTime);
        this.taskResult = doTask();
        logger.info("finish task: {}, cost time: {}ms", this.id, System.currentTimeMillis() - startTime);
        return taskResult;
    }
}