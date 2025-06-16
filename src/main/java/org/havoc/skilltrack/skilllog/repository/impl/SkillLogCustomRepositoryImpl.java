package org.havoc.skilltrack.skilllog.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.havoc.skilltrack.skilllog.dto.SkillLogFilterRequest;
import org.havoc.skilltrack.skilllog.entity.SkillLog;
import org.havoc.skilltrack.skilllog.repository.SkillLogCustomRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SkillLogCustomRepositoryImpl implements SkillLogCustomRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<List<SkillLog>> findByFilters(SkillLogFilterRequest filterRequest){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SkillLog> query = criteriaBuilder.createQuery(SkillLog.class);
        Root<SkillLog> root = query.from(SkillLog.class);

        List<Predicate> predicates = new ArrayList<>();

        // Type filter
        if (StringUtils.hasText(filterRequest.getType())) {
            predicates.add(criteriaBuilder.equal(root.get("type"), filterRequest.getType()));
        }

        // Source filter
        if (StringUtils.hasText(filterRequest.getSource())) {
            predicates.add(criteriaBuilder.equal(root.get("source"), filterRequest.getSource()));
        }

        // Date range
        if (Objects.nonNull(filterRequest.getFrom()) && Objects.nonNull(filterRequest.getTo())) {
            predicates.add(criteriaBuilder.between(root.get("date"), filterRequest.getFrom(), filterRequest.getTo()));
        }
        else if (Objects.nonNull(filterRequest.getFrom())) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), filterRequest.getFrom()));
        }
        else if (Objects.nonNull(filterRequest.getTo())) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), filterRequest.getTo()));
        }

        // Tags (partial match)
        if (Objects.nonNull(filterRequest.getTags()) && !filterRequest.getTags().isEmpty())  {
            predicates.add(root.join("tags").in(filterRequest.getTags()));
        }
        query.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return Optional.ofNullable(entityManager.createQuery(query).getResultList());
    }

}
