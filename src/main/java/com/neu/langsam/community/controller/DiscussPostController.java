package com.neu.langsam.community.controller;

import com.neu.langsam.community.entity.Comment;
import com.neu.langsam.community.entity.DiscussPost;
import com.neu.langsam.community.entity.Page;
import com.neu.langsam.community.entity.User;
import com.neu.langsam.community.service.CommentService;
import com.neu.langsam.community.service.DiscussPostService;
import com.neu.langsam.community.service.UserService;
import com.neu.langsam.community.util.CommunityConstant;
import com.neu.langsam.community.util.CommunityUtil;
import com.neu.langsam.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.WebParam;
import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    /**
     * 增加评论
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if(user == null) return CommunityUtil.getJsonString(403,"你还没有登录！");
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        return CommunityUtil.getJsonString(0,"发布成功！");
    }

    /**
     * 查询帖子
     * 界面需要显示作者昵称，在user表里面，而评论在discuss_post表里面，两种方式
     *      -两张表的联合查询
     *      -调用userService查出user，效率低
     * 这里采用后者，效率因素后面用redis解决
     */
//    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
//    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
//        //帖子
//        DiscussPost post=discussPostService.findDiscussPostById(discussPostId);
//        model.addAttribute("post",post);
//        //作者
//        User user=userService.findUserById(post.getUserId());
//        //System.out.println("-----------------------------");
//        System.out.println(user.toString());
//        //System.out.println("-----------------------------");
//        model.addAttribute("user",user);
//        //System.out.println("=============================");
//
//        return "/site/discuss-detail";
//
//    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        //作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        //评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        //评论：给帖子的评论
        //回复：给评论的评论

        //评论列表
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        //评论Vo列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                //评论Vo
                Map<String, Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment", comment);
                //作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //回复列表
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //回复的Vo
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply", reply);
                        //作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        //回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";

    }




}
