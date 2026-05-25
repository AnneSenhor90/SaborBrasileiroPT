package pt.saborbrasileiro.app

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Date

object DadosRestaurantesPorto {

    private val restaurantes = listOf(
        Restaurante(
            id = "porto-salve-simpatia",
            nome = "Salve Simpatia Porto",
            cidade = "Porto",
            categoria = "Brasileira",
            descricao = "Cozinha brasileira moderna com petiscos como bolinha de feijoada, pastel e pratos para partilhar no centro do Porto.",
            avaliacaoMedia = 4.6,
            imagemUrl = "https://cdn.website.dish.co/media/16/31/5394161/SALVE-SIMPATIA-PORTO-Picanha-fatiada-3-jpg.jpg",
            contacto = "+351 960 374 589",
            morada = "Rua da Picaria 89, 4050-478 Porto",
            fontePesquisa = "TheFork / TripTap",
            fonteUrl = "https://www.thefork.pt/restaurante/salve-simpatia-porto-r490117",
            criadoPorSeed = true,
            dataCriacao = Date()
        ),
        Restaurante(
            id = "porto-rodizio-brasa-nobre",
            nome = "Rodizio Brasa Nobre",
            cidade = "Porto",
            categoria = "Churrasco",
            descricao = "Rodizio de carnes de inspiração brasileira, com ambiente familiar e foco em churrasco no Porto.",
            avaliacaoMedia = 4.7,
            imagemUrl = "https://cdn.thefork.com/tf-lab/image/upload/w_500%2Ch_500%2Cc_fill%2Cq_auto%2Cf_jpg/restaurant/34cef82b-417c-46ec-b012-8cf56da5f365/7ca291f1-261f-4ba8-8d7f-bcd7d80205b4.jpg",
            contacto = "+351 933 707 802",
            morada = "R. de Sao Dinis 822, 4250-431 Porto",
            fontePesquisa = "TheFork / BellaCiao",
            fonteUrl = "https://www.thefork.pt/restaurante/rodizio-brasa-nobre-r836868",
            criadoPorSeed = true,
            dataCriacao = Date()
        ),
        Restaurante(
            id = "porto-bbqing",
            nome = "BBQing Porto",
            cidade = "Porto",
            categoria = "Churrasco",
            descricao = "Espetinhos e churrasco brasileiro com proposta casual para saborear o Brasil em Portugal.",
            avaliacaoMedia = 4.6,
            imagemUrl = "https://www.bbqingportugal.com/media/90e543_0570e1f50a70467a8a00e1904c464ca5-mv2_jpg/v1/fill/w_980-h_653-al_c-q_85-usm_0.66_1.00_0.01-enc_avif-quality_auto/90e543_0570e1f50a70467a8a00e1904c464ca5-mv2.jpg",
            contacto = "+351 936 006 708",
            morada = "R. da Boavista 335, 4050-041 Porto",
            fontePesquisa = "Site oficial BBQing",
            fonteUrl = "https://bbqing.pt/",
            criadoPorSeed = true,
            dataCriacao = Date()
        ),
        Restaurante(
            id = "porto-house-of-brazilian-food",
            nome = "The House of Brazilian Food",
            cidade = "Porto",
            categoria = "Feijoada",
            descricao = "Restaurante brasileiro com pratos caseiros, picanha executiva, feijoada completa e feijao tropeiro.",
            avaliacaoMedia = 4.3,
            imagemUrl = "https://img3.restaurantguru.com/cf86-Restaurant-The-House-of-Brazilian-Food-food.jpg",
            contacto = "",
            morada = "R. Nova de Sao Crispim 286, 4000-363 Porto",
            fontePesquisa = "TheFork / site oficial",
            fonteUrl = "https://www.thefork.pt/restaurante/the-house-of-brazilian-food-r841359",
            criadoPorSeed = true,
            dataCriacao = Date()
        ),
        Restaurante(
            id = "porto-feijuca-do-brazuca",
            nome = "Feijuca do Brazuca",
            cidade = "Porto",
            categoria = "Feijoada",
            descricao = "Comida brasileira acessivel, conhecida pela feijoada e pratos de conforto brasileiros.",
            avaliacaoMedia = 4.4,
            imagemUrl = "https://cdn6.localdatacdn.com/images/7475591/m_feijuca_do_brazuca_photo.jpg?q=6747d68c8459b",
            contacto = "",
            morada = "R. do Bonjardim 1240, Porto",
            fontePesquisa = "Restaurantji",
            fonteUrl = "https://www.restaurantji.com/pt/porto/feijuca-do-brazuca-/",
            criadoPorSeed = true,
            dataCriacao = Date()
        ),
        Restaurante(
            id = "porto-picanha-na-baixa",
            nome = "Picanha na Baixa Steakhouse Grill",
            cidade = "Porto",
            categoria = "Churrasco",
            descricao = "Steakhouse com destaque para picanha, carnes grelhadas e acompanhamentos brasileiros na Baixa do Porto.",
            avaliacaoMedia = 4.3,
            imagemUrl = "https://img02.restaurantguru.com/cbcb-Restaurant-Francesinha-na-Baixa-food.jpg",
            contacto = "+351 911 707 881",
            morada = "Rua de Santo Ildefonso 286, 4000-465 Porto",
            fontePesquisa = "Cardapio / GastroRanking",
            fonteUrl = "https://cardapio.menu/restaurants/porto-2/picanha-na-baixa",
            criadoPorSeed = true,
            dataCriacao = Date()
        ),
        Restaurante(
            id = "matosinhos-art-da-picanha",
            nome = "Art' Da Picanha",
            cidade = "Matosinhos",
            categoria = "Churrasco",
            descricao = "Rodizio e picanha no Grande Porto, com cortes brasileiros e acompanhamentos tradicionais.",
            avaliacaoMedia = 4.3,
            imagemUrl = "https://cdn.thefork.com/tf-lab/image/upload/w_500%2Ch_500%2Cc_fill%2Cq_auto%2Cf_jpg/restaurant/87ac83b0-be08-4f2e-b329-b4062de5a5c5/2d20d897-c00d-4489-8de2-8608db1a5318.jpg",
            contacto = "+351 968 206 950",
            morada = "R. Roberto Ivens 789, 4450-271 Matosinhos",
            fontePesquisa = "TheFork / Restaurantji",
            fonteUrl = "https://www.thefork.pt/restaurante/art-da-picanha-r824097",
            criadoPorSeed = true,
            dataCriacao = Date()
        ),
        Restaurante(
            id = "porto-sabor-gaucho",
            nome = "Sabor Gaucho",
            cidade = "Porto",
            categoria = "Churrasco",
            descricao = "Churrascaria brasileira em Campanha, com pratos de picanha, maminha e carnes grelhadas.",
            avaliacaoMedia = 4.0,
            imagemUrl = "https://tb-static.uber.com/prod/image-proc/processed_images/e85f400398376f2bfa84e4f9ef93057d/f6deb0afc24fee6f4bd31a35e6bcbd47.jpeg",
            contacto = "",
            morada = "Alameda Shopping, Campanha, Porto",
            fontePesquisa = "BellaCiao",
            fonteUrl = "https://bellaciao.pt/restaurante/sabor-gaucho/",
            criadoPorSeed = true,
            dataCriacao = Date()
        ),
        Restaurante(
            id = "porto-rosa-do-porto",
            nome = "Rosa do Porto",
            cidade = "Porto",
            categoria = "Brasileira",
            descricao = "Restaurante referenciado em listas publicas como brasileiro e seafood, numa zona residencial do Porto.",
            avaliacaoMedia = 4.2,
            imagemUrl = "https://media-cdn.tripadvisor.com/media/photo-t/11/dd/97/36/rosa-do-porto.jpg",
            contacto = "",
            morada = "Rua 15 de Novembro 23, 4100-421 Porto",
            fontePesquisa = "Tripadvisor",
            fonteUrl = "https://www.tripadvisor.com/Restaurant_Review-g189180-d13390013-Reviews-Rosa_do_Porto-Porto_Porto_District_Northern_Portugal.html",
            criadoPorSeed = true,
            dataCriacao = Date()
        )
    )

    fun idsSeed(): Set<String> {
        return restaurantes.map { restaurante -> restaurante.id }.toSet()
    }

    fun temRestaurantesAusentes(idsExistentes: Set<String>): Boolean {
        return restaurantes.any { restaurante -> restaurante.id !in idsExistentes }
    }

    fun sincronizarNoFirestore(
        db: FirebaseFirestore,
        aoConcluir: (Int) -> Unit,
        aoFalhar: (Exception) -> Unit
    ) {
        val batch = db.batch()
        val colecao = db.collection("restaurantes")

        restaurantes.forEach { restaurante ->
            batch.set(colecao.document(restaurante.id), restaurante, SetOptions.merge())
        }

        batch.commit()
            .addOnSuccessListener { aoConcluir(restaurantes.size) }
            .addOnFailureListener { erro -> aoFalhar(erro) }
    }
}
