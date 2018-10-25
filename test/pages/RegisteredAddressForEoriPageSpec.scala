package pages

import models.RegisteredAddressForEori
import pages.behaviours.PageBehaviours

class RegisteredAddressForEoriPageSpec extends PageBehaviours {

  "RegisteredAddressForEoriPage" must {

    beRetrievable[RegisteredAddressForEori](RegisteredAddressForEoriPage)

    beSettable[RegisteredAddressForEori](RegisteredAddressForEoriPage)

    beRemovable[RegisteredAddressForEori](RegisteredAddressForEoriPage)
  }
}
