package com.neu.langsam.community;

import com.neu.langsam.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.StringJoiner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void testSensitiveFilter() {
        String s = "你这个傻逼，---你玩个蛇皮，还有安琪拉，你可真是个老六啊！！！操";
        System.out.println(sensitiveFilter.filter(s));
    }
}
