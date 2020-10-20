package com.payMyBuddy.dto;

public class ConnectionDto {
    private UserDto owner;
    private UserDto target;

    public ConnectionDto() {
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public UserDto getTarget() {
        return target;
    }

    public void setTarget(UserDto target) {
        this.target = target;
    }
}
