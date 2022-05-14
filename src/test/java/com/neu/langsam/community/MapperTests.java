package com.neu.langsam.community;


import com.neu.langsam.community.dao.DiscussPostMapper;
import com.neu.langsam.community.dao.UserMapper;
import com.neu.langsam.community.entity.DiscussPost;
import com.neu.langsam.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;
    @Test
    public void testSelectUser() {
        User user = userMapper.selectByName("zzz");
        System.out.println(user);
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 20);
        for (DiscussPost post : list) {
            System.out.println(post);
        }
    }

}
