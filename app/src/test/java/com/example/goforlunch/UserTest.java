package com.example.goforlunch;

import com.example.goforlunch.model.User;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class UserTest {

    private User user;

    @Before
    public void setAttributes(){
        user = new User("123456", "bill", null, "myEmail", "789", "myRestaurant", true);
    }

    @Test
    public void userGetters(){
        assertEquals("123456", user.getUid());
        assertEquals("bill", user.getUsername());
        assertNull(user.getUrlPicture());
        assertEquals("myEmail", user.getmEmail());
        assertEquals("789", user.getmRestaurantId());
        assertEquals("myRestaurant", user.getmRestaurantName());
        assertTrue(user.getmLike());
    }

    @Test
    public void userSetters(){
        user.setUid("369258");
        user.setUsername("james");
        user.setUrlPicture("myUrl");
        user.setmEmail(null);
        user.setmRestaurantId("147");
        user.setmRestaurantName("newRestaurant");
        user.setmLike(false);

        assertEquals("369258", user.getUid());
        assertEquals("james", user.getUsername());
        assertEquals("myUrl", user.getUrlPicture());
        assertNull(user.getmEmail());
        assertEquals("147", user.getmRestaurantId());
        assertEquals("newRestaurant", user.getmRestaurantName());
        assertFalse(user.getmLike());
    }
}
