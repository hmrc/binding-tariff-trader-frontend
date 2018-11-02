package pages

import models.WhenToSendSample
import pages.behaviours.PageBehaviours

class WhenToSendSampleSpec extends PageBehaviours {

  "YourLocation" must {

    beRetrievable[WhenToSendSample](WhenToSendSamplePage)

    beSettable[WhenToSendSample](WhenToSendSamplePage)

    beRemovable[WhenToSendSample](WhenToSendSamplePage)
  }
}
