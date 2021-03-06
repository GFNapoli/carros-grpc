package br.com.zup.edu.carros

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Carro(
    @field:NotBlank @Column(nullable = false) val modelo: String,
    @field:NotBlank @Column(nullable = false) val placa: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
}