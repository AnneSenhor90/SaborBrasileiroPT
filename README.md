# Sabor Brasileiro PT

App Android para descobrir restaurantes brasileiros em Portugal, com foco em gastronomia brasileira, descoberta local, avaliações verificadas e uma experiência visual moderna.

O projeto nasceu com a proposta de ajudar utilizadores a encontrar sabores brasileiros perto de si, especialmente restaurantes, lanchonetes, churrascarias, casas de açaí, feijoada, doces e outros espaços brasileiros em Portugal.

## Conceito

O **Sabor Brasileiro PT** combina:

- gastronomia brasileira;
- descoberta de restaurantes em Portugal;
- avaliações de utilizadores;
- validação por administradores;
- identidade visual tropical sofisticada;
- proteção de dados com regras RGPD.

A ideia é criar uma base confiável de restaurantes brasileiros, permitindo que utilizadores recomendem novos locais e que administradores validem as informações antes da publicação.

## Estado atual

O app está em fase de protótipo funcional avançado.

Funcionalidades já implementadas:

- entrada principal visual com botão Google;
- login com email e senha;
- criação de conta de utilizador;
- criação de conta de restaurante/parceiro;
- perfil de utilizador;
- perfil administrativo;
- listagem de restaurantes;
- pesquisa por nome, cidade, categoria e descrição;
- filtros por categoria;
- tela de detalhes do restaurante;
- envio de avaliações pendentes;
- moderação de comentários;
- cadastro de novos restaurantes pendentes;
- validação por administrador master;
- gestão inicial de prémios;
- regras RGPD no registo e perfil;
- integração com Firebase Authentication e Firestore;
- carregamento de imagens por URL com Glide.

## Funcionalidades principais

### Restaurantes

- Cards com imagem, nome, categoria, cidade, descrição e avaliação.
- Clique no card para abrir detalhes.
- Tela de detalhes com:
  - imagem/logo;
  - nome;
  - categoria;
  - morada ou cidade;
  - contacto;
  - descrição;
  - comentários aprovados;
  - formulário de avaliação;
  - área futura para descontos e prémios.

### Pesquisa e filtros

O utilizador pode procurar restaurantes por texto e filtrar por categorias:

- Todos;
- Churrasco;
- Feijoada;
- Lanches;
- Doces;
- Bebidas.

### Avaliações

As avaliações enviadas pelos utilizadores não aparecem diretamente no app. Elas ficam pendentes até serem verificadas por um administrador.

Fluxo atual:

1. O utilizador envia uma avaliação.
2. A avaliação entra em `avaliacoes_pendentes`.
3. Um administrador pode aprovar, reprovar ou comunicar ao master.
4. Apenas avaliações aprovadas aparecem publicamente.

### Utilizadores

O perfil de utilizador permite guardar:

- foto por URL;
- nome completo;
- data de nascimento;
- email;
- contacto;
- NIF opcional;
- consentimento RGPD;
- pedido de apagamento de dados.

### Administradores

Existem perfis administrativos para restaurantes/parceiros e para administrador master.

O administrador de restaurante pode:

- gerir dados do estabelecimento;
- criar prémios;
- visualizar resgates;
- moderar comentários.

O administrador master pode:

- aprovar restaurantes pendentes;
- excluir restaurantes pendentes;
- adicionar novos emails master;
- validar conteúdos sensíveis.

## Identidade visual

A identidade visual segue o conceito **tropical sofisticado**, evitando um visual caricato ou excessivamente carnavalesco.

Paleta principal:

- Verde: `#1F6B4F`
- Verde escuro: `#0E4F38`
- Dourado: `#F4B400`
- Creme: `#FFF8EE`
- Castanho: `#5C3A21`
- Preto suave: `#1E1E1E`

O app usa:

- cards brancos arredondados;
- fundo creme;
- botões verdes;
- elementos dourados;
- imagem principal personalizada;
- ícones simples;
- layout adaptado para telemóveis.

## Tecnologias

- Kotlin
- Android XML Views
- Material Components
- Firebase Authentication
- Cloud Firestore
- Firebase Storage configurado
- Glide
- Gradle Kotlin DSL

## Estrutura do projeto

```text
app/
  src/main/java/pt/saborbrasileiro/app/
    MainActivity.kt
    LoginActivity.kt
    RegistoActivity.kt
    PerfilActivity.kt
    AdicionarRestauranteActivity.kt
    AvaliarRestauranteActivity.kt
    AdminMasterActivity.kt
    Restaurante.kt
    Avaliacao.kt
    Utilizador.kt
    Premio.kt
    ResgatePremio.kt

  src/main/res/
    layout/
    drawable/
    drawable-nodpi/
    values/

marketing/
firestore.rules
firebase.json
RELATORIO_FINAL_PROJETO.md
```

## Firebase

O projeto usa Firebase para autenticação e base de dados.

Coleções principais do Firestore:

- `utilizadores`
- `admin_masters`
- `restaurantes`
- `restaurantes_pendentes`
- `avaliacoes`
- `avaliacoes_pendentes`
- `avaliacoes_reprovadas`
- `comunicacoes_master`
- `premios`
- `resgates_premios`
- `pedidos_rgpd`

As regras do Firestore estão no ficheiro:

```text
firestore.rules
```

Sempre que as regras forem alteradas localmente, elas precisam ser publicadas novamente no Firebase Console.

## RGPD

O app inclui uma estrutura inicial para tratamento de dados conforme RGPD:

- aceite obrigatório no registo;
- consentimento no perfil;
- pedido de apagamento de dados;
- armazenamento de estado dos pedidos em Firestore;
- separação entre dados públicos e dados sensíveis.

Esta área ainda deve ser evoluída com uma política de privacidade completa dentro do app.

## Como executar o projeto

1. Clonar o repositório:

```bash
git clone https://github.com/AnneSenhor90/SaborBrasileiroPT.git
```

2. Abrir no Android Studio.

3. Confirmar que o ficheiro Firebase está presente:

```text
app/google-services.json
```

4. Sincronizar o Gradle.

5. Executar o app num emulador ou telemóvel Android.

## Build

Para compilar via terminal:

```bash
./gradlew assembleDebug
```

No Windows:

```bash
gradlew.bat assembleDebug
```

## Validações já realizadas

- `assembleDebug`: passou com sucesso.
- `testDebugUnitTest`: passou com sucesso.
- `lintDebug`: passou com sucesso, sem erros bloqueantes.

O Android Lint ainda aponta avisos de melhoria, principalmente relacionados a textos fixos em XML, autofill, acessibilidade e otimização de listas.

## Roadmap

Próximas melhorias previstas:

- concluir configuração Firebase/OAuth para o login Google autenticar em produção;
- melhorar recomendações com base nas buscas do utilizador;
- completar fluxo de descontos e prémios;
- permitir resgate de prémios pelo utilizador;
- filtrar comentários por restaurante/admin responsável;
- melhorar validação de NIF, telefone e datas;
- criar política RGPD completa;
- reduzir warnings do Android Lint;
- adicionar testes instrumentados;
- publicar uma primeira versão de teste.

## Login com Google

O app já possui a tela e o fluxo Android preparados para priorizar o login com conta Google.

Objetivo:

- facilitar a entrada no app;
- melhorar a identificação do utilizador;
- reduzir atrito no registo;
- aumentar a confiança nas avaliações;
- ajudar a personalizar buscas e recomendações;
- enriquecer a base de dados com validação do administrador master.

O login tradicional por email e senha poderá continuar como alternativa para quem não quiser ou não puder usar Google.

### Configuração necessária no Firebase

Para o login Google funcionar de ponta a ponta, ainda é necessário configurar o Firebase:

1. Ativar o provedor **Google** em Firebase Authentication.
2. Adicionar as chaves **SHA-1** e **SHA-256** do app Android nas definições do projeto Firebase.
3. Baixar novamente o ficheiro `google-services.json`.
4. Atualizar o valor `google_web_client_id` em `app/src/main/res/values/strings.xml` com o Web Client ID gerado pelo Firebase/Google Cloud.

Sem este Web Client ID, o app mostra uma mensagem a pedir a configuração Firebase em vez de iniciar a autenticação.

## Relatórios do projeto

O projeto possui relatórios de acompanhamento:

- `RELATORIO_FINAL_PROJETO.md`
- `RELATORIO_FINAL_PROJETO_SABOR_BRASILEIRO_PT.docx`

Eles descrevem o estado atual, funcionalidades implementadas, regras Firebase, identidade visual e próximos passos.

## Autoria

Projeto desenvolvido por **Anne Senhor** como app Android para descoberta de restaurantes brasileiros em Portugal.

## Licença

Este projeto ainda não possui uma licença definida. Antes de uso público, recomenda-se adicionar uma licença adequada ao objetivo do app.
