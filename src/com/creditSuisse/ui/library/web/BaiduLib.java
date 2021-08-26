package com.creditSuisse.ui.library.web;

import org.openqa.selenium.WebDriver;

public class BaiduLib extends Page{
    public BaiduLib(WebDriver driver) {
        super(driver,"demo");
    }

    public void search(){
        getElement("baidu_input").sendKeys("12345");
        getElement("baidu_button").click();
    }


}
