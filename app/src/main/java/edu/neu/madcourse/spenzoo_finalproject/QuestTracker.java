package edu.neu.madcourse.spenzoo_finalproject;

import android.app.Application;

import java.util.HashSet;
import java.util.Set;

public class QuestTracker extends Application {

    private boolean dailyLogIn = false;
    private boolean dailyShake = false;
    private boolean isExpensesCompleted = false;
    private boolean isCategoriesCompleted = false;
    private boolean isBudgetCompleted = false;
    private boolean isOneDecorationCompleted = false;
    private boolean isThreeDecorationCompleted = false;
    private boolean isFiveDecorationCompleted = false;
    private int expensesRecorded = 0;
    private int decorationsOwned = 0;
    private Set<String> categoriesSet = new HashSet<>();

    private static QuestTracker questTracker = new QuestTracker();

//    public QuestTracker() {
//        this.dailyLogIn = false;
//        this.dailyShake = false;
//        this.isExpensesCompleted = false;
//        this.isCategoriesCompleted = false;
//        this.isBudgetCompleted = false;
//        this.isOneDecorationCompleted = false;
//        this.isThreeDecorationCompleted = false;
//        this.isFiveDecorationCompleted = false;
//        this.expensesRecorded = 0;
//        this.decorationsOwned = 0;
//        this.categoriesSet = new HashSet<>();
//    }

    public static QuestTracker getInstance() {
        return questTracker;
    }

    public boolean isDailyLogIn() {
        return dailyLogIn;
    }

    public void setDailyLogIn(boolean dailyLogIn) {
        this.dailyLogIn = dailyLogIn;
    }

    public boolean isDailyShake() {
        return dailyShake;
    }

    public void setDailyShake(boolean dailyShake) {
        this.dailyShake = dailyShake;
    }

    public boolean isExpensesCompleted() {
        return isExpensesCompleted;
    }

    public void setExpensesCompleted(boolean expensesCompleted) {
        isExpensesCompleted = expensesCompleted;
    }

    public boolean isCategoriesCompleted() {
        return isCategoriesCompleted;
    }

    public void setCategoriesCompleted(boolean categoriesCompleted) {
        isCategoriesCompleted = categoriesCompleted;
    }

    public boolean isBudgetCompleted() {
        return isBudgetCompleted;
    }

    public boolean isOneDecorationCompleted() {
        return isOneDecorationCompleted;
    }

    public void setOneDecorationCompleted(boolean oneDecorationCompleted) {
        isOneDecorationCompleted = oneDecorationCompleted;
    }

    public boolean isThreeDecorationCompleted() {
        return isThreeDecorationCompleted;
    }

    public void setThreeDecorationCompleted(boolean threeDecorationCompleted) {
        isThreeDecorationCompleted = threeDecorationCompleted;
    }

    public boolean isFiveDecorationCompleted() {
        return isFiveDecorationCompleted;
    }

    public void setFiveDecorationCompleted(boolean fiveDecorationCompleted) {
        isFiveDecorationCompleted = fiveDecorationCompleted;
    }

    public int getExpensesRecorded() {
        return expensesRecorded;
    }

    public void recordNewExpense() {
        expensesRecorded += 1;
        if (expensesRecorded >= 3) {
            setExpensesCompleted(true);
        }
    }

    public int getCategoriesSetSize() {
        return categoriesSet.size();
    }

    public void addCategoriesSet(String category) {
        categoriesSet.add(category);
        if (getCategoriesSetSize() >= 2) {
            setCategoriesCompleted(true);
        }
    }

    public int getDecorationsOwned() {
        return decorationsOwned;
    }

    public void acquireNewDecoration() {
        decorationsOwned += 1;
        if (decorationsOwned >= 1) {
            setOneDecorationCompleted(true);
        }
        if (decorationsOwned >= 3) {
            setThreeDecorationCompleted(true);
        }
        if (decorationsOwned >= 5) {
            setFiveDecorationCompleted(true);
        }
    }

}
