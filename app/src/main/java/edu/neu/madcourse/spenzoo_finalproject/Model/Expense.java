package edu.neu.madcourse.spenzoo_finalproject.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Expense {

    public enum Category {
        FOOD, HOUSING, TRANSPORT, ENTERTAINMENT, TRAVEL,
        HOBBY, APPAREL, SAVING, INSURANCE, MISCELLANEOUS, DEFAULT
    }
    private String category;
    private Double amount;
    private Date date;
    private String description;
    private Boolean isExpense;

    public Expense() {
        this.category = Category.DEFAULT.name();
        this.amount = 0.00;
        this.date = Calendar.getInstance().getTime();
        this.description = "";
        this.isExpense = true;
    }

    public Expense(String category, Double amount, Date date, String description, Boolean expense) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.isExpense = expense;
    }

    private String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(cal.getTime());
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsExpense() {
        return isExpense;
    }

    public void setExpense(Boolean expense) {
        isExpense = expense;
    }
}

