package pages

import pages.behaviours.PageBehaviours


class UploadSupportingMaterialMultiplePageSpec extends PageBehaviours {

  "UploadSupportingMaterialMultiplePage" must {

    beRetrievable[String](UploadSupportingMaterialMultiplePage)

    beSettable[String](UploadSupportingMaterialMultiplePage)

    beRemovable[String](UploadSupportingMaterialMultiplePage)
  }
}
