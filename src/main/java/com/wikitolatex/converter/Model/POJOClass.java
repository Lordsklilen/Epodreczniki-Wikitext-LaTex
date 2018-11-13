package com.wikitolatex.converter.Model;

public class POJOClass {
    private int number;
    private String name;

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public POJOClass(int number, String name) {
        this.number = number;
        this.name = name;
    }
}
