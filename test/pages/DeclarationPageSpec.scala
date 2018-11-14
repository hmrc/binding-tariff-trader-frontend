package pages

import pages.behaviours.PageBehaviours


class DeclarationPageSpec extends PageBehaviours {

  "DeclarationPage" must {

    beRetrievable[String](DeclarationPage)

    beSettable[String](DeclarationPage)

    beRemovable[String](DeclarationPage)
  }
}
