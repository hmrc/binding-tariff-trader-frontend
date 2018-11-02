package pages

import models.CommodityCodeBestMatch
import pages.behaviours.PageBehaviours

class CommodityCodeBestMatchSpec extends PageBehaviours {

  "YourLocation" must {

    beRetrievable[CommodityCodeBestMatch](CommodityCodeBestMatchPage)

    beSettable[CommodityCodeBestMatch](CommodityCodeBestMatchPage)

    beRemovable[CommodityCodeBestMatch](CommodityCodeBestMatchPage)
  }
}
