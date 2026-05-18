package pt.saborbrasileiro.app

import java.util.Date

data class Restaurante(
    val id: String = "",
    val nome: String = "",
    val cidade: String = "",
    val categoria: String = "", // ex: Churrascaria, Pastelaria, Açaí
    val descricao: String = "",
    val avaliacaoMedia: Double = 0.0,
    val imagemUrl: String = "", // Aqui guardaremos o link da foto (contornando o Storage!)
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val contacto: String = "",
    val dataCriacao: Date = Date()
)