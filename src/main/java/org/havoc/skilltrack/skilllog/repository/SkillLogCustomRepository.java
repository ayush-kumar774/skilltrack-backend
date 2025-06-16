package org.havoc.skilltrack.skilllog.repository;

import org.havoc.skilltrack.skilllog.dto.SkillLogFilterRequest;
import org.havoc.skilltrack.skilllog.entity.SkillLog;

import java.util.List;
import java.util.Optional;

public interface SkillLogCustomRepository {
    Optional<List<SkillLog>> findByFilters(SkillLogFilterRequest filterRequest);
}
