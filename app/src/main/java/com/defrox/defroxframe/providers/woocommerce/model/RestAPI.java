package com.defrox.defroxframe.providers.woocommerce.model;

import android.content.Context;

import com.defrox.defroxframe.Config;
import com.defrox.defroxframe.R;

/**
 * This file is part of the Universal template
 * For license information, please check the LICENSE
 * file in the root of this project
 *
 * @author Sherdle
 * Copyright 2018
 */

public class RestAPI {
    /**
     * API Constants
     */
    private String path = "/wp-json/wc/v3/";
    //Defines the path to checkout
    private String checkout = "/checkout/";
    //Defines the path to the login page (User is signed in using with a post request. Parameters: log, pwd)
    private String login = "/wp-login.php";
    //Defines the path to the registration page where the user can register (User creates account manually)
    private String register = "/my-account/";
    //Defines the url that is visited when checkout is completed
    private String checkout_complete = "/checkout/order-received/";
    //Defines the first part of a of a name of a cookie that confirms that login was successful
    private String login_success_cookie = "wordpress_logged_in_";

    /**
     * Units & Currency to display
     */
    private static String currencyFormat = Config.WC_CURRENCY + "%s";
    private static String unit_size = "cm";
    private static String unit_weight = "kg";

    /**
     * WooCommerce Banner Action
     */
    public static String home_banner_one = "featured";
    public static String home_banner_two = "sale";

    private Context context;
    public RestAPI(Context context) {
        this.context = context;
    }

    public String getHost() {
        return context.getResources().getString(R.string.woocommerce_url);
    }

    public String getPath() {
        return path;
    }

    public String getCheckout() {
        return checkout;
    }

    public String getLogin() {
        return login;
    }

    public String getRegister() {
        return register;
    }

    public String getLoginCookie(){
        return login_success_cookie;
    }

    public String getCheckoutComplete() {
        return checkout_complete;
    }

    public String getCustomerKey() {
        return context.getResources().getString(R.string.woocommerce_consumer_key);
    }

    public String getCustomerSecret() {
        return context.getResources().getString(R.string.woocommerce_consumer_secret);
    }

    public static String getCurrencyFormat() {
         return currencyFormat;
    }
    public static String getUnitSize() {
        return unit_size;
    }
    public static String getUnitWeight() {
        return unit_weight;
    }

    public Context getContext(){
        return context;
    }
}
