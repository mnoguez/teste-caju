package com.testecaju.services;

import com.testecaju.domain.user.User;
import com.testecaju.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public User findUserById(String id) throws Exception {
        return this.repository.findById(id).orElseThrow(()->new Exception("Usuário não encontrado."));
    }

    public void saveUser(User user){
        this.repository.save(user);
    }
}
