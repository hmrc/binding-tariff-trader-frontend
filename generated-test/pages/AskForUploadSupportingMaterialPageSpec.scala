package pages

import pages.behaviours.PageBehaviours

class AskForUploadSupportingMaterialPageSpec extends PageBehaviours {

  "AskForUploadSupportingMaterialPage" must {

    beRetrievable[Boolean](AskForUploadSupportingMaterialPage)

    beSettable[Boolean](AskForUploadSupportingMaterialPage)

    beRemovable[Boolean](AskForUploadSupportingMaterialPage)
  }
}
