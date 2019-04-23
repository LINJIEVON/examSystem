package com.sp.exam.service.impl;

import com.sp.exam.dao.UserDao;
import com.sp.exam.enums.ResultEnum;
import com.sp.exam.exception.LoginException;
import com.sp.exam.pojo.User;
import com.sp.exam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public User login(User user) {
        User resultUser = userDao.findByUsername(user.getUsername());
        if(user.getPassword().equals(resultUser.getPassword())&&user.getUserType()==resultUser.getUserType()){
            return resultUser;
        }else {
            throw new LoginException(ResultEnum.ACCOUNT_WRONG);
        }


    }
}