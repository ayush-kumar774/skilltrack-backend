package org.havoc.skilltrack.skilllog.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SkillLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private Long userId;         // Auth user
    private String type;         // DSA, Backend, etc.
    private String source;       // MANUAL, GITHUB, LEETCODE
    private int count;           // Number of problems, PRs etc.
    private LocalDate date;

    @ElementCollection
    private List<String> tags;
}