package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller       //返回到页面，所以加Controller注解
public class userController {

    //绑定
    @Autowired
    private RestTemplate restTemplate;


    //注册页面
    @GetMapping("/register")
    @RequestMapping("/register")
    public String register()
    {
        return "register";
    }


    //添加用户,结果返回表单
    @RequestMapping("/adduser")
    //希望可以用map传递数据，request是前端往后端传，map是服务器往前端传
    public String adduser(HttpServletRequest request, Map<String,Object> map)
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user=new User();          //设置user的username和password
        user.setUsername(username);
        user.setPassword(password);

        //将User的信息写进数据库，用到resTemplate,其中postForObject方法与html里的post方法对应
        //将User的信息写到之前的restuser里面，request的参数是null，最后一个是返回值，希望返回String
        String s = restTemplate.postForObject("http://localhost:9000/restuser/adduser?username="
                + username + "&password=" + password, null, String.class);

        if("ok".equals(s))  //注册成功,restuser adduser返回ok
        {
            map.put("msg","success");     //注册成功，在register显示success
        }
        else
        {
            map.put("msg1","fault");     //注册失败，在register显示fault
        }
        return "register";
    }

    //删除用户
    @RequestMapping("/deleteuser")
    public String deleteuser(HttpServletRequest request,Map<String,Object> map)
    {
        String username = request.getParameter("username");
        String s = restTemplate.postForObject("http://localhost:9000/restuser/deleteuser?username="
                + username, null, String.class);

        if("delete".equals(s))   //删除成功,restuser deleteuser返回delete
        {
            map.put("msg2","delete");
        }
        else
        {
            map.put("msg3","fault");
        }
        return "register";
    }

    //修改用户信息
    @RequestMapping("/updateuser")
    public String updateuser(HttpServletRequest request,Map<String,Object> map)
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String s = restTemplate.postForObject("http://localhost:9000/restuser/updateuser?username="
                + username + "&password=" + password, null, String.class);

        if("update".equals(s))  //修改成功,restuser updateuser返回update
        {
            map.put("msg4","update");
        }
        else
        {
            map.put("msg5","fault");
        }
        return "register";
    }


    //查询用户，得到一个用户
    @RequestMapping("/getuser")
    public String getuser(HttpServletRequest request,Map<String,Object> map)
    {
        String username = request.getParameter("username");
        User user = restTemplate.postForObject("http://localhost:9000/restuser/getuser?username="
                + username, null, User.class);

        if(user!=null)    //有该用户
        {
            map.put("msg6","get");
            System.out.println(user);
        }
        else
        {
            map.put("msg7","fault");
        }
        return "register";
    }



    //得到所有用户
    @RequestMapping("/getalluser")
    public String getalluser(Model model)
    {
        //返回的list是一个json的字符串
        List list = restTemplate.postForObject("http://localhost:9000/restuser/getalluser",
                null, List.class);

        //需要加依赖，json对象和json字符串转换的依赖  fastjson
        for(Object o:list)
        {
            JSON json=(JSON) JSON.toJSON(o);  //json字符串转成json对象
            User user=JSON.toJavaObject(json,User.class);   // //json对象转换成User对象
            System.out.println(user);       //输出所有用户
        }
        return "register";
    }

}
