package com.intellimarket.api.stores.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="stores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Stores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 150)
    private String location;

    // Aquí podrías luego añadir el userId que mencionaste en tus notas
    // private Long userId;
}