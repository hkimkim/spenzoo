package edu.neu.madcourse.spenzoo_finalproject.Model;

import android.os.CountDownTimer;

import java.util.Calendar;
import java.util.Date;

import edu.neu.madcourse.spenzoo_finalproject.R;

public class Animal extends Piece{
    //private static final long INITIAL_TIME = 28800000; // 8hours
    private static final long INITIAL_TIME = 60000 ; // 8hours

    public enum AnimalType {
        DEFAULT, KOALA, RED_PANDA, ELEPHANT, FOX, PENGUIN
    }

    private String name;
    private Integer happinessLevel;
    private long dateOfAdoption;
    private String category;
    private Integer categoryIndex;
    private long lastLogTime;
    private Double totalExpenseForCategory;

    public Animal(AnimalType animalType) {
        this.name = "default";
        this.happinessLevel = 70;
        this.dateOfAdoption = System.currentTimeMillis();
        this.type = animalType.toString();
        this.category = Expense.Category.DEFAULT.name();
        this.categoryIndex = -1;
        this.imageSource = matchAnimalImage(animalType);
        this.xPosition = 0.0f;
        this.yPosition = 0.0f;
        this.lastLogTime = System.currentTimeMillis();
        this.totalExpenseForCategory = 0.00;
    }


    public Animal() {
        this.name = "default";
        this.happinessLevel = 70;
        this.dateOfAdoption = System.currentTimeMillis();
        this.type = AnimalType.DEFAULT.toString();
        this.categoryIndex = -1;
        this.category = Expense.Category.DEFAULT.name();
        this.imageSource = -1;
        this.xPosition = 0.0f;
        this.yPosition = 0.0f;
        this.lastLogTime = System.currentTimeMillis();
        this.totalExpenseForCategory = 0.00;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageSource(Integer imageSource) {
        this.imageSource = imageSource;
    }

    public Integer getHappinessLevel() {
        return happinessLevel;
    }

    public long getDateOfAdoption() {
        return dateOfAdoption;
    }

    public void setDateOfAdoption(long dateOfAdoption) {
        this.dateOfAdoption = dateOfAdoption;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCategoryIndex() {
        return categoryIndex;
    }

    public void setCategoryIndex(Integer categoryIndex) {
        this.categoryIndex = categoryIndex;
    }

    public Float getxPosition() {
        return xPosition;
    }

    public void setxPosition(Float xPosition) {
        this.xPosition = xPosition;
    }

    public Float getyPosition() {
        return yPosition;
    }

    public void setyPosition(Float yPosition) {
        this.yPosition = yPosition;
    }

    public long getLastLogTime() {
        return lastLogTime;
    }

    public Double getTotalExpenseForCategory() {
        return totalExpenseForCategory;
    }

    public void setTotalExpenseForCategory(Double totalExpenseForCategory) {
        this.totalExpenseForCategory = totalExpenseForCategory;
    }

    private Integer matchAnimalImage(AnimalType animalType) {
        switch (animalType) {
            case PENGUIN:
                return R.drawable.penguin;
            case RED_PANDA:
                return R.drawable.red_panda;
            case FOX:
                return R.drawable.fox;
            case KOALA:
                return R.drawable.koala;
            case ELEPHANT:
                return R.drawable.elephant;
        }
        return -1;
    }
}
