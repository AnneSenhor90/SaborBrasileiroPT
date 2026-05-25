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
    val morada: String = "",
    val fontePesquisa: String = "",
    val fonteUrl: String = "",
    val criadoPorSeed: Boolean = false,
    val estado: String = "aprovado",
    val criadoPorUid: String = "",
    val criadoPorTipo: String = "",
    val aprovadoPorUid: String = "",
    val dataAprovacao: Date? = null,
    val dataCriacao: Date = Date()
)
