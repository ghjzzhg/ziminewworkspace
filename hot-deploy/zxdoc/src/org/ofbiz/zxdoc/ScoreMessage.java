package org.ofbiz.zxdoc;

import java.io.Serializable;

/**
 * Created by galaxypan on 16.9.29.
 */
public class ScoreMessage implements Serializable{

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件由谁产生，userLoginId
     */
    private String scoreSource;

    /**
     * 积分在哪个账户上计算:userLoginId, 如果为空，eventSource;
     */
    private String scoreTarget;

    /**
     * 业务定义的积分变化值，例如悬赏积分
     */
    private int bizScoreOnSource;

    /**
     * 业务定义的积分变化值，例如悬赏积分
     */
    private int bizScoreOnTarget;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getScoreSource() {
        return scoreSource;
    }

    public void setScoreSource(String scoreSource) {
        this.scoreSource = scoreSource;
    }

    public String getScoreTarget() {
        return scoreTarget;
    }

    public void setScoreTarget(String scoreTarget) {
        this.scoreTarget = scoreTarget;
    }

    public int getBizScoreOnSource() {
        return bizScoreOnSource;
    }

    public void setBizScoreOnSource(int bizScoreOnSource) {
        this.bizScoreOnSource = bizScoreOnSource;
    }

    public int getBizScoreOnTarget() {
        return bizScoreOnTarget;
    }

    public void setBizScoreOnTarget(int bizScoreOnTarget) {
        this.bizScoreOnTarget = bizScoreOnTarget;
    }
}
