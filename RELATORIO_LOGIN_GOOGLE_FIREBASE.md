# Relatorio de ativacao do Login Google

Data: 2026-06-11

## Estado atual

O app ja possui o fluxo Android e o visual preparados para o login com Google:

- botao principal `Entrar com a minha conta Google`;
- alternativa por email e senha;
- dependencia `play-services-auth`;
- chamada ao Google Sign-In;
- autenticacao Firebase com `GoogleAuthProvider`;
- criacao/atualizacao do perfil do utilizador no Firestore apos login;
- uso do nome e foto da conta Google quando disponiveis.

## O que ainda falta fora do codigo

O ficheiro atual `app/google-services.json` ainda nao possui `oauth_client`.

Isto significa que o Firebase ainda nao esta configurado completamente para login Google neste app Android. Sem essa configuracao, o app compila e mostra o botao, mas nao consegue autenticar de ponta a ponta.

## Dados do app Android

Package name:

```text
pt.saborbrasileiro.app
```

Firebase project id:

```text
sabor-brasileiro-pt
```

## Chaves SHA para configurar no Firebase

Estas chaves foram geradas com:

```bash
gradlew.bat signingReport --console=plain
```

### Debug

Store:

```text
C:\Users\annin\.android\debug.keystore
```

Alias:

```text
AndroidDebugKey
```

SHA-1:

```text
EB:EC:7C:27:A3:E2:3C:A8:7F:32:C5:F5:54:D8:B5:91:0E:5A:FD:90
```

SHA-256:

```text
0B:FE:01:AA:64:57:BB:F1:C2:C7:4C:F3:4B:C1:EF:84:0C:9C:15:9B:D8:41:83:B6:FA:66:8A:96:41:40:28:ED
```

## Passos para ativar no Firebase Console

1. Abrir o Firebase Console.
2. Entrar no projeto `sabor-brasileiro-pt`.
3. Ir em `Authentication`.
4. Abrir `Sign-in method`.
5. Ativar o provedor `Google`.
6. Ir em `Project settings`.
7. Abrir o app Android com package `pt.saborbrasileiro.app`.
8. Adicionar o SHA-1 e SHA-256 acima.
9. Guardar as alteracoes.
10. Baixar novamente o ficheiro `google-services.json`.
11. Substituir o ficheiro em:

```text
app/google-services.json
```

12. Obter o Web Client ID no Firebase/Google Cloud.
13. Preencher em:

```xml
<string name="google_web_client_id">COLE_AQUI_O_WEB_CLIENT_ID</string>
```

Arquivo:

```text
app/src/main/res/values/strings.xml
```

14. Recompilar o app:

```bash
gradlew.bat assembleDebug
```

## Resultado esperado apos configurar

Ao tocar em `Entrar com a minha conta Google`, o app deve:

1. abrir a janela de escolha da conta Google;
2. retornar ao app apos a conta ser escolhida;
3. autenticar no Firebase;
4. criar ou atualizar o documento do utilizador em `utilizadores`;
5. abrir a Home.

## Observacao importante

Para testes no telemovel com build debug, as chaves SHA acima sao suficientes.

Para uma versao publicada na Play Store ou gerada com keystore de release, sera necessario gerar e adicionar tambem as chaves SHA da assinatura de release.
