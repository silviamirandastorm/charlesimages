## Busca de Títulos Recomendados
Para efetuar a consulta de uma lista de títulos recomendados através de uma id de oferta.

* `titleId`: Id do título para criar contexto da consulta.
* `offerId`: Id da oferta para consulta.
* `scale`: Tamanho do poster do título. Para recuperar a scale, basta utilizar `context`.scale().
* `page`: Número da página com os itens, para efeitos de paginação. Valor default 1.
* `perPage`: Número de items recuperado por página, tendo o valor default como 12 items.
* `Callback.TitlesSuggestion`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

```kotlin
JarvisClient.suggest.titleSuggestByOfferId(
    titleId,
    offerId,
    scale,
    page,
    perPage,
    object : Callback.TitlesSuggestion {
        override fun onAllSuccess(listOfRecommendedTitles: Pair<List<RecommendedTitleOffer>, AbExperiment?>) {}
        
        override fun onFailure(throwable: Throwable) { }
    }
)
```

ou

```kotlin
val observable : Observable<Pair<List<RecommendedTitleOffer>, AbExperiment?>> = 
    JarvisClient.suggest.titleSuggestByOfferId(
        titleId,
        offerId,
        scale,
        page,
        perPage,
        object : Callback.TitlesSuggestion {
            override fun onAllSuccess(listOfRecommendedTitles: Pair<List<RecommendedTitleOffer>, AbExperiment?>) {}
        
            override fun onFailure(throwable: Throwable) { }
        }
)
```

## Busca de Título Recomendados Mais Vistos

Busca de títulos recomendados mais vistos para `TV`. O método aceita os seguintes parâmetros:

* `Scale`: Tamanho do poster do título. Para recuperar a scale, basta utilizar `context`.scale().
* `ScaleCover`: Tamanho do poster do título. Para recuperar a scale, basta utilizar `context`.scale().
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Recommendations`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

#### Logo

| Dimensão  | Mobile | Tablet | TV |
|---|---|---|---|
| 0.75X    | -- | -- | --
| 1x       | -- |  -- |  --
| 1.5X     | -- | -- |  --
| 2x       | -- | --  |  --
| 3x       | -- | -- |  --
| HD       | -- | -- |  x928px
| FULL_HD  | -- | -- |  x1080px
| 4K       | -- | -- |  x1856px

#### Cover

| Dimensão  | Mobile | Tablet | TV |
|---|---|---|---|
| 0.75X    | -- | --| --
| 1x       | -- |  -- |  --
| 1.5X     | -- | -- |  --
| 2x       | -- | --  |  --
| 3x       | -- | -- |  --
| HD       | -- | -- |  146x216
| FULL_HD  | -- | -- |  324x219
| 4K       | -- | -- |  437x648


``` kotlin
JarvisClient.suggest.titlesTopHits(scale, scaleCover, perPage, object : Callback.Recommendations {
    override fun onRecommendedChannels(recommendationList: List<Recommendation>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<List<Recommendation>> = JarvisClient.suggest.titlesTopHits(scale, scaleCover, perPage)
```

## Busca de Título Recomendado para plugin de final de vídeo do player para tela de vídeo fullscreen

Busca de Título Recomendado para plugin de final de vídeo do player para  nova tela de vídeo fullscreen `mobile`. O método aceita os seguintes parâmetros:

* `titleId`: Id do título para criar contexto da consulta.
* `format`: TitleFormat do título para criar contexto da consulta.
* `group`: SuggestGroup da oferta para criar contexto da consulta.
* `scaleCover`: Tamanho do poster do título. Para recuperar a scale, basta utilizar `context`.scale().
* `Callback.Recommendations`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

#### ScaleCover

| Dimensão  | Mobile | Tablet Portrait | Tablet Landscape | TV |
|---|---|---|---|---|
| 0.75X    | CoverPortraitScales.X288 | CoverLandscapeScales.X276 | CoverLandscapeScales.X464 | --
| 1x       | CoverPortraitScales.X384 | CoverLandscapeScales.X348 | CoverLandscapeScales.X552 |  --
| 1.5X     | CoverPortraitScales.X576 | CoverLandscapeScales.X464 |  CoverLandscapeScales.X720 | --
| 2x       | CoverPortraitScales.X768 | CoverLandscapeScales.X552  |  CoverLandscapeScales.X928 | --
| 3x       | CoverPortraitScales.X1152 | CoverLandscapeScales.X828 |  CoverLandscapeScales.X1392 | --
| HD       | -- | -- |  -- | CoverWideScales.X696
| FULL_HD  | -- | -- |  -- | CoverWideScales.X928
| 4K       | -- | -- |  -- | CoverWideScales.X1392


``` kotlin
JarvisClient.suggest.nextTitle(titleId, format, group, scaleCover, object : Callback.Recommendations {
    override fun onRecommendedTitle(pair: Pair<AbExperiment?, Recommendation>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Pair<AbExperiment?, Recommendation>> = JarvisClient.suggest.nextTitle(titleId, format, group, scaleCover)
```

## Busca de Título Recomendado para um conteúdo

Busca de Título Recomendado para um conteúdo. O método aceita os seguintes parâmetros:

* `offerId`: Id da oferta para um conteúdo.
* `titleId`: Id do título para criar contexto da consulta.
* `scaleCover`: Tamanho do poster do título. Para recuperar a scale, basta utilizar `context`.scale().
* `Callback.Recommendations`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

#### ScaleCover

| Dimensão  | Mobile | Tablet Portrait | Tablet Landscape | TV |
|---|---|---|---|---|
| 0.75X    | CoverPortraitScales.X288 | CoverLandscapeScales.X276 | CoverLandscapeScales.X464 | --
| 1x       | CoverPortraitScales.X384 | CoverLandscapeScales.X348 | CoverLandscapeScales.X552 |  --
| 1.5X     | CoverPortraitScales.X576 | CoverLandscapeScales.X464 |  CoverLandscapeScales.X720 | --
| 2x       | CoverPortraitScales.X768 | CoverLandscapeScales.X552  |  CoverLandscapeScales.X928 | --
| 3x       | CoverPortraitScales.X1152 | CoverLandscapeScales.X828 |  CoverLandscapeScales.X1392 | --
| HD       | -- | -- |  -- | CoverWideScales.X696
| FULL_HD  | -- | -- |  -- | CoverWideScales.X928
| 4K       | -- | -- |  -- | CoverWideScales.X1392


``` kotlin
JarvisClient.suggest.nextTitle(offerId, titleId, scaleCover, object : Callback.Recommendations {
    override fun onRecommendedTitle(recommendation: Recommendation) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Recommendation> = JarvisClient.suggest.nextTitle(offerId, titleId, scaleCover)
```