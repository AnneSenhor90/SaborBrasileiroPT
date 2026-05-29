# Publicar regras do Firestore

O projeto já tem as regras em `firestore.rules` e a configuração em `firebase.json`.

## Opção recomendada: Firebase Console

1. Abrir o Firebase Console.
2. Escolher o projeto `sabor-brasileiro-pt`.
3. Ir a `Firestore Database`.
4. Abrir o separador `Rules`.
5. Substituir o conteúdo pelas regras do ficheiro `firestore.rules`.
6. Clicar em `Publish`.

## Opção via terminal

Se tiver Firebase CLI instalado e sessão iniciada:

```powershell
firebase login
firebase use sabor-brasileiro-pt
firebase deploy --only firestore:rules
```

## Verificação rápida

Depois de publicar, uma leitura pública da coleção `utilizadores` deve falhar. A Home continuará a ler `restaurantes`, porque restaurantes aprovados são públicos.

Coleções públicas:
- `restaurantes`
- `avaliacoes`
- `premios`

Coleções protegidas:
- `utilizadores`
- `restaurantes_pendentes`
- `avaliacoes_pendentes`
- `avaliacoes_reprovadas`
- `comunicacoes_master`
- `pedidos_rgpd`
- `resgates_premios`
