package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Device
import com.globo.jarvis.fragment.*
import com.globo.jarvis.fragment.SalesFaq
import com.globo.jarvis.fragment.SalesPaymentInfo
import com.globo.jarvis.fragment.SalesProduct
import com.globo.jarvis.model.*
import com.globo.jarvis.sales.*
import com.globo.jarvis.sales.SalesQuery.OfferItem
import com.globo.jarvis.type.*
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Test

class SalesRepositoryTest : BaseUnitTest() {
    private val ladingPageQueryBuilder = SalesQuery.builder().id("fake id")
    private val salesHighlightsOfferQuery = SalesHighlightsOfferQuery.builder().id("fake id")
    private val salesGenericOfferQuery = SalesGenericOfferQuery.builder().id("fake id")

    companion object {
        const val SCALE_DEFAULT = "X1"
        const val COVER_SCALE_DEFAULT = "X384"
        const val SALES_ID_DEFAULT = "globoplay"
        const val TITLE_ID_DEFAULT = "CpQvf5PfCL"
    }

    //Querys

    private fun salesQueryOfferItem(
        pageOffer: PageSalesOfferFragment? = pageOfferFragment,
        highlightOffer: PageSalesHighlightFragment? = pageHighlightFragment
    ) = OfferItem(
        "OfferItem",
        OfferItem.Fragments(
            pageOffer,
            highlightOffer
        )
    )

    private val genericTitleOffersQueryResources = SalesGenericTitleOffers.Resource(
        "resources",
        SalesGenericTitleOffers.Resource.Fragments(
            SalesTitleOffer(
                "TitleOffer",
                "Title headline",
                SalesTitleOffer.Poster(
                    "Poster",
                    "fake poster mobile url",
                    "fake poster tablet url"
                )
            )
        )
    )

    private val pageOfferFragment = PageSalesOfferFragment(
        "Offer",
        "E ainda pode maratonar um montão de séries",
        PageComponentType.POSTER,
        "ee9b9d84-7a66-4b9b-b5ba-3519ba70c95a"
    )

    private val pageHighlightFragment = PageSalesHighlightFragment(
        "Highlight",
        "call text",
        "fake headline",
        false,
        PageComponentType.PREMIUMHIGHLIGHT,
        "highlight Id"
    )

    private val salesQueryTitle = SalesQuery.Title(
        "Title",
        SalesQuery.Cover(
            "Cover",
            "X0_75",
            "X288",
            "X276"
        )
    )

    private val salesQueryHighlights = SalesQuery.Highlight(
        "Highlight",
        HighlightContentType.SIMULCAST,
        "headline",
        SalesQuery.HighlightImage(
            "HighlightImage",
            "highlight for mobile",
            "highlight for tablet"
        ),
        SalesQuery.Logo(
            "Logo",
            "logo for mobile",
            "logo for tablet"
        )
    )

    private val salesQueryPremiumHighlight = SalesQuery.PremiumHighlight(
        "PremiumHighlight",
        "call text",
        PageComponentType.PREMIUMHIGHLIGHT,
        "button text",
        salesQueryHighlights
    )

    private val salesQueryHighlightsOffer = SalesHighlightsOfferQuery.Highlight(
        "Highlight",
        HighlightContentType.SIMULCAST,
        SalesHighlightsOfferQuery.Logo(
            "Logo",
            "logo for mobile",
            "logo for tablet"
        ),
        SalesHighlightsOfferQuery.OfferImage(
            "OfferImage",
            "offer image for mobile",
            "offer image for tablet"
        )
    )

    private val salesFaq = SalesFaq(
        "SalesFaq",
        SalesFaq.Links(
            "Link",
            "fake link"
        ),
        "link text"
    )

    private val salesLegalText = SalesProductLegalText(
        "LegalText",
        "content",
        "url contract",
        "url contract text"
    )

    private val salesPaymentFrequencyInfo = SalesPaymentFrequency(
        "PaymentFrequency",
        "MONTHLY",
        "Mensal",
        "mês"
    )

    private val salesUserConditions = SalesUserCondition(
        "SalesUserConditions",
        "BUY",
        7
    )

    private val salesInstallmentInfo = SalesInstallmentInfo(
        "",
        "XX,XX",
        12
    )

    private val salesPaymentInfo = SalesPaymentInfo(
        "PaymentInfo",
        "callText",
        "R$",
        "24,90",
        SalesPaymentInfo.Frequency(
            "",
            SalesPaymentInfo.Frequency.Fragments(
                salesPaymentFrequencyInfo
            )
        ),
        SalesPaymentInfo.InstallmentInfo(
            "InstallmentInfo",
            SalesPaymentInfo.InstallmentInfo.Fragments(
                salesInstallmentInfo
            )
        )
    )

    private val salesRecommendationUserErrorQuery = SalesUserError(
        "SalesUserError",
        "Error",
        "Você é dependente",
        SalesUserError.Faq(
            "",
            SalesUserError.Faq.Fragments(
                salesFaq
            )
        )
    )

    private val salesProductErrorQuery = SalesProductError(
        "ProductError",
        "Error",
        "Você é dependente",
        SalesProductError.Metadata(
            "Metadata",
            SalesProductError.Metadata.Fragments(
                ChangeChannelError(
                    "ChangeChannelError",
                    SalesPlatform.APPLE
                )
            )
        ),
        SalesProductError.Faq(
            "",
            SalesProductError.Faq.Fragments(
                salesFaq
            )
        )
    )

    private val salesBroadcastChannels = SalesChannels(
        "Channels",
        "fake name",
        "trimmed logo"
    )

    private val salesRecommendedProduct = SalesProduct(
        "Product",
        "productId",
        "Product Name",
        "Offer text",
        listOf("benefit one", "benefit two"),
        "Android sku",
        SalesProduct.PaymentInfo(
            "",
            SalesProduct.PaymentInfo.Fragments(
                salesPaymentInfo
            )
        ),
        SalesProduct.Faq(
            "",
            SalesProduct.Faq.Fragments(
                salesFaq
            )
        ),
        listOf(
            SalesProduct.Channel(
                "channels",
                SalesProduct.Channel.Fragments(
                    salesBroadcastChannels
                )
            )
        ),
        SalesProduct.LegalText(
            "",
            SalesProduct.LegalText.Fragments(
                salesLegalText
            )
        )
    )

    //Load Lading Page
    @Test
    fun givenValidSalesIdAndTitleIdWhenLoadLadingPageThenAssertStructureIsCalled() {
        val repository = getRepository()

        repository.landingPage(
            salesId = SALES_ID_DEFAULT,
            titleId = TITLE_ID_DEFAULT,
            scale = SCALE_DEFAULT,
            coverScale = COVER_SCALE_DEFAULT,
            page = 1,
            perPage = 24
        )
        verify {
            repository.structure(
                SALES_ID_DEFAULT,
                TITLE_ID_DEFAULT,
                SCALE_DEFAULT,
                COVER_SCALE_DEFAULT
            )
        }
    }

    @Test
    fun givenValidSalesIdAndNoTitleIdWhenLoadLadingPageThenAssertStructureIsCalled() {
        val repository = getRepository()

        repository.landingPage(
            salesId = SALES_ID_DEFAULT,
            scale = SCALE_DEFAULT,
            coverScale = COVER_SCALE_DEFAULT,
            page = 1,
            perPage = 24
        )
        verify { repository.structure(SALES_ID_DEFAULT, null, SCALE_DEFAULT, COVER_SCALE_DEFAULT) }
    }

    @Test
    fun givenValidValuesWhenLoadLadingPageThenReturnPremiumHighlightsValidWithOffersNull() {
        enqueueResponse("sales/ladingpage/lading_page_offers_null.json")
        enqueueResponse("sales/ladingpage/locale_br.json")

        val testObserver =
            getRepository().landingPage(
                salesId = SALES_ID_DEFAULT,
                titleId = TITLE_ID_DEFAULT,
                scale = SCALE_DEFAULT,
                coverScale = COVER_SCALE_DEFAULT,
                page = 1,
                perPage = 24
            ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue { pair ->
            val offersVO = pair.first
            val offersVOList = pair.second

            offersVO.componentType.value == ComponentType.PREMIUM_HIGHLIGHT.value
                    && offersVO.contentType.value == ContentType.PROMOTIONAL.value
                    && offersVOList.isEmpty()
        }
    }

    // Structure Load Lading Page
    @Test
    fun givenSuccessResponseWhenValidWhenLoadStructureThenAssertOffersAndServiceIdAreNotEmpty() {
        enqueueResponse("sales/structurelandingpage/structure_lading_page.json")

        val testObserver =
            getRepository().structure(
                salesId = SALES_ID_DEFAULT,
                titleId = TITLE_ID_DEFAULT,
                scale = SCALE_DEFAULT,
                coverScale = COVER_SCALE_DEFAULT
            ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue { (offers, serviceId, _) ->
            offers.first != null
                    && !offers.second.isNullOrEmpty()
                    && !serviceId.isNullOrEmpty()
        }
    }

    @Test
    fun givenOffersNullWhenLoadStructureThenAssertOfferItemsIsEmpty() {
        enqueueResponse("sales/structurelandingpage/structure_lading_page_offers_null.json")

        val testObserver =
            getRepository().structure(
                salesId = SALES_ID_DEFAULT,
                titleId = TITLE_ID_DEFAULT,
                scale = SCALE_DEFAULT,
                coverScale = COVER_SCALE_DEFAULT
            ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue { (offers, _, _) ->
            offers.second?.isEmpty() == true
        }
    }

    @Test
    fun givenOffersWithItemNullWhenLoadStructureThenAssertNullItemsIsRemoved() {
        enqueueResponse("sales/structurelandingpage/structure_lading_page_offers_with_item_null.json")

        val testObserver = getRepository().structure(
            salesId = SALES_ID_DEFAULT,
            titleId = TITLE_ID_DEFAULT,
            scale = SCALE_DEFAULT,
            coverScale = COVER_SCALE_DEFAULT
        ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue { (offers, _, _) ->
            offers.second?.size == 1
        }
    }

    @Test
    fun givenOffersGenericComponentInvalidWhenLoadStructureLadingPageThenAssertOfferItemIsEmpty() {
        enqueueResponse("sales/structurelandingpage/structure_lading_page_offers_generic_component_invalid.json")

        val testObserver =
            getRepository().structure(
                salesId = SALES_ID_DEFAULT,
                titleId = TITLE_ID_DEFAULT,
                scale = SCALE_DEFAULT,
                coverScale = COVER_SCALE_DEFAULT
            ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue { (offers, _, _) ->
            offers.second?.isEmpty() == true
        }
    }

    //Detail offer
    @Test
    fun givenPageOfferWhenDetailOfferThenReturnListOfPageOffer() {
        enqueueResponse("sales/detailsgeneric/details_generic.json")

        val offerItemsQuery = salesQueryOfferItem(highlightOffer = null)
        val detailOffers = getRepository().detailsOffer(
            offerItemList = listOf(offerItemsQuery),
            page = 1,
            perPage = 24,
            scale = SCALE_DEFAULT
        ).test()
        detailOffers.awaitTerminalEvent()
        detailOffers.assertComplete()

        detailOffers.assertValue {
            val pageOffer = offerItemsQuery.fragments().pageSalesOfferFragment()
            it.size == 1
                    && it[0].title == pageOffer?.title()
                    && it[0].componentType == ComponentType.RAILS_POSTER
                    && it[0].contentType == ContentType.TITLE
        }
    }

    @Test
    fun givenHighlightOfferWhenDetailOfferThenReturnListOfPageHighlightOffer() {
        enqueueResponse("sales/detailshighlights/details_highlight.json")

        val offerItemsQuery = salesQueryOfferItem()
        val detailOffers = getRepository().detailsOffer(
            offerItemList = listOf(offerItemsQuery),
            page = 1,
            perPage = 24,
            scale = SCALE_DEFAULT
        ).test()
        detailOffers.awaitTerminalEvent()
        detailOffers.assertComplete()

        detailOffers.assertValue {
            val pageHighlight = offerItemsQuery.fragments().pageSalesHighlightFragment()
            it.size == 1
                    && it[0].headlineText == pageHighlight?.headline()
                    && it[0].callText == pageHighlight?.callText()
                    && it[0].componentType == ComponentType.PREMIUM_HIGHLIGHT
                    && it[0].contentType == ContentType.PROMOTIONAL
                    && it[0].highlights != null
        }
    }

    // Load Details Offer Highlights
    @Test
    fun testLoadDetailsOfferHighlightsWhenTabletAndLeftAlignedFalse() {
        enqueueResponse("sales/detailshighlights/details_highlight.json")

        val testObserver = getRepository(device = Device.TABLET).detailsOfferHighlights(
            highlightId = "2337c65d-8795-4d56-8fd7-ef9dab954fb3",
            headlineText = "Sem compromisso",
            callText = "Não quer mais? Você cancela quando quiser: online e com um clique",
            leftAligned = false,
            pageComponentType = PageComponentType.OFFERHIGHLIGHT,
            scale = SCALE_DEFAULT
        ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.headlineText == "Sem compromisso"
                    && it.callText == "Não quer mais? Você cancela quando quiser: online e com um clique"
                    && it.componentType == ComponentType.HIGHLIGHT_RIGHT
                    && it.contentType == ContentType.PROMOTIONAL
                    && it.highlights != null
        }
    }

    @Test
    fun testLoadDetailsOfferHighlightsWhenTabletAndLeftAlignedTrue() {
        enqueueResponse("sales/detailshighlights/details_highlight.json")

        val testObserver = getRepository(device = Device.TABLET).detailsOfferHighlights(
            highlightId = "2337c65d-8795-4d56-8fd7-ef9dab954fb3",
            headlineText = "Sem compromisso",
            callText = "Não quer mais? Você cancela quando quiser: online e com um clique",
            leftAligned = true,
            pageComponentType = PageComponentType.OFFERHIGHLIGHT,
            scale = SCALE_DEFAULT
        ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.headlineText == "Sem compromisso"
                    && it.callText == "Não quer mais? Você cancela quando quiser: online e com um clique"
                    && it.componentType == ComponentType.HIGHLIGHT_LEFT
                    && it.contentType == ContentType.PROMOTIONAL
                    && it.highlights != null
        }
    }

    @Test
    fun testLoadDetailsOfferHighlightsWhenMobile() {
        enqueueResponse("sales/detailshighlights/details_highlight.json")

        val testObserver = getRepository().detailsOfferHighlights(
            highlightId = "2337c65d-8795-4d56-8fd7-ef9dab954fb3",
            headlineText = "Sem compromisso",
            callText = "Não quer mais? Você cancela quando quiser: online e com um clique",
            leftAligned = false,
            pageComponentType = PageComponentType.OFFERHIGHLIGHT,
            scale = SCALE_DEFAULT
        ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.headlineText == "Sem compromisso"
                    && it.callText == "Não quer mais? Você cancela quando quiser: online e com um clique"
                    && it.componentType == ComponentType.HIGHLIGHT
                    && it.contentType == ContentType.PROMOTIONAL
                    && it.highlights != null
        }
    }

    @Test
    fun testLoadDetailsOfferHighlightsWithContentTypeInvalid() {
        enqueueResponse("sales/detailshighlights/details_highlight_content_type_invalid.json")

        val testObserver = getRepository().detailsOfferHighlights(
            highlightId = "2337c65d-8795-4d56-8fd7-ef9dab954fb3",
            headlineText = "Sem compromisso",
            callText = "Não quer mais? Você cancela quando quiser: online e com um clique",
            leftAligned = false,
            pageComponentType = PageComponentType.OFFERHIGHLIGHT,
            scale = SCALE_DEFAULT
        ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()
        testObserver.values().isEmpty()
    }

    // Load Details Offer Generic
    @Test
    fun testLoadDetailsGenericOffer() {
        enqueueResponse("sales/detailsgeneric/details_generic.json")

        val testObserver = getRepository().detailsGenericOffer(
            offerId = "ee9b9d84-7a66-4b9b-b5ba-3519ba70c95a",
            title = "E ainda pode maratonar um montão de séries",
            pageComponentType = PageComponentType.POSTER,
            scale = SCALE_DEFAULT,
            page = 1,
            perPage = 24
        ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.title == "E ainda pode maratonar um montão de séries"
                    && it.componentType == ComponentType.RAILS_POSTER
                    && it.contentType == ContentType.TITLE
                    && it.titleList != null
        }
    }

    @Test
    fun testLoadDetailsGenericOfferWithContentTypeInvalid() {
        enqueueResponse("sales/detailsgeneric/details_generic_content_type_invalid.json")

        val testObserver = getRepository().detailsGenericOffer(
            offerId = "ee9b9d84-7a66-4b9b-b5ba-3519ba70c95a",
            title = "E ainda pode maratonar um montão de séries",
            pageComponentType = PageComponentType.POSTER,
            scale = SCALE_DEFAULT,
            page = 1,
            perPage = 24
        ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()
        testObserver.values().isEmpty()
    }

    //Transform Highlight
    @Test
    fun givenValidHighlightAndDeviceIsMobileWhenTransformHighlightToHighlightVOThenReturnHighlightVO() {

        val highlight = getRepository().transformHighlightToHighlightVO(salesQueryHighlightsOffer)

        assertEquals(highlight.offerImage, salesQueryHighlightsOffer.offerImage()?.mobile())
        assertEquals(highlight.logo, salesQueryHighlightsOffer.logo()?.mobile())
    }

    @Test
    fun givenValidHighlightAndDeviceIsTabletWhenTransformHighlightToHighlightVOThenReturnHighlightVO() {

        val highlight = getRepository(device = Device.TABLET)
            .transformHighlightToHighlightVO(salesQueryHighlightsOffer)

        assertEquals(highlight.offerImage, salesQueryHighlightsOffer.offerImage()?.tablet())
        assertEquals(highlight.logo, salesQueryHighlightsOffer.logo()?.tablet())
    }

    @Test
    fun givenNoValuesWhenTransformHighlightToHighlightVOThenReturnNoContentForHighlight() {

        val highlight = getRepository().transformHighlightToHighlightVO(null)

        assertNull(highlight.offerImage)
        assertNull(highlight.logo)
    }

    //Transform Premium Highlight
    @Test
    fun givenCountryCodeUsWhenTransformPremiumHighlightToHighlightsThenReturnHighlightVOCarrierButtonIsEnable() {

        val highlight = getRepository().transformPremiumHighlightToHighlights(
            premiumHighlight = salesQueryPremiumHighlight,
            title = salesQueryTitle
        )

        assertEquals(highlight.callText, salesQueryPremiumHighlight.callText())
        assertEquals(highlight.headlineText, salesQueryPremiumHighlight.highlight().headlineText())
        assertEquals(highlight.buttonText, salesQueryPremiumHighlight.buttonText())
        assertEquals(highlight.logo, salesQueryPremiumHighlight.highlight().logo()?.mobile())
        assertEquals(highlight.highlightImage, salesQueryTitle.cover()?.portrait())
    }

    @Test
    fun givenValidValuesForMobileWhenTransformPremiumHighlightToHighlightsThenReturnHighlightVO() {

        val highlight = getRepository().transformPremiumHighlightToHighlights(
            premiumHighlight = salesQueryPremiumHighlight,
            title = salesQueryTitle
        )

        assertEquals(highlight.callText, salesQueryPremiumHighlight.callText())
        assertEquals(highlight.headlineText, salesQueryPremiumHighlight.highlight().headlineText())
        assertEquals(highlight.buttonText, salesQueryPremiumHighlight.buttonText())
        assertEquals(highlight.logo, salesQueryPremiumHighlight.highlight().logo()?.mobile())
        assertEquals(highlight.highlightImage, salesQueryTitle.cover()?.portrait())

        assertFalse(highlight.carrierEnable)
    }

    @Test
    fun givenValidValuesForTabletWhenTransformPremiumHighlightToHighlightsThenReturnHighlightVO() {

        val highlight = getRepository(device = Device.TABLET).transformPremiumHighlightToHighlights(
            premiumHighlight = salesQueryPremiumHighlight,
            title = salesQueryTitle
        )

        assertEquals(highlight.callText, salesQueryPremiumHighlight.callText())
        assertEquals(highlight.headlineText, salesQueryPremiumHighlight.highlight().headlineText())
        assertEquals(highlight.buttonText, salesQueryPremiumHighlight.buttonText())
        assertEquals(highlight.logo, salesQueryPremiumHighlight.highlight().logo()?.tablet())
        assertEquals(highlight.highlightImage, salesQueryTitle.cover()?.landscape())

        assertFalse(highlight.carrierEnable)
    }

    @Test
    fun givenNoTitleQueryForTabletWhenTransformPremiumHighlightToHighlightsThenReturnHighlightVO() {

        val highlight = getRepository(device = Device.TABLET).transformPremiumHighlightToHighlights(
            premiumHighlight = salesQueryPremiumHighlight
        )

        assertEquals(highlight.callText, salesQueryPremiumHighlight.callText())
        assertEquals(highlight.headlineText, salesQueryPremiumHighlight.highlight().headlineText())
        assertEquals(highlight.buttonText, salesQueryPremiumHighlight.buttonText())
        assertEquals(highlight.logo, salesQueryPremiumHighlight.highlight().logo()?.tablet())
        assertEquals(
            highlight.highlightImage,
            salesQueryPremiumHighlight.highlight().highlightImage()?.tablet()
        )

        assertFalse(highlight.carrierEnable)
    }

    @Test
    fun givenNullValuesWhenTransformPremiumHighlightToHighlightsThenReturnHighlightVOWithNullValues() {

        val highlight = getRepository().transformPremiumHighlightToHighlights(
            premiumHighlight = null,
            title = null
        )

        assertNull(highlight.callText)
        assertNull(highlight.headlineText)
        assertNull(highlight.buttonText)
        assertNull(highlight.logo)
        assertNull(highlight.highlightImage)

        assertFalse(highlight.carrierEnable)
    }

    //Convert LadingPageQueryOfferItemList
    @Test
    fun givenOfferItemListWhenConvertLadingPageQueryOfferItemListObservableLadingPageQueryOfferItemListThenReturnSalesListOfOfferItem() {

        val pageOffer = salesQueryOfferItem().fragments().pageSalesOfferFragment()
        val highlightOffer = salesQueryOfferItem().fragments().pageSalesHighlightFragment()

        val offerItemList = getRepository()
            .convertLadingPageQueryOfferItemListObservableLadingPageQueryOfferItemList(
                listOf(salesQueryOfferItem())
            )
        assertTrue(offerItemList.isNotEmpty())
        offerItemList[0].test().assertValue { offerItem ->
            val pageHighlights = offerItem.fragments().pageSalesHighlightFragment()
            val offerHighlight = offerItem.fragments().pageSalesOfferFragment()

            offerHighlight != null &&
                    offerHighlight.title() == pageOffer?.title()
                    && offerHighlight.offerId() == pageOffer?.offerId()
                    && offerHighlight.componentType() == pageOffer.componentType()
                    && pageHighlights != null
                    && pageHighlights.callText() == highlightOffer?.callText()
                    && pageHighlights.headline() == highlightOffer?.headline()
                    && pageHighlights.leftAligned() == highlightOffer?.leftAligned()
                    && pageHighlights.highlightId() == highlightOffer?.highlightId()
        }
    }

    @Test
    fun givenNoValueWhenConvertLadingPageQueryOfferItemListObservableLadingPageQueryOfferItemListThenReturnEmptyList() {

        assertTrue(
            getRepository().convertLadingPageQueryOfferItemListObservableLadingPageQueryOfferItemList(
                null
            ).isEmpty()
        )
    }

    //Transform ResourceTitleVO
    @Test
    fun givenResourceListAndDeviceIsMobileWhenTransformResourceToTitleVOThenReturnTitleList() {
        val titleList =
            getRepository().transformResourceToTitleVO(listOf(genericTitleOffersQueryResources))

        assertEquals(titleList.size, 1)
        assertEquals(titleList[0].headline, "Title headline")
        assertEquals(titleList[0].poster, "fake poster mobile url")
    }

    @Test
    fun givenResourceListAndDeviceIsTabletWhenTransformResourceToTitleVOThenReturnTitleList() {
        val titleList = getRepository(device = Device.TABLET).transformResourceToTitleVO(
            listOf(genericTitleOffersQueryResources)
        )

        assertEquals(titleList.size, 1)
        assertEquals(titleList[0].headline, "Title headline")
        assertEquals(titleList[0].poster, "fake poster tablet url")
    }

    @Test
    fun givenNoResourceListWhenTransformResourceToTitleVOThenReturnEmptyList() {
        assertEquals(
            getRepository().transformResourceToTitleVO(null),
            emptyList<Title>()
        )
    }

    //Build LadingPageQuery
    @Test
    fun givenSalesIdAndNullTitleIdWhenBuilderLadingPageQueryThenReturnSalesQueryWithSalesIdAndEmptyTitleId() {

        val salesQuery = getRepository().builderLadingPageQuery(
            salesId = "fake sales Id",
            titleId = null,
            scale = "X0_75",
            coverScale = "X288"
        )

        assertEquals(salesQuery.variables().id(), "fake sales Id")
        assertEquals(salesQuery.variables().titleId().value, "")
        assertEquals(
            salesQuery.variables().coverPortraitScales().value?.rawValue(),
            CoverPortraitScales.X288.rawValue()
        )
        assertEquals(
            salesQuery.variables().highlightImageMobileScales().value?.rawValue(),
            HighlightImageMobileScales.X0_75.rawValue()
        )
        assertEquals(
            salesQuery.variables().highlightLogoMobileScales().value?.rawValue(),
            HighlightLogoMobileScales.X0_75.rawValue()
        )
    }

    @Test
    fun givenValidSalesIdAndTitleIdWhenBuilderLadingPageQueryThenReturnSalesQueryWithSalesIdAndTitleId() {

        val salesQuery = getRepository().builderLadingPageQuery(
            salesId = "fake sales Id",
            titleId = "Fake title id",
            scale = "X0_75",
            coverScale = "X288"
        )

        assertEquals(salesQuery.variables().id(), "fake sales Id")
        assertEquals(salesQuery.variables().titleId().value, "Fake title id")
        assertEquals(
            salesQuery.variables().coverPortraitScales().value?.rawValue(),
            CoverPortraitScales.X288.rawValue()
        )
        assertEquals(
            salesQuery.variables().highlightImageMobileScales().value?.rawValue(),
            HighlightImageMobileScales.X0_75.rawValue()
        )
        assertEquals(
            salesQuery.variables().highlightLogoMobileScales().value?.rawValue(),
            HighlightLogoMobileScales.X0_75.rawValue()
        )
    }

    //Build LadingPageGenericOfferQuery
    @Test
    fun givenValidOfferIdWhenBuilderLadingPageGenericOfferQueryThenReturnSalesGenericOfferQueryWithId() {

        val salesGenericOfferQuery = getRepository().builderLadingPageGenericOfferQuery(
            offerId = "fake offer id",
            scale = "X1",
            page = 1,
            perPage = 24
        )

        assertEquals(salesGenericOfferQuery.variables().id(), "fake offer id")
        assertEquals(salesGenericOfferQuery.variables().page().value, 1)
        assertEquals(salesGenericOfferQuery.variables().perPage().value, 24)
        assertEquals(
            salesGenericOfferQuery.variables().mobilePosterScales().value?.rawValue(),
            MobileLogoScales.X1.rawValue()
        )
    }

    @Test
    fun givenNullOfferIdWhenBuilderLadingPageGenericOfferQueryThenReturnSalesGenericOfferQueryWithEmptyId() {

        val salesGenericOfferQuery = getRepository().builderLadingPageGenericOfferQuery(
            offerId = null,
            scale = "X1",
            page = 1,
            perPage = 24
        )

        assertEquals(salesGenericOfferQuery.variables().id(), "")
        assertEquals(salesGenericOfferQuery.variables().page().value, 1)
        assertEquals(salesGenericOfferQuery.variables().perPage().value, 24)
        assertEquals(
            salesGenericOfferQuery.variables().mobilePosterScales().value?.rawValue(),
            MobileLogoScales.X1.rawValue()
        )
    }

    //Build ImageLadingPageGenericOffer
    @Test
    fun givenValidHighlightIdWhenBuilderImageLadingPageGenericOfferThenReturnSalesHighlightsOfferQueryWithId() {
        val highlightId = "fake highlight Id"

        val salesHighlightsOfferQuery = getRepository().builderLadingPageHighlightOfferQuery(
            highlightId = highlightId,
            scale = "X1"
        )

        assertEquals(salesHighlightsOfferQuery.variables().id(), highlightId)
    }

    @Test
    fun givenEmptyHighlightIdWhenBuilderImageLadingPageGenericOfferThenReturnSalesHighlightsOfferQueryWithIdEmpty() {
        val salesHighlightsOfferQuery = getRepository().builderLadingPageHighlightOfferQuery(
            highlightId = "",
            scale = "X1"
        )

        assertEquals(salesHighlightsOfferQuery.variables().id(), "")
    }

    @Test
    fun givenNullHighlightIdWhenBuilderImageLadingPageGenericOfferThenReturnSalesHighlightsOfferQueryWithIdEmpty() {
        val salesHighlightsOfferQuery = getRepository().builderLadingPageHighlightOfferQuery(
            highlightId = null,
            scale = "X1"
        )

        assertEquals(salesHighlightsOfferQuery.variables().id(), "")
    }

    //Build LadingPageHighlightOfferQuery
    @Test
    fun givenScaleForTabletWhenBuilderLadingPageHighlightOfferQueryThenReturnSalesGenericOfferQueryWithPoster() {
        val scaleList = listOf("X0_75", "X1", "X1_5", "X2", "X3", "X4", "UNKNOWN")

        val expectedTabletPosterScalesList = listOf(
            TabletPosterScales.X0_75,
            TabletPosterScales.X1,
            TabletPosterScales.X1_5,
            TabletPosterScales.X2,
            TabletPosterScales.X3,
            TabletPosterScales.X4,
            TabletPosterScales.`$UNKNOWN`
        )

        scaleList.forEachIndexed { index, scale ->
            val builderImage =
                getRepository(device = Device.TABLET).builderImageLadingPageGenericOffer(
                    salesGenericOfferQuery = salesGenericOfferQuery,
                    scale = scale
                )

            assertEquals(
                builderImage.build().variables().tabletPosterScales().value?.rawValue(),
                expectedTabletPosterScalesList[index].rawValue()
            )
        }
    }

    //Build ImageLadingPageGenericOffer
    @Test
    fun givenScaleForMobileWhenBuilderImageLadingPageGenericOfferThenReturnSalesGenericOfferQueryWithPoster() {
        val scaleList = listOf("X0_75", "X1", "X1_5", "X2", "X3", "X4", "UNKNOWN")

        val expectedMobilePosterScalesList = listOf(
            MobilePosterScales.X0_75,
            MobilePosterScales.X1,
            MobilePosterScales.X1_5,
            MobilePosterScales.X2,
            MobilePosterScales.X3,
            MobilePosterScales.X4,
            MobilePosterScales.`$UNKNOWN`
        )

        scaleList.forEachIndexed { index, scale ->
            val builderImage =
                getRepository(device = Device.MOBILE).builderImageLadingPageGenericOffer(
                    salesGenericOfferQuery = salesGenericOfferQuery,
                    scale = scale
                )

            assertEquals(
                builderImage.build().variables().mobilePosterScales().value?.rawValue(),
                expectedMobilePosterScalesList[index].rawValue()
            )
        }
    }

    //Build ImageLadingPageHighlightOffer
    @Test
    fun givenScaleForMobileWhenBuilderImageLadingPageHighlightOfferThenReturnSalesHighlightsOfferQueryWithOfferImageAndLogo() {
        val scaleList = listOf("X0_75", "X1", "X1_5", "X2", "X3", "X4", "UNKNOWN")

        val expectedHighlightOfferImageMobileScalesList = listOf(
            HighlightOfferImageMobileScales.X0_75,
            HighlightOfferImageMobileScales.X1,
            HighlightOfferImageMobileScales.X1_5,
            HighlightOfferImageMobileScales.X2,
            HighlightOfferImageMobileScales.X3,
            HighlightOfferImageMobileScales.X4,
            HighlightOfferImageMobileScales.`$UNKNOWN`
        )

        val expectedHighlightLogoMobileScalesList = listOf(
            HighlightLogoMobileScales.X0_75,
            HighlightLogoMobileScales.X1,
            HighlightLogoMobileScales.X1_5,
            HighlightLogoMobileScales.X2,
            HighlightLogoMobileScales.X3,
            HighlightLogoMobileScales.X4,
            HighlightLogoMobileScales.`$UNKNOWN`
        )

        scaleList.forEachIndexed { index, scale ->
            val builderImage =
                getRepository(device = Device.MOBILE).builderImageLadingPageHighlightOffer(
                    SalesHighlightsOfferQuery = salesHighlightsOfferQuery,
                    scale = scale
                )

            assertEquals(
                builderImage.build().variables().highlightLogoMobileScales().value?.rawValue(),
                expectedHighlightOfferImageMobileScalesList[index].rawValue()
            )
            assertEquals(
                builderImage.build().variables()
                    .highlightOfferImageMobileScales().value?.rawValue(),
                expectedHighlightLogoMobileScalesList[index].rawValue()
            )
        }
    }

    @Test
    fun givenScaleForTabletWhenBuilderImageLadingPageHighlightOfferThenReturnSalesHighlightsOfferQueryWithOfferImageAndLogo() {
        val scaleList = listOf("X0_75", "X1", "X1_5", "X2", "X3", "X4", "UNKNOWN")

        val expectedHighlightOfferImageTabletScalesList = listOf(
            HighlightOfferImageTabletScales.X0_75,
            HighlightOfferImageTabletScales.X1,
            HighlightOfferImageTabletScales.X1_5,
            HighlightOfferImageTabletScales.X2,
            HighlightOfferImageTabletScales.X3,
            HighlightOfferImageTabletScales.X4,
            HighlightOfferImageTabletScales.`$UNKNOWN`
        )

        val expectedHighlightLogoTabletScalesList = listOf(
            HighlightLogoTabletScales.X0_75,
            HighlightLogoTabletScales.X1,
            HighlightLogoTabletScales.X1_5,
            HighlightLogoTabletScales.X2,
            HighlightLogoTabletScales.X3,
            HighlightLogoTabletScales.X4,
            HighlightLogoTabletScales.`$UNKNOWN`
        )

        scaleList.forEachIndexed { index, scale ->
            val builderImage =
                getRepository(device = Device.TABLET).builderImageLadingPageHighlightOffer(
                    SalesHighlightsOfferQuery = salesHighlightsOfferQuery,
                    scale = scale
                )

            assertEquals(
                builderImage.build().variables().highlightLogoTabletScales().value?.rawValue(),
                expectedHighlightOfferImageTabletScalesList[index].rawValue()
            )
            assertEquals(
                builderImage.build().variables()
                    .highlightOfferImageTabletScales().value?.rawValue(),
                expectedHighlightLogoTabletScalesList[index].rawValue()
            )
        }
    }

    //Build ImageLadingPage
    @Test
    fun givenScaleAndCoverScaleForMobileWhenBuilderImageLadingPageThenReturnSalesQueryWithScaleForHighlightImageAndHighlightLogoAndCoverPortrait() {
        val scaleAndScaleCoverList = listOf(
            "X0_75" to "X288",
            "X1" to "X384",
            "X1_5" to "X576",
            "X2" to "X768",
            "X3" to "X1152",
            "X4" to "X1536",
            "UNKNOWN" to "UNKNOWN"
        )

        val expectedHighlightLogoScaleList = listOf(
            HighlightLogoMobileScales.X0_75,
            HighlightLogoMobileScales.X1,
            HighlightLogoMobileScales.X1_5,
            HighlightLogoMobileScales.X2,
            HighlightLogoMobileScales.X3,
            HighlightLogoMobileScales.X4,
            HighlightLogoMobileScales.`$UNKNOWN`
        )
        val expectedHighlightImageScaleList = listOf(
            HighlightImageMobileScales.X0_75,
            HighlightImageMobileScales.X1,
            HighlightImageMobileScales.X1_5,
            HighlightImageMobileScales.X2,
            HighlightImageMobileScales.X3,
            HighlightImageMobileScales.X4,
            HighlightImageMobileScales.`$UNKNOWN`
        )
        val expectedCoverImageScaleList = listOf(
            CoverPortraitScales.X288,
            CoverPortraitScales.X384,
            CoverPortraitScales.X576,
            CoverPortraitScales.X768,
            CoverPortraitScales.X1152,
            CoverPortraitScales.X1536,
            CoverPortraitScales.`$UNKNOWN`
        )

        scaleAndScaleCoverList.forEachIndexed { index, (scale, coverScale) ->
            val builderImage = getRepository(device = Device.MOBILE).builderImageLadingPage(
                SalesQuery = ladingPageQueryBuilder,
                scale = scale,
                coverScale = coverScale
            )

            assertEquals(
                builderImage.build().variables().highlightImageMobileScales().value?.rawValue(),
                expectedHighlightImageScaleList[index].rawValue()
            )
            assertEquals(
                builderImage.build().variables().highlightLogoMobileScales().value?.rawValue(),
                expectedHighlightLogoScaleList[index].rawValue()
            )
            assertEquals(
                builderImage.build().variables().coverPortraitScales().value?.rawValue(),
                expectedCoverImageScaleList[index].rawValue()
            )
        }
    }

    @Test
    fun givenScaleAndCoverScaleForTabletWhenBuilderImageLadingPageThenReturnSalesQueryWithScaleForHighlightImageAndHighlightLogoAndCoverLandscape() {
        val scaleAndScaleCoverList = listOf(
            "X0_75" to "X276",
            "X1" to "X348",
            "X1_5" to "X464",
            "X2" to "X552",
            "X3" to "X653",
            "X4" to "X720",
            "X4" to "X828",
            "X4" to "X928",
            "X4" to "X1080",
            "X4" to "X1392",
            "X4" to "X1635",
            "X4" to "X1856",
            "X4" to "X2160",
            "UNKNOWN" to "UNKNOWN"
        )

        val expectedHighlightLogoScaleList = listOf(
            HighlightLogoTabletScales.X0_75,
            HighlightLogoTabletScales.X1,
            HighlightLogoTabletScales.X1_5,
            HighlightLogoTabletScales.X2,
            HighlightLogoTabletScales.X3,
            HighlightLogoTabletScales.X4,
            HighlightLogoTabletScales.X4,
            HighlightLogoTabletScales.X4,
            HighlightLogoTabletScales.X4,
            HighlightLogoTabletScales.X4,
            HighlightLogoTabletScales.X4,
            HighlightLogoTabletScales.X4,
            HighlightLogoTabletScales.X4,
            HighlightLogoTabletScales.`$UNKNOWN`
        )

        val expectedHighlightImageScaleList = listOf(
            HighlightImageTabletScales.X0_75,
            HighlightImageTabletScales.X1,
            HighlightImageTabletScales.X1_5,
            HighlightImageTabletScales.X2,
            HighlightImageTabletScales.X3,
            HighlightImageTabletScales.X4,
            HighlightImageTabletScales.X4,
            HighlightImageTabletScales.X4,
            HighlightImageTabletScales.X4,
            HighlightImageTabletScales.X4,
            HighlightImageTabletScales.X4,
            HighlightImageTabletScales.X4,
            HighlightImageTabletScales.X4,
            HighlightImageTabletScales.`$UNKNOWN`
        )

        val expectedCoverLandscapeList = listOf(
            CoverLandscapeScales.X276,
            CoverLandscapeScales.X348,
            CoverLandscapeScales.X464,
            CoverLandscapeScales.X552,
            CoverLandscapeScales.X653,
            CoverLandscapeScales.X720,
            CoverLandscapeScales.X828,
            CoverLandscapeScales.X928,
            CoverLandscapeScales.X1080,
            CoverLandscapeScales.X1392,
            CoverLandscapeScales.X1635,
            CoverLandscapeScales.X1856,
            CoverLandscapeScales.X2160,
            CoverLandscapeScales.`$UNKNOWN`
        )

        scaleAndScaleCoverList.forEachIndexed { index, (scale, coverScale) ->
            val builderImage = getRepository(device = Device.TABLET).builderImageLadingPage(
                SalesQuery = ladingPageQueryBuilder,
                scale = scale,
                coverScale = coverScale
            )

            assertEquals(
                builderImage.build().variables().highlightImageTabletScales().value?.rawValue(),
                expectedHighlightImageScaleList[index].rawValue()
            )
            assertEquals(
                builderImage.build().variables().highlightLogoTabletScales().value?.rawValue(),
                expectedHighlightLogoScaleList[index].rawValue()
            )
            assertEquals(
                builderImage.build().variables().coverLandscapeScales().value?.rawValue(),
                expectedCoverLandscapeList[index].rawValue()
            )
        }
    }

    @Test
    fun givenValidValuesWhenBuildSalesRecommendationQueryThenReturnBuildSalesRecommendation() {

        val salesQuery = getRepository().builderSalesRecommendationQuery(
            serviceId = "fake sales Id",
            broadcastChannelTrimmedLogoScale = "X42",
            platform = SalesPlatform.GOOGLE
        )

        assertEquals(salesQuery.variables().serviceId()?.value, "fake sales Id")
        assertEquals(salesQuery.variables().platform(), SalesPlatform.GOOGLE)
        assertEquals(
            salesQuery.variables().trimmedLogoScale().rawValue(),
            BroadcastChannelTrimmedLogoScales.X42.rawValue()
        )
    }

    @Test
    fun givenValidValuesWhenBuildDefaultSalesRecommendationQueryThenReturnBuildSalesRecommendation() {

        val salesQuery = getRepository().builderDefaultSalesRecommendationQuery(
            serviceId = "fake sales Id",
            broadcastChannelTrimmedLogoScale = "X42",
            platform = SalesPlatform.GOOGLE
        )

        assertEquals(salesQuery.variables().serviceId()?.value, "fake sales Id")
        assertEquals(salesQuery.variables().platform(), SalesPlatform.GOOGLE)
        assertEquals(
            salesQuery.variables().trimmedLogoScale().rawValue(),
            BroadcastChannelTrimmedLogoScales.X42.rawValue()
        )
    }

    /*
       Sales Recommendation
     */
    @Test
    fun givenValuesWhenRequestSalesRecommendationThenReturnSalesRecommendation() {
        enqueueResponse("sales/salesrecommendation/sales_recommendation_success.json")

        val testObserver = getRepository().recommendation(
            serviceId = "globoplay",
            broadcastChannelTrimmedLogoScale = "X42"
        ).test()

        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.userError == null
                    && it.recommendedProducts != null
                    && it.recommendedProducts?.size == 1
                    && it.recommendedProducts?.get(0)?.androidSku == "androidSku"
                    && it.recommendedProducts?.get(0)?.productId == "Pro-0038"
                    && it.recommendedProducts?.get(0)?.name == "Globoplay + Canais Ao Vivo"
                    && it.recommendedProducts?.get(0)?.benefits?.containsAll(
                listOf(
                    "Todo catálogo Globoplay + BBB ao vivo",
                    "Até 5 acessos simultâneos",
                    "Adicione até  4 dependentes sem custo",
                    "Assista offline"
                )
            ) ?: false
                    && it.recommendedProducts?.get(0)?.paymentInfo?.callText == "Cobrança única renovada a cada 12 meses."
                    && it.recommendedProducts?.get(0)?.paymentInfo?.currency == "R$"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.price == "514.80"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.id == "ANNUAL"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.periodicityLabel == "Anual"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.timeUnitLabel == "ano"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.installmentValue == "42.90"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.numberOfInstallments == 12

                    && it.recommendedProducts?.get(0)?.productFaq?.link == "https://globoplay.globo.com/faq/"
                    && it.recommendedProducts?.get(0)?.productFaq?.text == "Saiba mais"

                    && it.recommendedProducts?.get(0)?.channels?.size == 3
                    && it.recommendedProducts?.get(0)?.channels?.containsAll(
                listOf(
                    Broadcast(
                        name = "TV Globo",
                        trimmedLogo = "https://s2.glbimg.com/n9swMvNkdkEcW98ahrTa_Oorwu4=/fit-in/84x42/https://s2.glbimg.com/gYVgRoEjdh0NUYIUtm1IJ7aid_E=/fit-in/48x24/https://s2.glbimg.com/kL_UPRW5kuYPAJ4-MVU4GI2a-Qc=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/p/r/AzV1BMTgScfblhcB7iHw/globo-png.png"
                    ),
                    Broadcast(
                        name = "SportTv",
                        trimmedLogo = "https://s2.glbimg.com/IN1wJdoaPXUefNVBIFM_qzCKUbo=/fit-in/84x42/https://s2.glbimg.com/XYHjDTXCzgv81O3kmoah6EHXvgU=/fit-in/48x24/https://s2.glbimg.com/YlpcJ25X1zQJh2JO3F3AUoZ4G60=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/e/X/bqOqIqSNyy3bBb7acyXA/sportv.png"
                    ),
                    Broadcast(
                        name = "SportTv 2",
                        trimmedLogo = "https://s2.glbimg.com/a9DLDdq3s_BKAID_rYmmPE9P6tI=/fit-in/84x42/https://s2.glbimg.com/aSej71Fh1JQl-h0gooZtT5Huojc=/fit-in/48x24/https://s2.glbimg.com/2vWqsnLXKCnXWwEJapUkEaqyKcw=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/q/X/APSAlSTvSFHcJfdodXaQ/sportv-2.png"
                    )
                )
            ) ?: false
                    && it.recommendedProducts?.get(0)?.legalText?.legalContent == "legalContent"
                    && it.recommendedProducts?.get(0)?.legalText?.contractUrl == "contractUrl"
                    && it.recommendedProducts?.get(0)?.legalText?.contractsUrlText == "contractsUrlText"
                    && it.recommendedProducts?.get(0)?.productError == null
                    && it.recommendedProducts?.get(0)?.userConditions?.action == "BUY"
                    && it.recommendedProducts?.get(0)?.userConditions?.trialPeriod == 7
        }
    }

    @Test
    fun givenValuesWhenRequestSalesRecommendationThenReturnSalesRecommendationWithProductError() {
        enqueueResponse("sales/salesrecommendation/sales_recommendation_product_error.json")

        val testObserver = getRepository().recommendation(
            serviceId = "globoplay",
            broadcastChannelTrimmedLogoScale = "X42"
        ).test()

        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.userError == null
                    && it.recommendedProducts != null
                    && it.recommendedProducts?.size == 1
                    && it.recommendedProducts?.get(0)?.androidSku == "androidSku"
                    && it.recommendedProducts?.get(0)?.productId == "Pro-0038"
                    && it.recommendedProducts?.get(0)?.name == "Globoplay + Canais Ao Vivo"
                    && it.recommendedProducts?.get(0)?.benefits?.containsAll(
                listOf(
                    "Todo catálogo Globoplay + BBB ao vivo",
                    "Até 5 acessos simultâneos",
                    "Adicione até  4 dependentes sem custo",
                    "Assista offline"
                )
            ) ?: false
                    && it.recommendedProducts?.get(0)?.paymentInfo?.callText == "Cobrança única renovada a cada 12 meses."
                    && it.recommendedProducts?.get(0)?.paymentInfo?.currency == "R$"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.price == "514.80"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.id == "ANNUAL"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.periodicityLabel == "Anual"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.timeUnitLabel == "ano"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.installmentValue == "42.90"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.numberOfInstallments == 12

                    && it.recommendedProducts?.get(0)?.productFaq?.link == "https://globoplay.globo.com/faq/"
                    && it.recommendedProducts?.get(0)?.productFaq?.text == "Saiba mais"

                    && it.recommendedProducts?.get(0)?.channels?.size == 3
                    && it.recommendedProducts?.get(0)?.channels?.containsAll(
                listOf(
                    Broadcast(
                        name = "TV Globo",
                        trimmedLogo = "https://s2.glbimg.com/n9swMvNkdkEcW98ahrTa_Oorwu4=/fit-in/84x42/https://s2.glbimg.com/gYVgRoEjdh0NUYIUtm1IJ7aid_E=/fit-in/48x24/https://s2.glbimg.com/kL_UPRW5kuYPAJ4-MVU4GI2a-Qc=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/p/r/AzV1BMTgScfblhcB7iHw/globo-png.png"
                    ),
                    Broadcast(
                        name = "SportTv",
                        trimmedLogo = "https://s2.glbimg.com/IN1wJdoaPXUefNVBIFM_qzCKUbo=/fit-in/84x42/https://s2.glbimg.com/XYHjDTXCzgv81O3kmoah6EHXvgU=/fit-in/48x24/https://s2.glbimg.com/YlpcJ25X1zQJh2JO3F3AUoZ4G60=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/e/X/bqOqIqSNyy3bBb7acyXA/sportv.png"
                    ),
                    Broadcast(
                        name = "SportTv 2",
                        trimmedLogo = "https://s2.glbimg.com/a9DLDdq3s_BKAID_rYmmPE9P6tI=/fit-in/84x42/https://s2.glbimg.com/aSej71Fh1JQl-h0gooZtT5Huojc=/fit-in/48x24/https://s2.glbimg.com/2vWqsnLXKCnXWwEJapUkEaqyKcw=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/q/X/APSAlSTvSFHcJfdodXaQ/sportv-2.png"
                    )
                )
            ) ?: false
                    && it.recommendedProducts?.get(0)?.legalText?.legalContent == "legalContent"
                    && it.recommendedProducts?.get(0)?.legalText?.contractUrl == "contractUrl"
                    && it.recommendedProducts?.get(0)?.legalText?.contractsUrlText == "contractsUrlText"
                    && it.recommendedProducts?.get(0)?.userConditions?.trialPeriod == 7
                    && it.recommendedProducts?.get(0)?.userConditions?.action == "BUY"

                    && it.recommendedProducts?.get(0)?.productError != null
                    && it.recommendedProducts?.get(0)?.productError?.message == "Você possui um plano no qual está em período de trial. Após o fim deste período você poderá efetuar a compra."
                    && it.recommendedProducts?.get(0)?.productError?.type == "ON_TRIAL"
                    && it.recommendedProducts?.get(0)?.productError?.faq?.text == "Saiba mais"
                    && it.recommendedProducts?.get(0)?.productError?.faq?.link == "https://globoplay.globo.com/faq/"
                    && it.recommendedProducts?.get(0)?.productError?.sourceChannel == null
        }
    }

    @Test
    fun givenValuesWhenRequestSalesRecommendationThenReturnSalesRecommendationUserError() {
        enqueueResponse("sales/salesrecommendation/sales_recommendation_error.json")

        val testObserver = getRepository().recommendation(
            serviceId = "globoplay",
            broadcastChannelTrimmedLogoScale = "X42"
        ).test()

        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.recommendedProducts == null
                    && it.userError != null
                    && it.userError?.type == "INDEBT"
                    && it.userError?.message == "Identificamos que você possui um débito pendente. Para assinar o Plano Globoplay + Canais é necessário a regulaziração do mesmo."
                    && it.userError?.faq?.link == "https://globoplay.globo.com/faq/"
                    && it.userError?.faq?.text == "Saiba mais"


        }
    }

    @Test
    fun givenRecommendedProductWhenTransformItThenReturnRecommendedProduct() {

        val recommendedProduct = salesRecommendation().recommendedProducts()?.get(0)
        val product = recommendedProduct?.product()?.fragments()?.salesProduct()
        val productError = recommendedProduct?.productError()?.fragments()?.salesProductError()
        val userConditions = recommendedProduct?.userConditions()?.fragments()?.salesUserCondition()
        val productErrorFaq = productError?.faq()?.fragments()?.salesFaq()
        val paymentInfo = product?.paymentInfo()?.fragments()?.salesPaymentInfo()
        val paymentFrequency = paymentInfo?.frequency()?.fragments()?.salesPaymentFrequency()
        val legalText = product?.legalText()?.fragments()?.salesProductLegalText()
        val installmentInfo = paymentInfo?.installmentInfo()?.fragments()?.salesInstallmentInfo()
        val productFaq = product?.faq()?.fragments()?.salesFaq()

        val resultRecommendedProduct =
            getRepository().transformSalesRecommendedProductsQueryToSalesRecommendedProducts(
                salesRecommendation().recommendedProducts()
            )

        assertNotNull(resultRecommendedProduct)
        assertEquals(resultRecommendedProduct?.size, 1)

        val resultProduct = resultRecommendedProduct?.get(0)
        assertEquals(resultProduct?.productId, product?.productId())
        assertEquals(resultProduct?.name, product?.name())
        assertEquals(resultProduct?.offerText, product?.offerText())
        assertEquals(resultProduct?.benefits, product?.benefits())
        assertEquals(resultProduct?.androidSku, product?.androidSku())
        assertEquals(resultProduct?.name, product?.name())
        assertEquals(resultProduct?.userConditions?.action, userConditions?.action())
        assertEquals(
            resultProduct?.userConditions?.trialPeriod,
            userConditions?.eligibleTrialPeriod()
        )

        assertEquals(resultProduct?.paymentInfo?.currency, paymentInfo?.currency())
        assertEquals(resultProduct?.paymentInfo?.frequency?.id, paymentFrequency?.id())
        assertEquals(
            resultProduct?.paymentInfo?.frequency?.periodicityLabel,
            paymentFrequency?.periodicityLabel()
        )
        assertEquals(
            resultProduct?.paymentInfo?.frequency?.timeUnitLabel,
            paymentFrequency?.timeUnitLabel()
        )
        assertEquals(resultProduct?.paymentInfo?.price, paymentInfo?.price())
        assertEquals(resultProduct?.paymentInfo?.callText, paymentInfo?.callText())
        assertEquals(
            resultProduct?.paymentInfo?.numberOfInstallments,
            installmentInfo?.numberOfInstallments()
        )
        assertEquals(
            resultProduct?.paymentInfo?.installmentValue,
            installmentInfo?.installmentValue()
        )

        assertEquals(resultProduct?.productFaq?.link, productFaq?.links()?.mobile())
        assertEquals(resultProduct?.productFaq?.text, productFaq?.text())

        assertEquals(resultProduct?.channels?.size, 1)
        assertEquals(
            resultProduct?.channels?.get(0)?.trimmedLogo,
            product?.channels()?.get(0)?.fragments()?.salesChannels()?.trimmedLogo()
        )

        assertEquals(
            resultProduct?.legalText?.contractUrl,
            product?.legalText()?.fragments()?.salesProductLegalText()?.contractUrl()
        )

        assertEquals(resultProduct?.legalText?.contractsUrlText, legalText?.contractsUrlText())
        assertEquals(resultProduct?.legalText?.legalContent, legalText?.legalContent())

        assertEquals(resultProduct?.productError?.type, productError?.type())
        assertEquals(resultProduct?.productError?.message, productError?.message())
        assertEquals(resultProduct?.productError?.faq?.text, productErrorFaq?.text())
        assertEquals(resultProduct?.productError?.faq?.link, productErrorFaq?.links()?.mobile())

    }

    @Test
    fun givenNullRecommendedProductWhenTransformItThenReturnNull() {

        assertNull(
            getRepository().transformSalesRecommendedProductsQueryToSalesRecommendedProducts(null)
        )
    }

    @Test
    fun givenSalesRecommendedQueryWhenTransformItThenReturnSalesRecommended() {

        val salesRecommended =
            getRepository().transformSalesRecommendationQueryIntoSalesRecommendation(
                salesRecommendation()
            )

        assertNull(salesRecommended?.userError)
        assertEquals(salesRecommended?.recommendedProducts?.size, 1)
    }

    @Test
    fun givenNullSalesRecommendedQueryWhenTransformItThenReturnNull() {

        val salesRecommended =
            getRepository().transformSalesRecommendationQueryIntoSalesRecommendation(
                null
            )

        assertNull(salesRecommended?.userError)
        assertNull(salesRecommended?.recommendedProducts)
    }

    /*
       Default Sales Recommendation
     */
    @Test
    fun givenValuesWhenRequestDefaultSalesRecommendationThenReturnSalesRecommendation() {
        enqueueResponse("sales/defaultsalesrecommendation/default_sales_recommendation_success.json")

        val testObserver = getRepository().defaultRecommendation(
            serviceId = "globoplay",
            broadcastChannelTrimmedLogoScale = "X42"
        ).test()

        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.userError == null
                    && it.recommendedProducts != null
                    && it.recommendedProducts?.size == 1
                    && it.recommendedProducts?.get(0)?.androidSku == "androidSku"
                    && it.recommendedProducts?.get(0)?.productId == "Pro-0038"
                    && it.recommendedProducts?.get(0)?.name == "Globoplay + Canais Ao Vivo"
                    && it.recommendedProducts?.get(0)?.benefits?.containsAll(
                listOf(
                    "Todo catálogo Globoplay + BBB ao vivo",
                    "Até 5 acessos simultâneos",
                    "Adicione até  4 dependentes sem custo",
                    "Assista offline"
                )
            ) ?: false
                    && it.recommendedProducts?.get(0)?.paymentInfo?.callText == "Cobrança única renovada a cada 12 meses."
                    && it.recommendedProducts?.get(0)?.paymentInfo?.currency == "R$"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.price == "514.80"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.id == "ANNUAL"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.periodicityLabel == "Anual"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.timeUnitLabel == "ano"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.installmentValue == "42.90"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.numberOfInstallments == 12

                    && it.recommendedProducts?.get(0)?.productFaq?.link == "https://globoplay.globo.com/faq/"
                    && it.recommendedProducts?.get(0)?.productFaq?.text == "Saiba mais"

                    && it.recommendedProducts?.get(0)?.channels?.size == 3
                    && it.recommendedProducts?.get(0)?.channels?.containsAll(
                listOf(
                    Broadcast(
                        name = "TV Globo",
                        trimmedLogo = "https://s2.glbimg.com/n9swMvNkdkEcW98ahrTa_Oorwu4=/fit-in/84x42/https://s2.glbimg.com/gYVgRoEjdh0NUYIUtm1IJ7aid_E=/fit-in/48x24/https://s2.glbimg.com/kL_UPRW5kuYPAJ4-MVU4GI2a-Qc=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/p/r/AzV1BMTgScfblhcB7iHw/globo-png.png"
                    ),
                    Broadcast(
                        name = "SportTv",
                        trimmedLogo = "https://s2.glbimg.com/IN1wJdoaPXUefNVBIFM_qzCKUbo=/fit-in/84x42/https://s2.glbimg.com/XYHjDTXCzgv81O3kmoah6EHXvgU=/fit-in/48x24/https://s2.glbimg.com/YlpcJ25X1zQJh2JO3F3AUoZ4G60=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/e/X/bqOqIqSNyy3bBb7acyXA/sportv.png"
                    ),
                    Broadcast(
                        name = "SportTv 2",
                        trimmedLogo = "https://s2.glbimg.com/a9DLDdq3s_BKAID_rYmmPE9P6tI=/fit-in/84x42/https://s2.glbimg.com/aSej71Fh1JQl-h0gooZtT5Huojc=/fit-in/48x24/https://s2.glbimg.com/2vWqsnLXKCnXWwEJapUkEaqyKcw=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/q/X/APSAlSTvSFHcJfdodXaQ/sportv-2.png"
                    )
                )
            ) ?: false
                    && it.recommendedProducts?.get(0)?.legalText?.legalContent == "legalContent"
                    && it.recommendedProducts?.get(0)?.legalText?.contractUrl == "contractUrl"
                    && it.recommendedProducts?.get(0)?.legalText?.contractsUrlText == "contractsUrlText"
                    && it.recommendedProducts?.get(0)?.productError == null
                    && it.recommendedProducts?.get(0)?.userConditions?.action == "BUY"
                    && it.recommendedProducts?.get(0)?.userConditions?.trialPeriod == 7
        }
    }

    @Test
    fun givenValuesWhenRequestDefaultSalesRecommendationThenReturnSalesRecommendationWithProductError() {
        enqueueResponse("sales/defaultsalesrecommendation/default_sales_recommendation_product_error.json")

        val testObserver = getRepository().defaultRecommendation(
            serviceId = "globoplay",
            broadcastChannelTrimmedLogoScale = "X42"
        ).test()

        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.userError == null
                    && it.recommendedProducts != null
                    && it.recommendedProducts?.size == 1
                    && it.recommendedProducts?.get(0)?.androidSku == "androidSku"
                    && it.recommendedProducts?.get(0)?.productId == "Pro-0038"
                    && it.recommendedProducts?.get(0)?.name == "Globoplay + Canais Ao Vivo"
                    && it.recommendedProducts?.get(0)?.benefits?.containsAll(
                listOf(
                    "Todo catálogo Globoplay + BBB ao vivo",
                    "Até 5 acessos simultâneos",
                    "Adicione até  4 dependentes sem custo",
                    "Assista offline"
                )
            ) ?: false
                    && it.recommendedProducts?.get(0)?.paymentInfo?.callText == "Cobrança única renovada a cada 12 meses."
                    && it.recommendedProducts?.get(0)?.paymentInfo?.currency == "R$"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.price == "514.80"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.id == "ANNUAL"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.periodicityLabel == "Anual"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.frequency?.timeUnitLabel == "ano"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.installmentValue == "42.90"
                    && it.recommendedProducts?.get(0)?.paymentInfo?.numberOfInstallments == 12

                    && it.recommendedProducts?.get(0)?.productFaq?.link == "https://globoplay.globo.com/faq/"
                    && it.recommendedProducts?.get(0)?.productFaq?.text == "Saiba mais"

                    && it.recommendedProducts?.get(0)?.channels?.size == 3
                    && it.recommendedProducts?.get(0)?.channels?.containsAll(
                listOf(
                    Broadcast(
                        name = "TV Globo",
                        trimmedLogo = "https://s2.glbimg.com/n9swMvNkdkEcW98ahrTa_Oorwu4=/fit-in/84x42/https://s2.glbimg.com/gYVgRoEjdh0NUYIUtm1IJ7aid_E=/fit-in/48x24/https://s2.glbimg.com/kL_UPRW5kuYPAJ4-MVU4GI2a-Qc=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/p/r/AzV1BMTgScfblhcB7iHw/globo-png.png"
                    ),
                    Broadcast(
                        name = "SportTv",
                        trimmedLogo = "https://s2.glbimg.com/IN1wJdoaPXUefNVBIFM_qzCKUbo=/fit-in/84x42/https://s2.glbimg.com/XYHjDTXCzgv81O3kmoah6EHXvgU=/fit-in/48x24/https://s2.glbimg.com/YlpcJ25X1zQJh2JO3F3AUoZ4G60=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/e/X/bqOqIqSNyy3bBb7acyXA/sportv.png"
                    ),
                    Broadcast(
                        name = "SportTv 2",
                        trimmedLogo = "https://s2.glbimg.com/a9DLDdq3s_BKAID_rYmmPE9P6tI=/fit-in/84x42/https://s2.glbimg.com/aSej71Fh1JQl-h0gooZtT5Huojc=/fit-in/48x24/https://s2.glbimg.com/2vWqsnLXKCnXWwEJapUkEaqyKcw=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/q/X/APSAlSTvSFHcJfdodXaQ/sportv-2.png"
                    )
                )
            ) ?: false
                    && it.recommendedProducts?.get(0)?.legalText?.legalContent == "legalContent"
                    && it.recommendedProducts?.get(0)?.legalText?.contractUrl == "contractUrl"
                    && it.recommendedProducts?.get(0)?.legalText?.contractsUrlText == "contractsUrlText"
                    && it.recommendedProducts?.get(0)?.userConditions?.trialPeriod == 7
                    && it.recommendedProducts?.get(0)?.userConditions?.action == "BUY"

                    && it.recommendedProducts?.get(0)?.productError != null
                    && it.recommendedProducts?.get(0)?.productError?.message == "Você possui um plano no qual está em período de trial. Após o fim deste período você poderá efetuar a compra."
                    && it.recommendedProducts?.get(0)?.productError?.type == "ON_TRIAL"
                    && it.recommendedProducts?.get(0)?.productError?.faq?.text == "Saiba mais"
                    && it.recommendedProducts?.get(0)?.productError?.faq?.link == "https://globoplay.globo.com/faq/"
                    && it.recommendedProducts?.get(0)?.productError?.sourceChannel == null
        }
    }

    @Test
    fun givenValuesWhenRequestDefaultSalesRecommendationThenReturnSalesRecommendationUserError() {
        enqueueResponse("sales/defaultsalesrecommendation/default_sales_recommendation_error.json")

        val testObserver = getRepository().defaultRecommendation(
            serviceId = "globoplay",
            broadcastChannelTrimmedLogoScale = "X42"
        ).test()

        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.recommendedProducts == null
                    && it.userError != null
                    && it.userError?.type == "INDEBT"
                    && it.userError?.message == "Identificamos que você possui um débito pendente. Para assinar o Plano Globoplay + Canais é necessário a regulaziração do mesmo."
                    && it.userError?.faq?.link == "https://globoplay.globo.com/faq/"
                    && it.userError?.faq?.text == "Saiba mais"


        }
    }

    @Test
    fun givenDefaultRecommendedProductWhenTransformItThenReturnRecommendedProduct() {

        val recommendedProduct = salesRecommendation().recommendedProducts()?.get(0)
        val product = recommendedProduct?.product()?.fragments()?.salesProduct()
        val productError = recommendedProduct?.productError()?.fragments()?.salesProductError()
        val userConditions = recommendedProduct?.userConditions()?.fragments()?.salesUserCondition()
        val productErrorFaq = productError?.faq()?.fragments()?.salesFaq()
        val paymentInfo = product?.paymentInfo()?.fragments()?.salesPaymentInfo()
        val paymentFrequency = paymentInfo?.frequency()?.fragments()?.salesPaymentFrequency()
        val legalText = product?.legalText()?.fragments()?.salesProductLegalText()
        val installmentInfo = paymentInfo?.installmentInfo()?.fragments()?.salesInstallmentInfo()
        val productFaq = product?.faq()?.fragments()?.salesFaq()

        val resultRecommendedProduct =
            getRepository().transformDefaultRecommendedProductsQueryToSalesRecommendedProducts(
                defaultSalesRecommendation().recommendedProducts()
            )

        assertNotNull(resultRecommendedProduct)
        assertEquals(resultRecommendedProduct?.size, 1)

        val resultProduct = resultRecommendedProduct?.get(0)
        assertEquals(resultProduct?.productId, product?.productId())
        assertEquals(resultProduct?.name, product?.name())
        assertEquals(resultProduct?.offerText, product?.offerText())
        assertEquals(resultProduct?.benefits, product?.benefits())
        assertEquals(resultProduct?.androidSku, product?.androidSku())
        assertEquals(resultProduct?.name, product?.name())
        assertEquals(resultProduct?.userConditions?.action, userConditions?.action())
        assertEquals(
            resultProduct?.userConditions?.trialPeriod,
            userConditions?.eligibleTrialPeriod()
        )

        assertEquals(resultProduct?.paymentInfo?.currency, paymentInfo?.currency())
        assertEquals(resultProduct?.paymentInfo?.frequency?.id, paymentFrequency?.id())
        assertEquals(
            resultProduct?.paymentInfo?.frequency?.periodicityLabel,
            paymentFrequency?.periodicityLabel()
        )
        assertEquals(
            resultProduct?.paymentInfo?.frequency?.timeUnitLabel,
            paymentFrequency?.timeUnitLabel()
        )
        assertEquals(resultProduct?.paymentInfo?.price, paymentInfo?.price())
        assertEquals(resultProduct?.paymentInfo?.callText, paymentInfo?.callText())
        assertEquals(
            resultProduct?.paymentInfo?.numberOfInstallments,
            installmentInfo?.numberOfInstallments()
        )
        assertEquals(
            resultProduct?.paymentInfo?.installmentValue,
            installmentInfo?.installmentValue()
        )

        assertEquals(resultProduct?.productFaq?.link, productFaq?.links()?.mobile())
        assertEquals(resultProduct?.productFaq?.text, productFaq?.text())

        assertEquals(resultProduct?.channels?.size, 1)
        assertEquals(
            resultProduct?.channels?.get(0)?.trimmedLogo,
            product?.channels()?.get(0)?.fragments()?.salesChannels()?.trimmedLogo()
        )

        assertEquals(
            resultProduct?.legalText?.contractUrl,
            product?.legalText()?.fragments()?.salesProductLegalText()?.contractUrl()
        )

        assertEquals(resultProduct?.legalText?.contractsUrlText, legalText?.contractsUrlText())
        assertEquals(resultProduct?.legalText?.legalContent, legalText?.legalContent())

        assertEquals(resultProduct?.productError?.type, productError?.type())
        assertEquals(resultProduct?.productError?.message, productError?.message())
        assertEquals(resultProduct?.productError?.faq?.text, productErrorFaq?.text())
        assertEquals(resultProduct?.productError?.faq?.link, productErrorFaq?.links()?.mobile())

    }

    @Test
    fun givenNullDefaultRecommendedProductWhenTransformItThenReturnNull() {

        assertNull(
            getRepository().transformDefaultRecommendedProductsQueryToSalesRecommendedProducts(null)
        )
    }

    @Test
    fun givenDefaultSalesRecommendedQueryWhenTransformItThenReturnSalesRecommended() {

        val salesRecommended =
            getRepository().transformDefaultSalesRecommendationQueryIntoSalesRecommendation(
                defaultSalesRecommendation()
            )

        assertNull(salesRecommended?.userError)
        assertEquals(salesRecommended?.recommendedProducts?.size, 1)
    }

    @Test
    fun givenNullDefaultSalesRecommendedQueryWhenTransformItThenReturnNull() {

        val salesRecommended =
            getRepository().transformDefaultSalesRecommendationQueryIntoSalesRecommendation(
                null
            )

        assertNull(salesRecommended?.userError)
        assertNull(salesRecommended?.recommendedProducts)
    }

    @Test
    fun givenNoSalesFrequencyQueryWhenTransformSalesFrequencyQueryToSalesFrequencyThenReturnNull() {

        assertNull(getRepository().transformSalesFrequencyFragmentToSalesFrequency(null))
    }

    @Test
    fun givenSalesFrequencyQueryWhenTransformItThenReturnSalesFrequency() {

        val salesFrequency = getRepository().transformSalesFrequencyFragmentToSalesFrequency(
            salesPaymentFrequencyInfo
        )

        assertEquals(salesFrequency?.id, salesPaymentFrequencyInfo.id())
        assertEquals(salesFrequency?.periodicityLabel, salesPaymentFrequencyInfo.periodicityLabel())
        assertEquals(salesFrequency?.timeUnitLabel, salesPaymentFrequencyInfo.timeUnitLabel())
    }

    @Test
    fun givenSalesUserErrorFaqWhenTransformItThenReturnSalesFaq() {

        val salesFaq = salesRecommendationUserErrorQuery.faq()

        val errorFaq = getRepository().transformSalesFaqFragmentToSalesSalesFaq(
            salesFaq?.fragments()?.salesFaq()
        )

        assertEquals(errorFaq?.link, salesFaq?.fragments()?.salesFaq()?.links()?.mobile())
        assertEquals(errorFaq?.text, salesFaq?.fragments()?.salesFaq()?.text())
    }

    @Test
    fun givenNullProductErrorFaqWhenTransformItThenReturnNullSalesFaq() {

        val errorFaq = getRepository().transformSalesFaqFragmentToSalesSalesFaq(null)

        assertNull(errorFaq)
    }

    @Test
    fun givenSalesUserErrorWhenTransformItThenReturnSalesError() {

        val faqError = salesRecommendationUserErrorQuery.faq()?.fragments()?.salesFaq()

        val salesError = getRepository().transformUserErrorFragmentToSalesUserError(
            salesRecommendationUserErrorQuery
        )

        assertEquals(salesError?.message, salesRecommendationUserErrorQuery.message())
        assertEquals(salesError?.type, salesRecommendationUserErrorQuery.type())
        assertEquals(salesError?.faq?.text, faqError?.text())
        assertEquals(salesError?.faq?.link, faqError?.links()?.mobile())
    }

    @Test
    fun givenNullSalesUserErrorWhenTransformItThenReturnNull() {

        val salesError = getRepository().transformUserErrorFragmentToSalesUserError(null)

        assertNull(salesError)
    }

    @Test
    fun givenProductErrorWhenTransformItThenReturnSalesError() {

        val channelError = salesProductErrorQuery.metadata()?.fragments()?.changeChannelError()
        val faqError = salesProductErrorQuery.faq()?.fragments()?.salesFaq()

        val salesError = getRepository().transformProductErrorFragmentToSalesError(
            salesProductErrorQuery
        )

        assertEquals(salesError?.message, salesProductErrorQuery.message())
        assertEquals(salesError?.type, salesProductErrorQuery.type())
        assertEquals(
            salesError?.sourceChannel,
            SalesPlatformType.normalize(channelError?.sourceChannel())
        )
        assertEquals(salesError?.faq?.text, faqError?.text())
        assertEquals(salesError?.faq?.link, faqError?.links()?.mobile())
    }

    @Test
    fun givenNullProductErrorWhenTransformItThenReturnNull() {

        val salesError = getRepository().transformProductErrorFragmentToSalesError(null)

        assertNull(salesError)
    }

    @Test
    fun givenNoSalesUserConditionsQueryWhenTransformItThenReturnNull() {

        assertNull(getRepository().transformUserConditionsFragmentToUserConditions(null))
    }

    @Test
    fun givenSalesUserConditionsQueryWhenTransformItThenReturnSalesUserCondition() {

        val salesUserCondition = getRepository().transformUserConditionsFragmentToUserConditions(
            salesUserConditions
        )

        assertEquals(salesUserCondition?.action, salesUserConditions.action())
        assertEquals(salesUserCondition?.trialPeriod, salesUserConditions.eligibleTrialPeriod())
    }

    @Test
    fun givenSalesLegalTextWhenTransformItThenReturnLegalText() {

        val legalText = getRepository().transformSalesProductLegalTextFragmentSalesLegalText(
            salesLegalText
        )

        assertEquals(legalText?.contractUrl, salesLegalText.contractUrl())
        assertEquals(legalText?.legalContent, salesLegalText.legalContent())
        assertEquals(legalText?.contractsUrlText, salesLegalText.contractsUrlText())
    }

    @Test
    fun givenNullSalesLegalTextWhenTransformItThenReturnNull() {

        assertNull(getRepository().transformSalesProductLegalTextFragmentSalesLegalText(null))
    }

    @Test
    fun givenBroadcastChannelListWhenTransformItThenReturnListOfTrimmedLogo() {

        val channels = getRepository().transformBroadcastChannelFragmentToBroadcastChannels(
            salesRecommendedProduct.channels()
        )

        assertEquals(channels?.size, salesRecommendedProduct.channels()?.size)
        assertEquals(
            channels?.get(0)?.name,
            salesRecommendedProduct.channels()?.get(0)?.fragments()?.salesChannels()?.name()
        )
        assertEquals(
            channels?.get(0)?.trimmedLogo,
            salesRecommendedProduct.channels()?.get(0)?.fragments()?.salesChannels()?.trimmedLogo()
        )
    }

    @Test
    fun givenNullBroadcastChannelListWhenTransformItThenReturnNull() {

        val channels = getRepository().transformBroadcastChannelFragmentToBroadcastChannels(null)

        assertNull(channels)
    }

    @Test
    fun givenPaymentInfoWhenTransformItThenReturnPaymentInfo() {

        val paymentFrequency = salesPaymentInfo.frequency().fragments().salesPaymentFrequency()
        val installmentInfo =
            salesPaymentInfo.installmentInfo()?.fragments()?.salesInstallmentInfo()

        val productPaymentInfo = getRepository().transformDefaultPaymentInfoFragmentToPaymentInfo(
            salesPaymentInfo
        )

        assertEquals(productPaymentInfo?.callText, salesPaymentInfo.callText())
        assertEquals(productPaymentInfo?.price, salesPaymentInfo.price())
        assertEquals(productPaymentInfo?.frequency?.id, paymentFrequency.id())
        assertEquals(
            productPaymentInfo?.frequency?.periodicityLabel,
            paymentFrequency.periodicityLabel()
        )
        assertEquals(
            productPaymentInfo?.frequency?.timeUnitLabel,
            paymentFrequency.timeUnitLabel()
        )
        assertEquals(productPaymentInfo?.currency, salesPaymentInfo.currency())
        assertEquals(productPaymentInfo?.installmentValue, installmentInfo?.installmentValue())
        assertEquals(
            productPaymentInfo?.numberOfInstallments,
            installmentInfo?.numberOfInstallments()
        )
    }

    @Test
    fun givenNullPaymentInfoWhenTransformItThenReturnNull() {

        val productPaymentInfo =
            getRepository().transformDefaultPaymentInfoFragmentToPaymentInfo(null)

        assertNull(productPaymentInfo)
    }

    private fun getRepository(client: ApolloClient = apolloClient, device: Device = Device.MOBILE) =
        spyk(
            SalesRepository(
                apolloClient = client,
                device = device
            )
        )

    private fun salesRecommendation(enableUserError: Boolean = false) =
        if (enableUserError) {
            SalesRecommendationQuery.SalesRecommendation(
                "SalesRecommendation",
                SalesRecommendationQuery.SalesUserError(
                    "SalesUserError",
                    SalesRecommendationQuery.SalesUserError.Fragments(
                        salesRecommendationUserErrorQuery
                    )
                ),
                null
            )
        } else {
            SalesRecommendationQuery.SalesRecommendation(
                "SalesRecommendation",
                null,
                listOf(
                    SalesRecommendationQuery.RecommendedProduct(
                        "RecommendedProduct",
                        SalesRecommendationQuery.ProductError(
                            "ProductError",
                            SalesRecommendationQuery.ProductError.Fragments(
                                salesProductErrorQuery
                            )
                        ),
                        SalesRecommendationQuery.UserConditions(
                            "UserConditions",
                            SalesRecommendationQuery.UserConditions.Fragments(
                                salesUserConditions
                            )
                        ),
                        SalesRecommendationQuery.Product(
                            "Product",
                            SalesRecommendationQuery.Product.Fragments(
                                salesRecommendedProduct
                            )
                        )
                    )
                )
            )
        }

    private fun defaultSalesRecommendation(enableUserError: Boolean = false) =
        if (enableUserError) {
            DefaultSalesRecommendationQuery.DefaultSalesRecommendation(
                "SalesRecommendation",
                DefaultSalesRecommendationQuery.SalesUserError(
                    "SalesUserError",
                    DefaultSalesRecommendationQuery.SalesUserError.Fragments(
                        salesRecommendationUserErrorQuery
                    )
                ),
                null
            )
        } else {
            DefaultSalesRecommendationQuery.DefaultSalesRecommendation(
                "SalesRecommendation",
                null,
                listOf(
                    DefaultSalesRecommendationQuery.RecommendedProduct(
                        "RecommendedProduct",
                        DefaultSalesRecommendationQuery.ProductError(
                            "ProductError",
                            DefaultSalesRecommendationQuery.ProductError.Fragments(
                                salesProductErrorQuery
                            )
                        ),
                        DefaultSalesRecommendationQuery.UserConditions(
                            "UserConditions",
                            DefaultSalesRecommendationQuery.UserConditions.Fragments(
                                salesUserConditions
                            )
                        ),
                        DefaultSalesRecommendationQuery.Product(
                            "Product",
                            DefaultSalesRecommendationQuery.Product.Fragments(
                                salesRecommendedProduct
                            )
                        )
                    )
                )
            )
        }
}