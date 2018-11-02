package pages

import models.SimilarItemCommodityCode
import pages.behaviours.PageBehaviours

class SimilarItemCommodityCodeSpec extends PageBehaviours {

  "YourLocation" must {

    beRetrievable[SimilarItemCommodityCode](SimilarItemCommodityCodePage)

    beSettable[SimilarItemCommodityCode](SimilarItemCommodityCodePage)

    beRemovable[SimilarItemCommodityCode](SimilarItemCommodityCodePage)
  }
}
