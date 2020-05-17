package br.com.senior.challenge.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * Entidade base para as demais entidades do sistema
 */
@MappedSuperclass
@EqualsAndHashCode
@Getter
@Setter
public abstract class AbstractBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", updatable = false, unique = true, nullable = false)
    private UUID id;

}
