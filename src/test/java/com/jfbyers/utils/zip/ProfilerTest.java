package com.jfbyers.utils.zip;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ProfilerTest {


    @Test
    public void testFromStringToPath(){
        String result = Profiler.fromStringToPath("c:\\test\\test");
        Assert.assertEquals("No extension test ","c:/test/test",result);
    }

    @Test
    public void testFromStringToPath2(){
        String result = Profiler.fromStringToPath("c:\\test");
        Assert.assertEquals("No extension and no folder test ","c:/test",result);
    }

    @Test
    public void testFromStringToPath3(){
        String result = Profiler.fromStringToPath("/test");
        Assert.assertEquals("No extension and no folder linux test ","/test",result);
    }

}
