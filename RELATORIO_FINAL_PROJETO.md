# Relatorio final do projeto Sabor Brasileiro PT

Data da revisao: 2026-06-03
Ultima atualizacao: 2026-06-03, apos correcao visual dos botoes do perfil.

## Visao geral

O projeto foi evoluido para um app Android nativo em Kotlin + XML com identidade visual propria para descobrir restaurantes brasileiros em Portugal. A direcao aplicada foi tropical sofisticada, com tons quentes, fundo creme, verde principal, dourado de destaque, cards brancos, botoes arredondados e uma experiencia mais proxima de apps modernos de descoberta local.

O app esta estruturado com Firebase Authentication, Cloud Firestore, Glide para imagens por URL e telas separadas para login, registo, listagem, detalhes/avaliacoes, perfil, cadastro de restaurante e validacao master.

## Identidade visual aplicada

- Paleta base aplicada:
  - Verde principal: `#1F6B4F`
  - Verde escuro: `#0E4F38`
  - Dourado: `#F4B400`
  - Creme: `#FFF8EE`
  - Castanho: `#5C3A21`
  - Preto suave: `#1E1E1E`
- Criados fundos e componentes visuais:
  - cards brancos arredondados;
  - campos de texto claros;
  - botoes primarios verdes;
  - chips de categorias;
  - selo de avaliacao;
  - botao de voltar com fundo claro e borda;
  - placeholder visual para restaurantes sem imagem.
- A imagem principal do app foi atualizada com a arte enviada em `simbolo_app.jpeg`, aplicada no login como `logo_sabor_brasileiro.jpg`.

## Telas principais

### Login

- Tela inicial com logo grande do app.
- Campos de email e senha.
- Link para recuperacao de senha.
- Link para registo.
- Redirecionamento automatico para a Home quando o utilizador ja esta autenticado.

### Registo

- Criacao de conta com nome, email e senha.
- Escolha entre utilizador comum e restaurante/parceiro.
- Aceite obrigatorio da regra RGPD.
- Email `anne.kelly.senhor@gmail.com` tratado como admin master inicial.

### Home/Listagem

- Header verde com mensagem de descoberta.
- Pesquisa funcional por nome, cidade, categoria e descricao.
- Filtros por categoria:
  - Todos
  - Churrasco
  - Feijoada
  - Lanches
  - Doces
  - Bebidas
- Cards com imagem, nome, categoria, cidade, descricao, avaliacao e estado.
- O card inteiro agora abre detalhes do restaurante.
- Botao de adicionar restaurante mantido.

### Detalhes do restaurante

- A antiga tela de avaliar foi transformada numa tela de detalhes completa.
- Mostra:
  - foto/logo do restaurante;
  - nome;
  - categoria;
  - morada ou cidade;
  - contacto;
  - descricao;
  - area futura de descontos e premios;
  - comentarios aprovados;
  - formulario para enviar avaliacao.
- Avaliacoes continuam pendentes de aprovacao antes de ficarem publicas.

### Adicionar restaurante

- Tela redesenhada para ficar mais clara e humana.
- Campos:
  - nome;
  - cidade;
  - morada completa;
  - categoria;
  - contacto;
  - link da imagem/logo;
  - descricao.
- Se o utilizador for admin master, o botao mostra `Publicar restaurante`.
- Para outros perfis, mostra `Enviar para validacao`.
- Restaurantes enviados por utilizadores ficam em `restaurantes_pendentes`.

### Perfil

- Campos de perfil:
  - foto por URL;
  - nome completo;
  - data de nascimento;
  - email;
  - contacto;
  - NIF opcional.
- Botao `Guardar perfil`.
- Botao `Alterar senha por e-mail` corrigido para ficar verde com texto branco visivel.
- Secao RGPD com consentimento e pedido de apagamento de dados.
- Botao `Solicitar apagamento dos dados` corrigido para ficar verde com texto branco visivel.
- Botao `Sair da conta` agora mais visivel.
- Botao voltar com contraste corrigido.

### Perfil administrativo

- Campos de estabelecimento:
  - nome do estabelecimento;
  - data de fundacao;
  - NIF da empresa.
- Criacao de premios.
- Visualizacao de premios criados.
- Visualizacao de resgates.
- Moderacao de comentarios pendentes:
  - aprovar;
  - reprovar;
  - comunicar ao master.

### Admin master

- Lista de restaurantes pendentes.
- Aprovar restaurante pendente e publicar em `restaurantes`.
- Excluir restaurante pendente.
- Adicionar novos emails como admin master futuro.

## Firebase e base de dados

### Colecoes usadas

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

### Regras Firestore

As regras foram preparadas para proteger dados sensiveis:

- leitura publica permitida apenas onde faz sentido:
  - `restaurantes`;
  - `avaliacoes`;
  - `premios`.
- dados pessoais protegidos:
  - `utilizadores`;
  - `pedidos_rgpd`;
  - `resgates_premios`.
- validacoes protegidas:
  - `restaurantes_pendentes`;
  - `avaliacoes_pendentes`;
  - `avaliacoes_reprovadas`;
  - `comunicacoes_master`.
- regras reforcadas para evitar que um utilizador comum crie a propria conta como admin master.
- admin master inicial definido pelo email `anne.kelly.senhor@gmail.com`.

Importante: sempre que `firestore.rules` for alterado localmente, as regras precisam ser publicadas novamente no Firebase Console.

## Restaurantes e imagens

- Foi criada base inicial de restaurantes brasileiros conhecidos na zona do Porto/Grande Porto.
- O restaurante Maria Pitanga Matosinhos foi ajustado com dados reais conhecidos e logo enviada.
- O app usa `imagemUrl` com Glide.
- Quando a imagem falha, usa placeholder local.
- Maria Pitanga tem fallback especial com logo local.

## RGPD

- Registo exige aceite RGPD.
- Perfil permite guardar consentimento.
- Perfil permite solicitar apagamento dos dados.
- Pedidos RGPD ficam pendentes para tratamento pelo master.
- Texto de RGPD foi incluído nas telas de registo e perfil.

## Ajustes de seguranca

- Apenas `LoginActivity` permanece exportada por ser launcher.
- Telas internas foram protegidas com `exported=false`.
- Fluxos de admin master e validacao consultam o tipo de acesso.
- Firestore rules foram reforcadas contra criacao indevida de perfis master.
- Regras tambem foram reforcadas para evitar criacao de avaliacoes, restaurantes pendentes e resgates em nome de outro utilizador.

## Correcoes visuais finais

- Corrigido o problema de dois botoes verdes sem texto aparente na tela de perfil.
- O botao `Alterar senha por e-mail` agora usa fundo verde e texto branco.
- O botao `Solicitar apagamento dos dados` agora usa fundo verde e texto branco.
- A cor foi definida explicitamente no XML para evitar que o tema do Android aplique tint automatico e volte a esconder o texto.
- Apos a correcao, o projeto foi compilado novamente com sucesso.

## Verificacoes realizadas

- Build debug:
  - comando: `gradlew.bat assembleDebug`
  - resultado: sucesso.
  - ultima execucao apos a correcao dos botoes: sucesso.
- Testes unitarios:
  - comando: `gradlew.bat testDebugUnitTest`
  - relatorio gerado em `app/build/reports/tests/testDebugUnitTest/index.html`.
- Android Lint:
  - comando: `gradlew.bat lintDebug`
  - resultado: `0 errors, 198 warnings`.

Os avisos atuais do Lint nao bloqueiam o app. A maioria esta relacionada a textos fixos em XML, autofill hints, uso de `notifyDataSetChanged` e pequenos ajustes de performance/acessibilidade.

## Pontos ainda recomendados

- Mover textos fixos dos XML para `strings.xml`.
- Criar fluxo completo para o utilizador resgatar premios/descontos.
- Filtrar comentarios pendentes por restaurante/admin responsavel.
- Melhorar validacao de campos como telefone, NIF e datas.
- Adicionar testes instrumentados de telas principais.
- Testar visualmente em dispositivos reais de tamanhos diferentes.
- Criar politica RGPD completa em pagina dedicada.
- Publicar novamente as Firestore Rules sempre que forem ajustadas.

## Estado final

O app esta funcional para:

- login;
- registo;
- listagem de restaurantes;
- pesquisa;
- filtros;
- abertura de detalhes;
- envio de avaliacoes pendentes;
- perfil de utilizador;
- perfil administrativo;
- cadastro de restaurantes pendentes;
- aprovacao master;
- seguranca basica via Firebase Rules;
- identidade visual propria.

O projeto esta num bom ponto de prototipo funcional avancado. A proxima fase ideal e refinar textos, acessibilidade, testes em telemovel real e completar o fluxo de premios/descontos.
