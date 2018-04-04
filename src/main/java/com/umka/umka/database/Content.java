package com.umka.umka.database;

/**
 * Created by trablone on 10.05.16.
 */
public class Content {
    //8ArTpxN2PcyYAyKraRLLnL5ZhhOXbF9P

    public static class User{
        public static String id = "id";
        public static String master_id = "master_id";
        public static String profile = "profile";
        public static String avatar = "avatar";
        public static String about = "about";
        public static String gender = "gender";
        public static String type = "type";
        public static String first_name = "first_name";
        public static String last_name = "last_name";
        public static String fcm_token = "fcm_token";
        public static String email = "email";
        public static String city = "city";
        public static String phone = "phone";
        public static String pass = "pass";
        public static String token = "token";
    }
    public static class Category{
        public static String parent_id = "parent";
        public static String color = "color";
        public static String name = "name";
        public static String nameEn = "nameEn";
        public static String id = "id";
        public static String picture = "picture";
        public static String next_layer = "next_layer";
    }

    public static class Favorite{
        public static String id = "id";
    }

    public static class Spec{
        public static String next_layer = "next_layer";
        public static String layer = "layer";
        public static String spec_name = "spec_name";
        public static String name = "name";
        public static String id = "id";
        public static String one_id = "one_id";
        public static String parent_id = "parent_id";
    }

}
