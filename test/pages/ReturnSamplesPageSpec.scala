package pages

import models.ReturnSamples
import pages.behaviours.PageBehaviours

class ReturnSamplesSpec extends PageBehaviours {

  "YourLocation" must {

    beRetrievable[ReturnSamples](ReturnSamplesPage)

    beSettable[ReturnSamples](ReturnSamplesPage)

    beRemovable[ReturnSamples](ReturnSamplesPage)
  }
}
