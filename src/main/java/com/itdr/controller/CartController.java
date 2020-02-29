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
                              @RequestParam(value = "type",required = false,defaultValue = "100")Integer type,
                              HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartsService.add(productID,count,type,user);
    }

    /**
     * 移除购物车某个商品
     * @param productID
     * @param session
     * @return
     */
    @RequestMapping("delete_product.do")
    public ServerResponse deleteProduct(Integer productID, HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartsService.deleteProduct(productID,user);
    }

    /**
     * 移除购物车中所有被选中的商品
     * @param session
     * @return
     */
    @RequestMapping("delete_products.do")
    public ServerResponse deleteProducts(HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartsService.deleteProducts(user);
    }

    /**
     * 更新购物车某个商品的数量
     * @param productID
     * @param type
     * @param count
     * @param session
     * @return
     */
    @RequestMapping("update.do")
    public ServerResponse update(Integer productID,
                                 @RequestParam(value = "type",required = false,defaultValue = "100")Integer type,
                                 Integer count, HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartsService.update(productID,count,type,user);
    }

    /**
     * 查询购物车中的商品数量
     * @param session
     * @return
     */
    @RequestMapping("get_cart_product_count.do")
    public ServerResponse getCartProductCount(HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartsService.getCartProductCount(user);
    }


    @RequestMapping("checked.do")
    public ServerResponse checked(Integer productID,
                                  @RequestParam(value = "type",required = false,defaultValue = "0")Integer type,
                                  HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartsService.checked(productID,type,user);
    }

    /**
     * 购物车跳转去结算页面
     * @param session
     * @return
     */
    @RequestMapping("to_pay_for.do")
    public ServerResponse toPayFor(HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartsService.toPayFor(user);
    }
}
