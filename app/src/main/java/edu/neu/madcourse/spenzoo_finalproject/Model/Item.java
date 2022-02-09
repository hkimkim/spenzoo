package edu.neu.madcourse.spenzoo_finalproject.Model;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.neu.madcourse.spenzoo_finalproject.R;

public class Item extends Piece{
    public enum ItemType {
        PALM_TREE, TREE, ROCK, LOG, BIG_ICE, SMALL_ICE
    }
    private Double cost;
    private Date dateOfPurchase;
    private String description;

    public Item(ItemType type) {
        this.type = type.name();
        this.cost = calculateCost(type);
        //this.fixedItem = fixedItem;
        this.dateOfPurchase = Calendar.getInstance().getTime();
        this.xPosition = null;
        this.yPosition = null;
        this.imageSource = matchItemImage(type);
        this.description = matchDescription(type);
    }


    public Item() {
        this.type = "";
        this.cost = 0.00;
        //this.fixedItem = true;
        this.dateOfPurchase = Calendar.getInstance().getTime();
        this.xPosition = null;
        this.yPosition = null;
        this.imageSource = -1;
    }

    // TODO: update based on cost
    private Double calculateCost(ItemType type) {
        switch (type) {
            case TREE:
                return 35.00;
            case ROCK:
                return 20.00;
            case PALM_TREE:
                return 40.00;
            case BIG_ICE:
                return 25.00;
            case LOG:
                return 30.00;
            case SMALL_ICE:
                return 15.00;
        }
        return 0.00;
    }

    public Double getCost() {
        return cost;
    }
    public Integer getImg() { return imageSource; }
    public String getDescription() { return description; }

    public Date getDateOfPurchase() {
        return dateOfPurchase;
    }

    private String matchDescription(ItemType itemType) {
        switch (itemType) {
            case TREE:
                return "tree";
            case PALM_TREE:
                return "palm_tree";
            case ROCK:
                return "rock";
            case LOG:
                return "log";
            case BIG_ICE:
                return "big_ice";
            case SMALL_ICE:
                return "small_ice";
        }
        return "";
    }

    private Integer matchItemImage(ItemType itemType) {
        switch (itemType) {
            case TREE:
                return R.drawable.tree;
            case PALM_TREE:
                return R.drawable.palm_tree;
            case ROCK:
                return R.drawable.rock;
            case LOG:
                return R.drawable.log;
            case BIG_ICE:
                return R.drawable.big_ice;
            case SMALL_ICE:
                return R.drawable.small_ice;
        }
        return -1;
    }
//    public Boolean isFixedItem() {
//        return fixedItem;
//    }
//
//    public void setFixedItem(Boolean fixedItem) {
//        this.fixedItem = fixedItem;
//    }


    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setDateOfPurchase(Date dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
