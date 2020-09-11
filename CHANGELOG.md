# Últimas versões

##1.47.0
Adiciona query de recomendação de vendas default

## 1.46.1
Atualiza o campo link na query de recommendação de vendas
Mapea o subscriptionService para o troca de canais

## 1.46.0
Adiciona mapeamento dos campos de serviceId e subscriptionService para query de detalhes de vídeo.
Adiciona nova regra de uso de imagem de thumb do continue watching
Adiciona campo fullyWatchedThreshold para para marcação de vídeo completamente assistido.

## 1.45.0
Atualiza query de recomendação de vendas
Adiciona paginacao para query de programas das afiliadas
Altera a imagem de thumb do continue watching para exibir sempre a logo

## 1.44.0
Mapeia novos campos na query de afiliadas: content rating, content rating criteria. Esses campos são utilizar para preencher o outdoor da tela de detalhes dos programas.

## 1.43.0
Atualiza a query de epg mapeando cover

## 1.42.0
Adiciona novo campo no request de estado (acronym)

## 1.41.0
Unifica os repositórios States, Regions e Affiliates

## 1.40.0
Adiciona query para: lista de regiões, lista de estados e lista de programas das afiliadas
Mapea o novo campo em Broadcasts chamado ignoreAdvertisements

## 1.39.0
Migração de episódios

## 1.38.1
Altera retorno dos requests ao remote config para coletarmos um Map `Map<String, Configuration?>`
ao invés de um List `List<Configuration>`.

## 1.38.0
Adiciona query de configurações remotas 

## 1.37.0
Migra a query de cenas 


## 1.36.2
Mapeia campo `serviceId` na query de EpisodesWithRelatedExcerptsByDate


## 1.36.1
Resolve bug de query para próximo título recomendado ajustando tipo do parâmetro de scaleCover para 
mobile e tablet


## 1.35.0
Adiciona perPage e nextPage na query de últimos vídeos vistos.


## 1.34.0
Mapeia campo `transmissionId` na query de Broadcast


## 1.33.0
Adiciona query para buscar os canais


## 1.32.0
Adiciona novo destino (canais) para navegação estruturada


## 1.31.0
Adiciona novo destino (categorias) para navegação estruturada


## 1.30.0
Adiciona query para buscar o detlahes da oferta de vídeo da home
Adiciona query para buscar o detlahes da oferta de título da home


## 1.29.0
Adiciona query de estrutura da home
Adiciona limit para query de current slots do epg

## 1.28.0
Adiciona no request de broadcast a entidade de titulo com suas imagens: landscape, portraint e wide

## 1.27.0
Adiciona query buscar o detalhes da oferta de categorias
Adiciona trimmed logo no broadcast

## 1.26.0
Adiciona método que permite habilitar os logs dos requests realizado pela library


## 1.25.1
Adiciona urls do título para experimento AB usado na impressão e conversão
Adiciona nova regra ao parâmetro da logo no repositório de broadcast

## 1.25.0
Traz a correção feita na versão 1.22.1

## 1.24.0
Adiciona query de canais

## 1.23.0
Adiciona na query de busca o trimmed logo e image on air para trilho de canais

## 1.22.2
Separa o destaque premium de vendas da lista de ofertas

## 1.22.1
Remoção do locale e tenant da query de Sales -> landingPage
Remoção do parametro localeRepository da SalesRepository
Removido regras do produto da query de landingpage
Query de langingPage retorna apenas as ofertas do jarvis.

## 1.22.0
Adiciona query de recomendação de próximo título dado um offerId
Mapea campos `format` e `genresNames` para detalhes de vídeo

## 1.21.1
Remove campo "accessibleOffline" do `ContinueWatching` e passamos a utilizar o "accessibleOffline" que contem no `Title`
Adiciona teste unitário para evitar regressão dessa regra.

## 1.21.0
Mapea novo atributo "salesPageCallToAction" no nível de `Broadcasts` na query de `Channels`
Mapea novo atributo "logo" no nível de `Broadcasts` na query de `Channels`
Remove campo "slug" depreciado pelo Jarvis nas queries de `Channels` e `EPG`

## 1.20.0
Mapea querires referentes aos fluxos de Títulos
Adiciona query para busca de recomendação de títulos a partir de um OfferID
Adiciona testes unitários para ambos os cenários.

## 1.18.2
Corrige chamada para requisição de detalhes de um broadcast mesmo que as coordenadas (lat/long)
estejam nulas, fazendo assim, o BE inferí-las por IP.

## 1.18.1
Corrige scale usado na query de `searchTopHits`

## 1.18.0
Adiciona novo campo `externalLinkLabel` no modelo `PayTv`

## 1.17.0
Mapeamento de campos `payTvInfoLink` e `payTvExternalLink` na query de canais
Criando modelo `PayTv` para centralizar atributos de operadora no `Channel`

## 1.16.0
Mapeamento de campos novos na query de mais vistos da busca

## 1.15.1
HotFix para devolver zero ao invéz de um quando o `season` de um `Episode` for nulo

## 1.15.0
Adiciona query títulos recomendados mais vistos
Adiciona header `userId para identificação do usuário nos requests.

## 1.14.0
Adiciona query de Video e NextVideo
Mapeia novos campos no modelo de Video.
Ajustes no VideoRepository.

## 1.13.0
Mapeia campo requireUserTeam no modelo de Channel

## 1.12.0
Adiciona query para listagem das categorias.

## 1.11.0
Adiciona query para detalhes do vídeo e próximo vídeo.

## 1.10.2
Altera o método lastedVideo para receber o glbId.

## 1.10.1
Remove o call timeout que estoura o tempo do request em redes lenta.

## 1.10.0
Altera mapeamento do campo pathUrl do objeto de Experimento, 
o valor é obtido através do campo url dentro do objeto Title.

## 1.9.0
Adiciona query para buscar os últimos vídeos vistos do usuário

## 1.8.0
Mapeando campo `titleId` no modelo do EPG
Atualiza contrato das queries de busca
Adiciona testes unitarios no repositorio de busca

## 1.7.2
Mapeando novos campos `payTvUsersMessage` e `payTvServiceId` no modelo de Channel

## 1.7.1
Mapeando campo salesPageId no modelo de Channel

## 1.7.0
Adiciona testes unitários para repositório de locale
Adiciona titleId na query de epg

## 1.6.0
Adiciona testes unitários para repositório de usuário
Renomeado withDVRMediaId, withoutDVRMediaID e promotionalMediaID para respectivamente withDVRVideoId, withoutDVRVideoId e promotionalVideoId

## 1.5.0
Remove a política de cache do apollo e adiciona o cache do okhttp para sempre buscar da rede, caso ela falhe busque do cache respeitando o cache control.

## 1.4.2
Corrige a política de cache para para sempre buscar da rede, caso ela falhe busque do cache respeitando o cache control.
Agora é permitido passar seu apollo client, caso você não queria utilizar as configurações do jarvis client ou mesmo para teste instrumentados/unitários.

## 1.4.1
Movendo propriedade withoutDVRMediaId do modelo de Channel para o modelo de Media

## 1.4.0
Adição da propriedade `authorizedForUser` no objeto `Channel`, será utilizado para guardar valor relacionado à autorização do usuário a consumir mídia do canal.
Renomeamento da propriedade `failedDetailsRequest` do objeto `Channel` para `hasError`.

## 1.3.0
Atualização do apollo para versão 1.3.0

## 1.2.2
Remoção do parâmetro `salesServiceId` da query de `Broadcast`, pois este não é mais utilizado para validação da disponibilidade da mídia.

## 1.2.1
Ajuste no request de getEpgs devido a problemas de parse de Date pra String.
Mapeando campo headline no getChannel.

## 1.2.0
Adiciona novos repositórios: Vídeo, Título, Localização, Trechos, Episódios, Categorias, Calendário

## 1.1.1
Adiciona novo tratamento para receber os novos canais do BBB.
Mapeamento do campo `headline` para tratar os nomes dos canais do mesmo.

## 1.1.0
Altera contrato de inicialização do JarvisClient
Adiciona testes unitários
Remove utilitários de Schedulers

## 1.0.4
Adiciona no contrato de inicialização uma baseURL para fins de tests

## 1.0.3
Adiciona novos repositórios: busca, continuar assistindo, minha lista.

## 1.0.2
Remove o campo transmissionId da query

## 1.0.1
Adiciona o recebimento do apollo client na inicialização do biblioteca para permitir teste instrumentados

## 1.0.0
Altera o nome do projeto para jarvis client que encapsula outras chamadas para o jarvis.
Corrige encadeamento de datas nos requests de EPG

## 0.8.5
Permite latitude e longitude a receber valores nulos.

## 0.8.4
Altera lógica de `Schedule` de requests, criando faixas de atualizações a cada 1 minuto.

## 0.8.3
Remoção do `TenantVO`, e seu uso substituido por `String`.

## 0.8.2
Mapeia novos `Tenants`, `GLOBOPLAY_BETA` e `PHILOS`

## 0.8.1
Mapeia contrato de `Affiliate` para tratamentos de Motorola DTV

## 0.8.0
Torna atributos do tipo `date` nullables nos modelos `ChannelSlotVO` e `EpgSlotVO`
Ajusta serialização de modelos

## 0.7.0
Torna os modelos parcelable 

## 0.6.0
Adiciona custom headers 

## 0.5.0
Adiciona image on air na lista de canais

## 0.4.2
Otimiza chamada ao backend quando a `latitude` e `longitude` forem nulas na chamada de `getChannel`

## 0.4.1
Corrige problema de ordenamento da lista de canais
Corrige problema do proguard ofuscar classes erradas

## 0.4.0
Adiciona extension para orientacao da tela

## 0.3.1
Corrige regras do proguard

## 0.3.0
Adiciona o parse do atríbuto durationInMinutes

## 0.2.3
Corrige problema do composite disposable não está sendo reaproveitado
Adiciona url de produção

## 0.2.2
Ajusta o scale do image on air

## 0.2.1
Ajusta o problema de envia uma latitude e longitude inválida para o jarvis
Ajusta o scale do logo
Adiciona nome do canal e logo para Epg

## 0.2.0
Adiciona request do title cover para tela de detalhes do epg
Adiciona metodo para buscar a lista de epg baseado numa data

## 0.1.0
Adiciona request para lista de epg
Adiciona lógica para formatar a latitude e longitude
Atualiza o schema para o novo contrato
Adiciona novas scales
Adiciona lógica para os custom scaler (Date e DateTime)
Altera os request de POST para GET
Atualiza o client do apollo para versão 1.0.0

## 0.0.16-alpha
Ajustando qualidade das logos

## 0.0.15-alpha
Atualizando o tenant globolive para globo-live

## 0.0.14-alpha
Mudança no campo: `availableInLocation` para `geoblocked` 

## 0.0.13-alpha
Update no schema do jarvis e nos campos programName e Description dos slots do epg 

## 0.0.12-alpha
Corrigindo Tenant do Globo Play

## 0.0.11-alpha
Corrigindo possível leak de memória

## 0.0.10-alpha
Logo dos canais com scaling de acordo com o device e densidade de pixels

## 0.0.9-alpha
Adição do campo de exhibitionClassifications

## 0.0.8-alpha
Request Com localização para Broadcasts com geofence 
Adição do campo availableInLocation, para saber se a mídia está disponível na localização recebida

## 0.0.7-alpha
Rollback para utilização de http 

## 0.0.6-alpha
Atualização no schema do Apollo

## 0.0.5-alpha
Muda a url para utilizar https

## 0.0.4-alpha
Adicionado caso de uso de atualização dos broadcasts que estão para acabar
Adição dos campos de media, serviceId e texto promocional 

## 0.0.3-alpha
Adição dos campos de slug do canal e id de mídia promocional 

## 0.0.2-alpha
Integração com Persisted Query

## 0.0.1-alpha