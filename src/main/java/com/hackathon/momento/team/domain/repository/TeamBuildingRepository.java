package com.hackathon.momento.team.domain.repository;

import com.hackathon.momento.member.domain.Member;
import com.hackathon.momento.team.domain.Status;
import com.hackathon.momento.team.domain.TeamBuilding;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamBuildingRepository extends JpaRepository<TeamBuilding, Long> {

    boolean existsByMemberAndStatus(Member member, Status status);

    List<TeamBuilding> findByStatus(Status status);

    List<TeamBuilding> findByMemberAndStatus(Member member, Status status);
}
