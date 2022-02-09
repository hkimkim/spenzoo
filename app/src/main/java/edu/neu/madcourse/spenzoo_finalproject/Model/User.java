package edu.neu.madcourse.spenzoo_finalproject.Model;

import java.util.HashMap;

public class User {
    private String username;
    private String email;
    private HashMap<String, Animal> animal;
    private HashMap<String, Expense> expense;
    private HashMap<String, Budget> budget;
    private Integer rewards;
    private HashMap<String, Item> items;
    private Integer numOfAnimals;
    private long creationDate;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.animal = new HashMap<>();
        this.budget = new HashMap<>();
        this.expense = new HashMap<>();
        this.rewards = 0;
        this.items = new HashMap<>();
        this.numOfAnimals = 0;
        this.creationDate = 0;
    }

    public User() {};

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, Animal> getAnimal() {
        return animal;
    }

    public void setAnimal(HashMap<String, Animal> animal) {
        this.animal = animal;
    }

    public HashMap<String, Expense> getExpense() {
        return expense;
    }

    public void setExpense(HashMap<String, Expense> expense) {
        this.expense = expense;
    }

    public HashMap<String, Budget> getBudget() {
        return budget;
    }

    public void setBudget(HashMap<String, Budget> budget) {
        this.budget = budget;
    }

    public Integer getRewards() {
        return rewards;
    }

    public void setRewards(Integer rewards) {
        this.rewards = rewards;
    }

    public HashMap<String, Item> getItems() {
        return items;
    }

    public void setItems(HashMap<String, Item> items) {
        this.items = items;
    }

    public Integer getNumOfAnimals() {
        return numOfAnimals;
    }

    public void setNumOfAnimals(Integer numOfAnimals) {
        this.numOfAnimals = numOfAnimals;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }
}
