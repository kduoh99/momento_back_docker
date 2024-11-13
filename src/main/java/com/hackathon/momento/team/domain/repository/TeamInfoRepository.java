package com.hackathon.momento.team.domain.repository;

import com.hackathon.momento.team.domain.TeamInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamInfoRepository extends JpaRepository<TeamInfo, Long> {
}
