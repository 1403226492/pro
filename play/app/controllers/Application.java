package controllers;

import models.User;
import play.mvc.Controller;

import java.util.List;

import book.Weather;

public class Application extends Controller {

    public static void login() {

        render();
    }

    public static void regist(){

        render();
    }

    public static void doLogin(String username, String password){
        User user = User.find("username=? and password=?", username, password).first();
        if(user != null) {
           //renderJSON(user);
           getWeather();
        }
    }

    public static void doRegist(User user){
        user.save();
        login();
    }

    public static void getWeather(){
        List<String> list = Weather.getWeth();
        renderJSON(list);
    }
}