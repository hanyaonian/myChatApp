package entity;

/**
 * Created by DELL on 2017/7/11.
 */
//this is a singleton, for all application use

public class User {
    private static User instance;
    private boolean isLogin;
    private String userName;
    private void User() {
        isLogin = false;
    }
    public static User getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (User.class){
                User t = new User();
                instance = t;
                return instance;
            }
        }
    }
    public String getUserName() {return userName;}
    public void setUserName(String Username) {userName = Username;}
    public boolean isLogin() {
        return isLogin;
    }
    public void logOut() {
        isLogin = false;
    }
    public void logIn() {
        isLogin = true;
    }
}
