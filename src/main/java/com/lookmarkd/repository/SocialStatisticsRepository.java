package com.lookmarkd.repository;

import com.lookmarkd.domain.SocialStatistic;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialStatisticsRepository extends PagingAndSortingRepository<SocialStatistic, Integer> {
}
