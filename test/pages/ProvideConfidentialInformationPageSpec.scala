package pages

import pages.behaviours.PageBehaviours


class ProvideConfidentialInformationPageSpec extends PageBehaviours {

  "ProvideConfidentialInformationPage" must {

    beRetrievable[String](ProvideConfidentialInformationPage)

    beSettable[String](ProvideConfidentialInformationPage)

    beRemovable[String](ProvideConfidentialInformationPage)
  }
}
