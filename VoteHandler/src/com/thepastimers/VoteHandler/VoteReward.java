package com.thepastimers.VoteHandler;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 7/28/13
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class VoteReward {
    String name;
    String item;
    String description;
    int amount;
    int credits;

    public VoteReward(String name, String item, String description, int amount, int credits) {
        this.name = name;
        this.item = item;
        this.description = description;
        this.amount = amount;
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
