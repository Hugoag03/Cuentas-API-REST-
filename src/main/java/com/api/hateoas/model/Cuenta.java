package com.api.hateoas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Entity
@Table(name = "cuenta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta extends RepresentationModel<Cuenta> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 20, nullable = false, unique = true)
    private String numeroDeCuenta;

    private float monto;

    public Cuenta(Integer id, String numeroDeCuenta) {
        this.id = id;
        this.numeroDeCuenta = numeroDeCuenta;
    }
}
