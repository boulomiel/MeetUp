package com.rubenmimoun.meetup.app.Models;

import java.util.ArrayList;

public class UserList {

    private ArrayList<User> userlist ;

    public UserList() {
        this.userlist = new ArrayList<>();
    }

    public ArrayList<User> getUserlist() {
        return userlist;
    }

    public void setUserlist(ArrayList<User> userlist) {
        this.userlist = userlist;
    }
}
