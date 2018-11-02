package pages

import pages.behaviours.PageBehaviours


class CommodityCodeDigitsPageSpec extends PageBehaviours {

  "CommodityCodeDigitsPage" must {

    beRetrievable[String](CommodityCodeDigitsPage)

    beSettable[String](CommodityCodeDigitsPage)

    beRemovable[String](CommodityCodeDigitsPage)
  }
}
