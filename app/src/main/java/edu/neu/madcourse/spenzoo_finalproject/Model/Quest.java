package edu.neu.madcourse.spenzoo_finalproject.Model;

public class Quest {

    String questName;
    String questDescription;
    int code;
    int reward;
    // 0 = completed, 1 = incomplete, 2 = submitted
    int status;

    public Quest(String questName, String questDescription, int code, int reward) {
        this.questName = questName;
        this.questDescription = questDescription;
        this.code = code;
        this.reward = reward;
        this.status = 1;
    }

    public String getQuestName() {
        return questName;
    }

    public void setQuestName(String questName) {
        this.questName = questName;
    }

    public String getQuestDescription() {
        return questDescription;
    }

    public void setQuestDescription(String questDescription) {
        this.questDescription = questDescription;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatusByCode(int code, int status) {

    }
}
