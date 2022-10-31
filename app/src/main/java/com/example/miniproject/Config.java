package com.example.miniproject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Config {
    //JSON CATEGORY URL
    public static final String DATA_CATEGORY_URL = "https://opentdb.com/api_category.php";
    //API GET QUESTIONS eg https://opentdb.com/api.php?amount=10&difficulty=easy
    public static final String DATA_API_URL = "https://opentdb.com/api.php";

    //Firebase Tournament Ref
    public static final String TOURNAMENT_REF = "tournament";

    public static final Integer Q_AMOUNT = 10; // AMOUNT OF QUESTIONS

    public static final String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    public static User user;



}
