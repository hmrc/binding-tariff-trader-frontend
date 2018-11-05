package pages

import pages.behaviours.PageBehaviours


class CommodityCodeRulingReferencePageSpec extends PageBehaviours {

  "CommodityCodeRulingReferencePage" must {

    beRetrievable[String](CommodityCodeRulingReferencePage)

    beSettable[String](CommodityCodeRulingReferencePage)

    beRemovable[String](CommodityCodeRulingReferencePage)
  }
}
