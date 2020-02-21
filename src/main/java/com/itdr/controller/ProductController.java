package com.itdr.controller;


import com.itdr.common.ServerResponse;
import com.itdr.pojo.Category;
import com.itdr.pojo.Product;
import com.itdr.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/portal/product/")
public class ProductController {

    @Autowired
    ProductService productService;

    /**
     * 获取商品直接分类
     * @param sid
     * @return
     */
    @RequestMapping("topcategory.do")
    public ServerResponse<Category> topCategory(Integer sid){
        return productService.topCategory(sid);
    }

    /**
     * 获取商品详情
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    public ServerResponse detail(Integer productId){
        return productService.detail(productId);
    }


    /**
     * 动态排序  模糊查询
     * @param keyWord
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping("list.do")
    public ServerResponse<Product> list(String keyWord,
                                        @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,//页码
                                        @RequestParam(value = "pageSize",required = false,defaultValue = "5")Integer pageSize,//每页数目
                                        @RequestParam(value = "orderBy",required = false,defaultValue = "")String orderBy){     //排序规则
        return productService.list(keyWord,pageNum,pageSize,orderBy);
    }
}
