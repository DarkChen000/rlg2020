package com.itdr.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.CategoryMapper;
import com.itdr.mapper.ProductMapper;
import com.itdr.pojo.Category;
import com.itdr.pojo.Product;
import com.itdr.pojo.vo.ProductVO;
import com.itdr.service.ProductService;
import com.itdr.utils.ObjectToVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public ServerResponse<Category> topCategory(Integer sid) {
        // 判断参数合法性
        if (sid == null || sid < 0){
            return ServerResponse.defeatedRS
                    (ConstCode.ProductEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ProductEnum.UNLAWFUINESS_PARAM.getDesc());
        }

        // 根据父ID查询直接子类
        List<Category> li = categoryMapper.selectByParentID(sid);
        // 成功返回
        return ServerResponse.successRS(li);
    }

    @Override
    public ServerResponse detail(Integer productId) {
        // 判断参数合法性
        if (productId == null || productId < 0){
            return ServerResponse.defeatedRS
                    (ConstCode.ProductEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ProductEnum.UNLAWFUINESS_PARAM.getDesc());
        }

        // 根据父id茶渣直接子类
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null || product.getStatus() != 1){
            return ServerResponse.defeatedRS(ConstCode.ProductEnum.NO_PRODUCT.getCode(),
                    ConstCode.ProductEnum.NO_PRODUCT.getDesc());
        }

        // 封装VO
        ProductVO productVO = ObjectToVOUtil.ProductToProductVO(product);

        // 返回成功数据
        return ServerResponse.successRS(productVO);
    }

    @Override
    public ServerResponse<Product> list(String keyWord, Integer pageName, Integer pageSize, String orderBy) {
        //非空判断
        if(StringUtils.isEmpty(keyWord)){
            return ServerResponse.defeatedRS(ConstCode.ProductEnum.UNLAWFUINESS_PARAM.getCode(),
                    ConstCode.ProductEnum.UNLAWFUINESS_PARAM.getDesc());
        }
        //排序参数处理
        String[] split = new String[2];
        if(!StringUtils.isEmpty(orderBy)){
            split = orderBy.split("_");
        }
        //模糊查询
        String word = "%"+keyWord+"%";
        //开启分页
        PageHelper.startPage(pageName,pageSize,split[0]+" "+split[1]);
        List<Product> li =  productMapper.selectByName(word);
        PageInfo pageInfo = new PageInfo(li);

        //封装Vo
        List<ProductVO> liNew = new ArrayList<ProductVO>();
        for (Product product : li) {
            ProductVO productVO = ObjectToVOUtil.ProductToProductVO(product);
            liNew.add(productVO);
        }

        pageInfo.setList(liNew);
        //返回成功数据
        return ServerResponse.successRS(pageInfo);
    }
}
