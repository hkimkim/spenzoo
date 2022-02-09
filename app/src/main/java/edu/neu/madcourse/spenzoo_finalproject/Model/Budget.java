package edu.neu.madcourse.spenzoo_finalproject.Model;

import java.util.Calendar;

public class Budget {
    private Integer year;
    private Integer month;
    private Double amount;
    private String description;
    private Double totalExpense;

    public Budget(Double amount, Integer year, Integer month, String description) {
        this.year = year;
        this.month = month;
        this.amount = amount;
        this.description = description;
        this.totalExpense = 0.00;

    }

    public Budget() {
        this.year = Calendar.getInstance().get(Calendar.YEAR);
        this.month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        this.amount = 0.00;
        this.description = "";
        this.totalExpense = 0.00;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }
}
