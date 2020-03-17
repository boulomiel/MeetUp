package com.rubenmimoun.meetup.app.Models;

public class User {

    private String id ;
    private String name ;
    private String email;
    private String password ;
    private String status ;
    private String imageURL ;
    private String playing;
    private String bars ;
    private String connected_to;
    private String inGame ;
    private String left;
    private String city ;
    private boolean first ;




    public User (){}


    public User(String name, String email, String password,String imageURL, String status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.imageURL = imageURL ;
        this.id = "" ;
        this.status = status;
        this.left ="" ;
        this.city ="city" ;


    }


    public User(String id){
        this.id= id ;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPlaying() {
        return playing;
    }

    public void setPlaying(String playing) {
        this.playing = playing;
    }

    public String getConnected_to() {
        return connected_to;
    }

    public String getInGame() {
        return inGame;
    }

    public void setInGame(String inGame) {
        this.inGame = inGame;
    }

    public void setConnected_to(String connected_to) {
        this.connected_to = connected_to;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(){
        first = true ;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", playing='" + playing + '\'' +
                ", bars='" + bars + '\'' +
                ", connected_to='" + connected_to + '\'' +
                '}';
    }
}
