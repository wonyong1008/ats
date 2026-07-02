package com.ainjob.ats.domain.applicant;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Skill")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer category;
}
