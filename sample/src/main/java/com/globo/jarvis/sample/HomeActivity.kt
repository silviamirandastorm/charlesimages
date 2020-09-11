package com.globo.jarvis.sample

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.globo.jarvis.Callback
import com.globo.jarvis.JarvisClient
import com.globo.jarvis.home.HomeQuery
import com.globo.jarvis.model.*
import com.globo.jarvis.model.Locale
import com.globo.jarvis.type.*
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity(), HomeAdapter.OnClickListener {
    private var progressDialog: ProgressDialog? = null
    private val itemList: List<String> =
        listOf(
            "Broadcast",
            "EPG",
            "Busca",
            "Busca Títulos",
            "Busca Videos",
            "Busca Canais",
            "Minha Lista",
            "Continuar Assistindo",
            "Top Hits",
            "Títulos Recomendados Mais Vistos",
            "Busca Global",
            "Sales",
            "Canais com detalhes",
            "Todos Canais",
            "Recomendação Titulo",
            "Recomendação Titulo Infantil",
            "Detalhes de Vídeo",
            "Oferta Lista de Categorias",
            "Recomendação de Vendas",
            "Broadcast MediaIds",
            "Broadcast Current Slots",
            "Estrutura Home",
            "Calendário",
            "Calendário - Trechos por data",
            "Episódio - Detalhes",
            "Episódio - Detalhes c/ Season",
            "Cenas",
            "Cenas c/ Season",
            "Cenas s/ Detalhes Season",
            "Cenas s/ Detalhes",
            "Configurações Remotas",
            "Lista de estados",
            "Lista de regiões",
            "Lista de regiões Por Estado",
            "Programas das Afiliadas",
            "Recomendação de Vendas Default",
            "Localização",
            "Título - Origin ProgramID",
            "Título - Formato do título",
            "Capítulo - Listagem por data",
            "Capítulo - Listagem"
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(baseContext)
            adapter = HomeAdapter(itemList, this@HomeActivity)
            recyclerView.adapter = adapter
        }
    }

    override fun click(view: View?, position: Int) {
        when (position) {
            0 -> {
                progressDialog = progressDialog("Buscando Broadcast")
                JarvisClient.broadcast.all(
                    -23.0029364,
                    -43.3239079,
                    BroadcastChannelLogoScales.X285.rawValue(),
                    BroadcastChannelTrimmedLogoScales.X224.rawValue(),
                    BroadcastImageOnAirScales.X720.rawValue(),
                    CoverPortraitScales.X1536.rawValue(),
                    2,
                    object : Callback.Broadcasts {
                        override fun onBroadcastsSuccess(broadcastVOList: List<Broadcast>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(broadcastVOList)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            1 -> {
                progressDialog = progressDialog("Busca lista de epgs")
                JarvisClient.epg.all(
                    -23.0029364,
                    -43.3239079,
                    BroadcastChannelLogoScales.X285.rawValue(),
                    BroadcastImageOnAirScales.X720.rawValue(),
                    CoverWideScales.X928.rawValue(),
                    Date(),
                    object : Callback.Epgs {
                        override fun onEpgListSuccess(epgList: List<Epg>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(epgList)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            2 -> {
                progressDialog = progressDialog("Buscando")
                JarvisClient.search.all(
                    "Globo",
                    MobilePosterScales.X4.rawValue(),
                    BroadcastImageOnAirScales.X720.rawValue(),
                    BroadcastChannelTrimmedLogoScales.X42.rawValue(),
                    1,
                    12,
                    searchCallback = object : Callback.Search {
                        override fun onSearchSuccess(triple: Triple<SearchTitles, SearchChannel, SearchVideos>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(triple)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            3 -> {
                progressDialog = progressDialog("Buscando Títulos")
                JarvisClient.search.titles(
                    "Globo Esporte",
                    MobilePosterScales.X4.rawValue(),
                    1,
                    12,
                    searchCallback = object : Callback.Search {
                        override fun onSearchTitlesSuccess(searchTitles: SearchTitles) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(searchTitles)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            4 -> {
                progressDialog = progressDialog("Buscando Videos")
                JarvisClient.search.videos(
                    "Globo Esporte",
                    BroadcastImageOnAirScales.X720.rawValue(),
                    BroadcastChannelTrimmedLogoScales.X42.rawValue(),
                    1,
                    12,
                    searchCallback = object : Callback.Search {
                        override fun onSearchVideosSuccess(searchVideos: SearchVideos) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(searchVideos)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            5 -> {
                progressDialog = progressDialog("Buscando Canais")
                JarvisClient.search.channels(
                    "Globo",
                    BroadcastChannelTrimmedLogoScales.X42.rawValue(),
                    1,
                    12,
                    searchCallback = object : Callback.Search {
                        override fun onSearchChannelsSuccess(searchChannel: SearchChannel) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(searchChannel)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            6 -> {
                progressDialog = progressDialog("Buscando Minha Lista")
                JarvisClient.user.myList(
                    MobilePosterScales.X4.rawValue(),
                    1,
                    12,
                    userCallback = object : Callback.User {
                        override fun onMyListSuccess(myList: MyList) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(myList)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            7 -> {
                progressDialog = progressDialog("Continuar Assistindo")
                JarvisClient.user.lastVideos(
                    JarvisApplication.GLB_ID,
                    MobilePosterScales.X4.rawValue(),
                    userCallback = object : Callback.User {
                        override fun onMyLastedWatchedVideosSuccess(triple: Triple<Int?, Boolean, List<ContinueWatching>>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(triple)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            8 -> {
                progressDialog = progressDialog("Buscando Top Hits")
                JarvisClient.search.searchTopHits(
                    scale = MobilePosterScales.X4.rawValue(),
                    searchCallback = object : Callback.Search {
                        override fun onTopHitsSuccess(searchTopHitsList: List<SearchTopHits>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(searchTopHitsList)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            9 -> {
                progressDialog = progressDialog("Buscando Títulos Recomendados Mais Vistos")
                JarvisClient.suggest.titlesTopHits(
                    scale = TVLogoScales.X3.rawValue(),
                    coverScale = CoverLandscapeScales.X2160.rawValue(),
                    perPage = 12,
                    recommendationCallback = object : Callback.Recommendations {
                        override fun onRecommendedChannels(recommendationList: List<Recommendation>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(recommendationList)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            10 -> {
                progressDialog = progressDialog("Busca Global")
                JarvisClient.search.global(
                    "Globo Esporte",
                    CoverLandscapeScales.X2160.rawValue()
                ).also {
                    dialogDismiss(progressDialog)
                    AlertDialog.Builder(this@HomeActivity)
                        .setMessage(
                            GsonBuilder()
                                .setPrettyPrinting()
                                .create()
                                .toJson(it)
                        )
                        .setCancelable(true)
                        .show()
                }
            }

            11 -> {
                progressDialog = progressDialog("Buscando página de vendas")
                JarvisClient.sales.landingPage(
                    salesId = "globoplay",
                    titleId = "wBJQsQRHZd",
                    scale = MobilePosterScales.X4.rawValue(),
                    coverScale = CoverPortraitScales.X576.rawValue(),
                    page = 1,
                    perPage = 10,
                    salesCallback = object : Callback.Sales {
                        override fun onLandingPageSuccess(triple: Triple<Offer, List<Offer>, String?>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(triple)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                )
            }

            12 -> {
                progressDialog = progressDialog("Buscando canais com detalhes")
                JarvisClient.channels.all(
                    broadcastChannelFilters = BroadcastChannelFilters.WITH_PAGES,
                    broadcastChannelTrimmedLogoScales = BroadcastChannelTrimmedLogoScales.X224.rawValue(),
                    channelsCallback = object : Callback.Channels {
                        override fun onChannelsSuccess(triple: Triple<Int?, Boolean, List<Channel>>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(triple.third)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                )
            }

            13 -> {
                progressDialog = progressDialog("Buscando todos canais")
                JarvisClient.channels.all(
                    broadcastChannelTrimmedLogoScales = BroadcastChannelTrimmedLogoScales.X168.rawValue(),
                    channelsCallback = object : Callback.Channels {
                        override fun onChannelsSuccess(triple: Triple<Int?, Boolean, List<Channel>>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(triple.third)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                )
            }

            14 -> {
                progressDialog = progressDialog("Buscando Título Recomendado")
                JarvisClient.suggest.nextTitle(
                    titleId = "fhmwKtSNxG",
                    format = TitleFormat.SERIES,
                    group = SuggestGroups.PLAY_NEXT,
                    coverScale = CoverWideScales.X1392.rawValue(),
                    recommendationCallback = object : Callback.Recommendations {

                        override fun onRecommendedTitle(pair: Pair<AbExperiment?, Recommendation>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(pair.second) + "AbExperiment: " + pair.first.toString()
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onRecommendedTitle(recommendation: Recommendation) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(recommendation)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            15 -> {
                progressDialog = progressDialog("Buscando Título Recomendado Infantil")
                JarvisClient.suggest.nextTitle(
                    offerId = "c3dc91b8-4e2f-4ffc-ace3-def140a1bd11",
                    titleId = "Gf1Tq7TGhf",
                    coverScale = CoverWideScales.X1392.rawValue(),
                    recommendationCallback = object : Callback.Recommendations {

                        override fun onRecommendedTitle(recommendation: Recommendation) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(recommendation)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            16 -> {
                progressDialog = progressDialog("Buscando Detalhes de Vídeo")
                JarvisClient.video.details(
                    videoId = "8487542",
                    posterScale = MobilePosterScales.X4.rawValue(),
                    videosCallback = object : Callback.Videos {

                        override fun onDetailsSuccess(video: Video) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(video)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            17 -> {
                progressDialog =
                    progressDialog("Buscando Detalhes da Oferta de Lista de  Categorias")
                JarvisClient.home.categories(
                    "12e8748f-89ce-46c1-b380-48bf5002f9ef",
                    1,
                    20,
                    object : Callback.Home {
                        override fun onOfferCategoriesSuccess(triple: Triple<Int?, Boolean, List<Category>>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(triple)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            18 -> {
                progressDialog = progressDialog("Buscando Recomendação de vendas")
                JarvisClient
                    .sales.recommendation(
                    serviceId = "mais-canais",
                    trimmedLogoScale = BroadcastChannelTrimmedLogoScales.X42.rawValue(),
                    salesCallback = object : Callback.Sales {

                        override fun onRecommendationSuccess(
                            salesRecommendation: SalesRecommendation
                        ) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(salesRecommendation)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            19 -> {
                progressDialog = progressDialog("Buscando Broadcast Media Ids...")
                JarvisClient.broadcast.mediaIds(callback = object : Callback.BroadcastMediaIds {
                    override fun onBroadcastMediaIdsSuccess(mediaIds: List<String>) {
                        dialogDismiss(progressDialog)
                        AlertDialog.Builder(this@HomeActivity)
                            .setMessage(
                                GsonBuilder()
                                    .setPrettyPrinting()
                                    .create()
                                    .toJson(mediaIds)
                            )
                            .setCancelable(true)
                            .show()
                    }

                    override fun onFailure(throwable: Throwable) {
                        dialogDismiss(progressDialog)
                        makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                            .show()
                    }
                })
            }

            20 -> {
                progressDialog = progressDialog("Buscando Broadcast Current Slots...")
                JarvisClient.broadcast.currentSlots(
                    "6120663", "172",
                    callback = object : Callback.BroadcastCurrentSlots {
                        override fun onBroadcastCurrentSlotsSuccess(slots: List<BroadcastSlot>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(slots)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            21 -> {
                progressDialog = progressDialog("Buscando Estrutura da Home...")
                JarvisClient.home.structure(
                    "home-free", HighlightImageMobileScales.X1.rawValue(),
                    object : Callback.Home {
                        override fun onStructureSuccess(triple: Triple<String, HomeQuery.PremiumHighlight?, List<HomeQuery.OfferItem>>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(triple)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            22 -> {
                progressDialog = progressDialog("Buscando calendário...")
                JarvisClient.calendar.all("ddgrqPRPQr",
                    object : Callback.Dates {
                        override fun onDatesSuccess(pair: Pair<List<Calendar>, List<String>>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(pair)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            23 -> {
                progressDialog = progressDialog("Buscando trechos relacionados por data...")
                val findDate = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 25)
                    set(Calendar.MONTH, Calendar.MAY)
                    set(Calendar.YEAR, 2020)
                }
                JarvisClient.episode.detailsWithRelatedExcerptsByDate(
                    "ddgrqPRPQr",
                    findDate.time,
                    findDate.time,
                    object : Callback.Dates {
                        override fun onEpisodeAndRelatedExcerptsByDateSuccess(pair: Pair<Video?, List<Thumb>>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(pair)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            24 -> {
                progressDialog = progressDialog("Buscando detalhes do episódio...")
                JarvisClient.episode.detail("7634176", MobilePosterScales.X1.rawValue(),
                    object : Callback.Episodes {
                        override fun onDetailSuccess(episode: Episode) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(episode)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            25 -> {
                progressDialog = progressDialog("Buscando detalhes do episódio com season...")
                JarvisClient.episode.detailsWithSeason("1b7jV1jcLh", 1, 1,
                    object : Callback.Episodes {
                        override fun onDetailsWithSeasonSuccess(triple: Triple<String, List<Season>, EpisodeDetails>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(triple)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            26 -> {
                progressDialog = progressDialog("Buscando cenas...")
                JarvisClient.scenes.withThumbs("8382555", 1, "Headline", 1, 1, 1, 1,
                    object : Callback.Excerpts {
                        override fun onScenesSuccess(scene: Scene) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(scene)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }
            27 -> {
                progressDialog = progressDialog("Buscando cenas c/ season...")
                JarvisClient.scenes.withSeason("zGmSyVg7h2", 1, 10, 1, 1,
                    object : Callback.Scenes {
                        override fun onScenesWithSeasonSuccess(pair: Pair<List<Season>, Triple<Boolean, Int, List<Scene>>>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(pair)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }
            28 -> {
                progressDialog = progressDialog("Buscando cenas s/ detalhes season...")
                JarvisClient.scenes.withoutSeasonDetails("zGmSyVg7h2", 1, 10,
                    object : Callback.Scenes {
                        override fun onScenesWithoutSeasonDetailsSuccess(pair: Pair<Pair<Season?, List<Season>>, Triple<Boolean, Int, List<ScenesPreview>>>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(pair)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            29 -> {
                progressDialog = progressDialog("Buscando cenas s/ detalhes...")
                JarvisClient.scenes.withoutDetails(
                    "zGmSyVg7h2", "8gDWZfR6MR", 1, 10, 1, 1,
                    object : Callback.Scenes {
                        override fun onScenesWithoutDetailsSuccess(triple: Triple<Boolean, Int, List<ScenesPreview>>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(triple)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            30 -> {
                progressDialog = progressDialog("Buscando configurações remotas")
                JarvisClient.remoteConfig.all(
                    "ANDROID_MOBILE",
                    object : Callback.RemoteConfig {
                        override fun onRemoteConfigSuccess(configurationList: Map<String, Configuration?>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(configurationList)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }, "test", "sales"
                )
            }

            31 -> {
                progressDialog = progressDialog("Buscando lista de estados")
                JarvisClient.affiliates.states(
                    object : Callback.State {
                        override fun onStatesSuccess(statesList: List<States>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(statesList)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            32 -> {
                progressDialog = progressDialog("Buscando lista de regiões")
                JarvisClient.affiliates.regions(
                    object : Callback.Region {
                        override fun onRegionsSuccess(regionsList: List<Regions>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(regionsList)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            33 -> {
                progressDialog = progressDialog("Buscando lista de regiões por estado")
                JarvisClient.affiliates.regionsByState("RJ",
                    object : Callback.Region {
                        override fun onRegionsSuccess(regionsList: List<Regions>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(regionsList)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            34 -> {
                progressDialog = progressDialog("Buscando Programas das Afiliadas")
                JarvisClient.affiliates.programs(
                    "rio-de-janeiro-e-regiao",
                    MobilePosterScales.X4.rawValue(),
                    CoverLandscapeScales.X653.rawValue(),
                    1, 12,
                    object : Callback.AffiliatePrograms {
                        override fun onAffiliateProgramsSuccess(triple: Triple<Boolean, Int?, List<Title>>) {
                            dialogDismiss(progressDialog)
                            AlertDialog.Builder(this@HomeActivity)
                                .setMessage(
                                    GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(triple)
                                )
                                .setCancelable(true)
                                .show()
                        }

                        override fun onFailure(throwable: Throwable) {
                            dialogDismiss(progressDialog)
                            makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }

            35 -> {
                progressDialog = progressDialog("Buscando Recomendação default de vendas")
                JarvisClient
                    .sales.defaultRecommendation(
                        serviceId = "mais-canais",
                        trimmedLogoScale = BroadcastChannelTrimmedLogoScales.X42.rawValue(),
                        salesCallback = object : Callback.Sales {

                            override fun onRecommendationSuccess(
                                salesRecommendation: SalesRecommendation
                            ) {
                                dialogDismiss(progressDialog)
                                AlertDialog.Builder(this@HomeActivity)
                                    .setMessage(
                                        GsonBuilder()
                                            .setPrettyPrinting()
                                            .create()
                                            .toJson(salesRecommendation)
                                    )
                                    .setCancelable(true)
                                    .show()
                            }

                            override fun onFailure(throwable: Throwable) {
                                dialogDismiss(progressDialog)
                                makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        })
            }

            36 -> {
                progressDialog = progressDialog("Buscando Localização")
                JarvisClient
                    .locale.find(CountryCode.BR, "tenant-default",
                        object : Callback.Locales {
                            override fun onLocaleSuccess(locale: Locale) {
                                dialogDismiss(progressDialog)
                                AlertDialog.Builder(this@HomeActivity)
                                    .setMessage(
                                        GsonBuilder()
                                            .setPrettyPrinting()
                                            .create()
                                            .toJson(locale)
                                    )
                                    .setCancelable(true)
                                    .show()
                            }

                            override fun onFailure(throwable: Throwable) {
                                dialogDismiss(progressDialog)
                                makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        })
            }

            37 -> {
                progressDialog = progressDialog("Buscando Title Format")
                JarvisClient
                    .title.epgActive("ddgrqPRPQr",
                        object : Callback.Titles {
                            override fun onEpgActiveSuccess(isEpgActive: Boolean) {
                                dialogDismiss(progressDialog)
                                AlertDialog.Builder(this@HomeActivity)
                                    .setMessage(
                                        GsonBuilder()
                                            .setPrettyPrinting()
                                            .create()
                                            .toJson(isEpgActive)
                                    )
                                    .setCancelable(true)
                                    .show()
                            }

                            override fun onFailure(throwable: Throwable) {
                                dialogDismiss(progressDialog)
                                makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        })
            }

            38 -> {
                progressDialog = progressDialog("Buscando Origin ProgramID")
                JarvisClient
                    .title.format("ddgrqPRPQr",
                        object : Callback.Titles {
                            override fun onFormatSuccess(format: Format) {
                                dialogDismiss(progressDialog)
                                AlertDialog.Builder(this@HomeActivity)
                                    .setMessage(
                                        GsonBuilder()
                                            .setPrettyPrinting()
                                            .create()
                                            .toJson(format)
                                    )
                                    .setCancelable(true)
                                    .show()
                            }

                            override fun onFailure(throwable: Throwable) {
                                dialogDismiss(progressDialog)
                                makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        })
            }

            39 -> {
                progressDialog = progressDialog("Buscando capítulos")
                JarvisClient
                    .chapter.byDateRange(
                        titleId = "ddgrqPRPQr",
                        startDate = Date(1583031600000),
                        endDate = Date(1598899033295),
                        page = 1,
                        perPage = 10,
                        chaptersCallback = object : Callback.Chapters {
                            override fun onChapterByDateRangeSuccess(triple: Triple<List<Chapter>, Boolean, Int>) {
                                dialogDismiss(progressDialog)
                                AlertDialog.Builder(this@HomeActivity)
                                    .setMessage(
                                        GsonBuilder()
                                            .setPrettyPrinting()
                                            .create()
                                            .toJson(triple)
                                    )
                                    .setCancelable(true)
                                    .show()
                            }

                            override fun onFailure(throwable: Throwable) {
                                dialogDismiss(progressDialog)
                                makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        })
            }

            40 -> {
                progressDialog = progressDialog("Buscando capítulos")
                JarvisClient
                    .chapter.all(
                        titleId = "ddgrqPRPQr",
                        page = 1,
                        perPage = 10,
                        chaptersCallback = object : Callback.Chapters {
                            override fun onChaptersSuccess(triple: Triple<List<Chapter>, Boolean, Int>) {
                                dialogDismiss(progressDialog)
                                AlertDialog.Builder(this@HomeActivity)
                                    .setMessage(
                                        GsonBuilder()
                                            .setPrettyPrinting()
                                            .create()
                                            .toJson(triple)
                                    )
                                    .setCancelable(true)
                                    .show()
                            }

                            override fun onFailure(throwable: Throwable) {
                                dialogDismiss(progressDialog)
                                makeText(this@HomeActivity, throwable.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        })
            }
        }
    }

    private fun Activity.progressDialog(message: String) =
        if (!isFinishing) ProgressDialog(this).let { progressDialog ->
            progressDialog.setMessage(message)
            progressDialog.setOnCancelListener { it.dismiss() }
            progressDialog.setCancelable(true)
            progressDialog.show()
            return@let progressDialog
        } else null


    private fun Activity.dialogDismiss(dialog: ProgressDialog?) {
        if (!isFinishing && dialog?.isShowing == true) {
            dialog.dismiss()
        }
    }
}
