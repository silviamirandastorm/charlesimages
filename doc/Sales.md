## Página de vendas

Recupera a página de vendas com as ofertas disponíveis. A query aceita os parâmetros:

salesId: "Id da página de vendas. Quando não informado utiliza o default"
titleId: "Id do título, que pode ser nulo",
scale: "Tamanho das imagens para mobile",
scaleCover: "Tamanho das imagens pra tablet",
tenantDefault: "Id default da aplicação",
countryCodeDefault: "País do usuário. Por default é BR"
page: Quantidade de páginas,
perPage: Quantidade de itens por página

``` kotlin
JarvisClient.sales.landingPage(
    salesId,
    titleId,
    scale,
    scaleCover,
    tenantDefault,
    countryCodeDefault,
    page,
    perPage,
    object : Callback.Sales {
        override fun onLandingPageSuccess(offerList: List<Offer>) { }

        override fun onFailure(throwable: Throwable) { }
    }
)
```
ou
``` kotlin
val observable = Observable<List<Offer>> = structure(salesId, titleId, scale, scaleCover) =
    JarvisClient.sales.landingPage(
        salesId,
        titleId,
        scale,
        scaleCover,
        tenantDefault,
        countryCodeDefault,
        page,
        perPage
    )
```

Scale e as respectivas conversões

| Dimensão  | Mobile | Tablet Portrait | Tablet Landscape | TV |
|---|---|---|---|---|
| 0.75X    | CoverPortraitScales.X288 | CoverLandscapeScales.X276 | CoverLandscapeScales.X464 | --
| 1x       | CoverPortraitScales.X384 | CoverLandscapeScales.X348 | CoverLandscapeScales.X552 |  --
| 1.5X     | CoverPortraitScales.X576 | CoverLandscapeScales.X464 |  CoverLandscapeScales.X720 | --
| 2x       | CoverPortraitScales.X768 | CoverLandscapeScales.X552  |  CoverLandscapeScales.X928 | --
| 3x       | CoverPortraitScales.X1152 | CoverLandscapeScales.X653 |  CoverLandscapeScales.X1392 | --
| 4x       | CoverPortraitScales.X1536 | CoverLandscapeScales.X828 |  CoverLandscapeScales.X1635 | --
| HD       | -- | -- |  -- | CoverWideScales.X696
| FULL_HD  | -- | -- |  -- | CoverWideScales.X928
| 4K       | -- | -- |  -- | CoverWideScales.X1392