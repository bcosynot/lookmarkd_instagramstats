package com.lookmarkd.repository;

import com.lookmarkd.domain.FOSUser;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FOSUserRepository extends PagingAndSortingRepository<FOSUser, Long> {
    public FOSUser findByUsername(String username);
}
