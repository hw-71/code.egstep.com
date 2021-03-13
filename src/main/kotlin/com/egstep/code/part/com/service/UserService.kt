package com.egstep.code.part.com.service

import com.egstep.code.entity.ComUserMst
import com.egstep.code.repo.jpa.UserRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired lateinit var repo: UserRepo

    fun getUsers(): List<ComUserMst> {
        val pageable = PageRequest.of(0, 10)
        return repo.findAll(pageable).toList()
    }

}