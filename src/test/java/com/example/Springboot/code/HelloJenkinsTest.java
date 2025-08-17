package com.example.Springboot.code;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloJenkinsTest {

    @Test
    public void testGreet() {
        HelloJenkins app = new HelloJenkins();
        String result = app.greet("Jenkins");
        assertEquals("Hello, Jenkins", result);
    }
}	
