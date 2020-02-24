package com.itdr.controller;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.pojo.User;
import com.itdr.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@ResponseBody
@RequestMapping("/portal/cart/")
public class CartController {

    @Autowired
    CartService cartsService;

    /**
     * 查看购物车列表
     * @param session
     * @return
     */
    @RequestMapping("list.do")
    public ServerResponse list(HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartsService.list(user);
    }

    /**
     * 向购物车里面增加商品
     * @param productID
     * @param count
     * @param session
     * @return
     */
    @RequestMapping("add.do")
    public ServerResponse add(Integer productID,
                              @RequestParam(value = "count",required = false,defaultValue = "1")Integer count,
                              HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartsService.add(productID,count,user);
    }

    /**
     * 移除购物车某个商品
     * @param productIDs
     * @param session
     * @return
     */
    @RequestMapping("delete_product.do")
    public ServerResponse deleteProduct(String productIDs, HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartsService.deleteProduct(productIDs,user);
    }


    @RequestMapping("update.do")
    public ServerResponse update(Integer productID, Integer count, HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartsService.update(productID,count,user);
    }
}
