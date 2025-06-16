package org.havoc.skilltrack.skilllog.repository;

import org.havoc.skilltrack.skilllog.entity.SkillLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SkillLogRepository extends JpaRepository<SkillLog, String>, SkillLogCustomRepository {
    boolean existsByUserIdAndTypeAndSourceAndDate(Long userId, String type, String source, LocalDate date);
}
