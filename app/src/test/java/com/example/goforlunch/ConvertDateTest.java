package com.example.goforlunch;

import com.example.goforlunch.utils.ConvertDate;

import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class ConvertDateTest {
    @Test
    public void dateEnglish(){
        assertEquals("5.25pm", ConvertDate.convertDate("1725", "English"));
    }

    @Test
    public void dateFrench(){
        assertEquals("17h25", ConvertDate.convertDate("1725", "French"));
    }
}
