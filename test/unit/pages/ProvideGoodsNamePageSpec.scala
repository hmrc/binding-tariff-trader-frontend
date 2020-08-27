package unit.pages

import pages.ProvideGoodsNamePage
import pages.behaviours.PageBehaviours


class ProvideGoodsNamePageSpec extends PageBehaviours {

  "provideGoodsNamePage" must {

    beRetrievable[String](ProvideGoodsNamePage)

    beSettable[String](ProvideGoodsNamePage)

    beRemovable[String](ProvideGoodsNamePage)
  }
}
