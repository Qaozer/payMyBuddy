package com.payMyBuddy.dto;

public class BankTransactionDto {

    private String iBAN;
    private double amount;

    public BankTransactionDto() {
    }

    public String getiBAN() {
        return iBAN;
    }

    public void setiBAN(String iBAN) {
        this.iBAN = iBAN;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
