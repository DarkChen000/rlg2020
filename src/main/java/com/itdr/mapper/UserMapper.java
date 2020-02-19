package com.itdr.mapper;

import com.itdr.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUserNameAndPassWord(String username, String password);

    int selectByUserName(String username);

    int selectByEmails(String email);

    User getInformation(String username);

    int selectByUserNameOrEmails(@Param("str") String str,@Param("type") String type);

    User selectByUsername(String username);

    int selectByUserNameAndQuestionAndAnswer(@Param("username") String username, @Param("question")String question, @Param("answer")String answer);

    int updateByUserNameAndPassWord(@Param("username")String username, @Param("passwordNew")String passwordNew);

    int updateByUserNameAndPasswordOldAndPassWordNew(@Param("username")String username,  @Param("passwordOld")String passwordOld,  @Param("passwordNew")String passwordNew);

}