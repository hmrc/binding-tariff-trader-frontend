package pages

import models.SupportingInformation
import pages.behaviours.PageBehaviours

class SupportingInformationSpec extends PageBehaviours {

  "YourLocation" must {

    beRetrievable[SupportingInformation](SupportingInformationPage)

    beSettable[SupportingInformation](SupportingInformationPage)

    beRemovable[SupportingInformation](SupportingInformationPage)
  }
}
