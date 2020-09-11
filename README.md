# Jarvis Client Android

É um cliente http de comunicação com a API do `Jarvis`.

## Ultimas versões

Veja o [changelog](CHANGELOG.md)

## Instalação do componente


Adicione a seguinte dependências no build.gradle do seu aplicativo

```
    dependencies {
        implementation 'com.globo.jarvis:<most_recent_lib_version>'
    }
```


## Configurando Repositório
Após baixar a dependência, configure o repositório onde a mesma está hospedada. Adicione ao `build.gradle`: 

``` gradle![]
maven {
   url 'https://globocom.jfrog.io/globocom/jarvis-android-release-local/'
   credentials {
      username artifactory_user
      password artifactory_key
   }
}
```

## Utilização

Antes de chamar o `JarvisClient` é inicializar a dependência no `onCreate` da sua aplicação:

``` kotlin
JarvisClient.initialize(object: Settings(){}, ApolloClient);
```

O Settings é um interface que deve ser implementada a fim de devolver items utilizados na configuração da biblioteca. A interface contém as seguites chaves:
   * `glbId` Identificador do token da sessão do usuário.
   * `userId` Identificador do usuário.
   * `anonymousUserId` Identificador do anônimo.
   * `timeout` Tempo de escrita, leitura e conexão do request.
   * `tenant` Indentifica qual a aplicação está requisitando dados ao servidor. (`globo-play`, `globo-play-beta`, `globo-live`)
   * `device`  Identifica qual a plataforma. (`MOBILE`, `TABLET`, `TV`)
   * `version`  Identificador da versão do produto.
   * `application`  Contexto da aplicaçõa utilizado para criar o arquivo de cache. 
   * `enableLog`  Habilitar os log's das requisições feitas.
   * `environment`  Indentifica para qual o ambiente do `Jarvis` as requisições serão feitas. (`PRODUCTION`, `BETA`) 
   * `apolloClient` Cliente do apollo customizado pelo cliente.

## Repositórios
- [**Capítulo**](doc/Chapter.md)

- [**Calendário**](doc/Calendar.md)

- [**Categorias**](doc/Categories.md)

- [**Broadcast**](doc/Broadcast.md)
 
- [**Home**](doc/Home.md)

- [**Canais**](doc/Channels.md)

- [**Epg**](doc/Epg.md)

- [**Episódios**](doc/Episode.md)

- [**Trechos**](doc/Excerpt.md)

- [**Recomendação**](doc/Recommendation.md)

- [**Localização**](doc/Locale.md)

- [**Busca**](doc/Search.md)

- [**Título**](doc/Title.md)

- [**Usuário**](doc/User.md)

- [**Vídeo**](doc/Video.md)

- [**Cenas**](doc/Scenes.md)

- [**Configurações Remotas**](doc/RemoteConfig.md)

- [**Afiliadas**](doc/Affiliates.md)
