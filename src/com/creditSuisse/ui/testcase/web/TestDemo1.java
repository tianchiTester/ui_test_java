package com.creditSuisse.ui.testcase.web;


import com.creditSuisse.ui.common.DriverFactory;
import com.creditSuisse.ui.library.web.BaiduLib;
import com.creditSuisse.utility.AllureListener;
import com.creditSuisse.utility.ExceptionHandling;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.creditSuisse.utility.LogManager.logProduct;


//@Listeners(TestngListener.class)
@Listeners(AllureListener.class)
public class TestDemo1 extends BaseTest{


    @Test
    @Feature("123")
    public void test() throws ExceptionHandling, IOException {
        driver = DriverFactory.initialDriver("firefox");
        driver.get("https://www.baidu.com");
        BaiduLib baiduLib = new BaiduLib(driver);
        baiduLib.search();
        logProduct.info("test baidu");
        Assert.assertTrue(false);
    }




    @AfterMethod
    public void teardown(){
        driver.quit();

    }

}