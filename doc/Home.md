## Estrutura da Home

Recupera a estrutura da home, retornando a lista de ofertas e destaque premium. O método aceita os seguintes parâmetros:

* `pageId`: Identificador da home.
* `highlightLogoScale`: Escala da logo do destaque premium.
* `Callback.Home`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.home.structure(pageId, highlightLogoScale, object : Callback.Home {
    override fun onStructureSuccess(triple: Triple<String, HomeQuery.PremiumHighlight?, List<HomeQuery.OfferItem>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<CategoryDetails> = JarvisClient.home.structure(pageId, highlightLogoScale)
```




## Detalhes Lista de Categoria

Recupera a lista de categorias baseado no identificador da oferta. O método aceita os seguintes parâmetros:

* `offerId`: Identificador da oferta.
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Categories`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.home.categories(offerId,  page, perPage, object : Callback.Offer {
    override fun onOfferCategoriesSuccess(triple: Triple<Int?, Boolean, List<Category>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<CategoryDetails> = JarvisClient.home.categories(offerId, page, perPage)
```


