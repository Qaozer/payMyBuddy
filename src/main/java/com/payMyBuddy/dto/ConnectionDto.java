package com.payMyBuddy.dto;

public class ConnectionDto {
    private UserDto first;
    private UserDto second;
    private boolean confirmed;

    public ConnectionDto() {
    }

    public UserDto getFirst() {
        return first;
    }

    public void setFirst(UserDto first) {
        this.first = first;
    }

    public UserDto getSecond() {
        return second;
    }

    public void setSecond(UserDto second) {
        this.second = second;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
