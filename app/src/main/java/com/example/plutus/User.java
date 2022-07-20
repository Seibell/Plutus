package com.example.plutus;

public class User {
    public String firstName;
    public String lastName;
    public String email;
    public double income;

    public User() {
    }

    public User (String firstName, String lastName, String email, double income) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.income = income;
    }
}