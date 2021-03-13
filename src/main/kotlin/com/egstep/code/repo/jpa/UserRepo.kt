package com.egstep.code.repo.jpa

import com.egstep.code.entity.ComUserMst
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : PagingAndSortingRepository<ComUserMst, Int> {
}