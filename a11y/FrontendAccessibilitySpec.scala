/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import config.FrontendAppConfig
import forms.{EnterContactDetailsFormProvider, ProvideBTIReferenceFormProvider, RegisteredAddressForEoriFormProvider}
import models._
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.api.data.Forms.{boolean, text, tuple}
import play.api.mvc.{Request, RequestHeader}
import play.api.test.CSRFTokenHelper
import play.twirl.api.Html
import service.CountriesService
import uk.gov.hmrc.scalatestaccessibilitylinter.views.AutomaticAccessibilitySpec
import viewmodels._
import views.CaseDetailTab
import views.html._

class FrontendAccessibilitySpec extends AutomaticAccessibilitySpec {

  private val appConfig: FrontendAppConfig       = app.injector.instanceOf[FrontendAppConfig]
  private val `case`: Case                       = oCase.btiCaseWithDecision
  private val pagedCase: Paged[Case]             = Paged[Case](Seq(`case`))
  private val dashboard: Dashboard               = Dashboard(pagedCase, Sort())
  private val countriesService: CountriesService = new CountriesService
  private val answerSection: AnswerSection =
    AnswerSection(Some("checkYourAnswers"), Seq(AnswerRow("", "", answerIsMessageKey = true, "/")))

  private val provideBTIReferenceForm: ProvideBTIReferenceFormProvider = new ProvideBTIReferenceFormProvider
  private val enterContactDetailsForm: EnterContactDetailsFormProvider = new EnterContactDetailsFormProvider
  private val registeredAddressForEoriForm: RegisteredAddressForEoriFormProvider =
    new RegisteredAddressForEoriFormProvider

  implicit val arbAppConfig: Arbitrary[FrontendAppConfig] = fixed(appConfig)
  implicit val arbRequestHeader: Arbitrary[RequestHeader] = fixed(fakeRequest)
  implicit val arbHtmlInput: Arbitrary[Html]              = fixed(Html.apply(""))
  override implicit val arbAsciiString: Arbitrary[String] = fixed("/")

  implicit val arbMode: Arbitrary[Mode]                                      = fixed(NormalMode)
  implicit val arbCase: Arbitrary[Case]                                      = fixed(`case`)
  implicit val arbDashboard: Arbitrary[Dashboard]                            = fixed(dashboard)
  implicit val arbCaseDetailTab: Arbitrary[CaseDetailTab.Value]              = fixed(CaseDetailTab.APPLICATION)
  implicit val arbConfirmationViewModel: Arbitrary[ConfirmationUrlViewModel] = fixed(ConfirmationUrlViewModel(true))

  implicit val arbCountriesList: Arbitrary[List[Country]]              = fixed(countriesService.getAllCountries)
  implicit val arbAnswerSectionSequence: Arbitrary[Seq[AnswerSection]] = fixed(Seq(answerSection))

  implicit val arbEnterContactDetailsForm: Arbitrary[Form[EnterContactDetails]] = fixed(enterContactDetailsForm())
  implicit val arbProvideBTIReferenceForm: Arbitrary[Form[BTIReference]]        = fixed(provideBTIReferenceForm())
  implicit val arbRegisteredAddressForEoriForm: Arbitrary[Form[RegisteredAddressForEori]] = fixed(
    registeredAddressForEoriForm()
  )

  implicit val arbBooleanForm: Arbitrary[Form[Boolean]]  = fixed(Form("value" -> boolean))
  implicit val arbTextInputForm: Arbitrary[Form[String]] = fixed(Form("value" -> text))
  implicit val arbTupleForm: Arbitrary[Form[(String, Boolean)]] = fixed(
    Form(tuple("value" -> text, "value" -> boolean))
  )

  override def renderViewByClass: PartialFunction[Any, Html] = {
    case acceptItemInformationList: acceptItemInformationList               => render(acceptItemInformationList)
    case account_dashboard_statuses: account_dashboard_statuses             => render(account_dashboard_statuses)
    case addAnotherRuling: addAnotherRuling                                 => render(addAnotherRuling)
    case addConfidentialInformation: addConfidentialInformation             => render(addConfidentialInformation)
    case addSupportingDocuments: addSupportingDocuments                     => render(addSupportingDocuments)
    case areYouSendingSamples: areYouSendingSamples                         => render(areYouSendingSamples)
    case beforeYouStart: beforeYouStart                                     => render(beforeYouStart)
    case check_your_answers: check_your_answers                             => render(check_your_answers)
    case commodityCodeBestMatch: commodityCodeBestMatch                     => render(commodityCodeBestMatch)
    case commodityCodeDigits: commodityCodeDigits                           => render(commodityCodeDigits)
    case commodityCodeRulingReference: commodityCodeRulingReference         => render(commodityCodeRulingReference)
    case confirmation: confirmation                                         => render(confirmation)
    case contactCustomsDutyLiabilityTeam: contactCustomsDutyLiabilityTeam   => render(contactCustomsDutyLiabilityTeam)
    case documentNotFound: documentNotFound                                 => render(documentNotFound)
    case enterContactDetails: enterContactDetails                           => render(enterContactDetails)
    case error_template: error_template                                     => render(error_template)
    case howWeContactYou: howWeContactYou                                   => render(howWeContactYou)
    case informationPublic: informationPublic                               => render(informationPublic)
    case isSampleHazardous: isSampleHazardous                               => render(isSampleHazardous)
    case legalChallenge: legalChallenge                                     => render(legalChallenge)
    case legalChallengeDetails: legalChallengeDetails                       => render(legalChallengeDetails)
    case makeFileConfidential: makeFileConfidential                         => render(makeFileConfidential)
    case previousBTIRuling: previousBTIRuling                               => render(previousBTIRuling)
    case provideBTIReference: provideBTIReference                           => render(provideBTIReference)
    case provideConfidentialInformation: provideConfidentialInformation     => render(provideConfidentialInformation)
    case provideGoodsDescription: provideGoodsDescription                   => render(provideGoodsDescription)
    case provideGoodsName: provideGoodsName                                 => render(provideGoodsName)
    case registeredAddressForEori: registeredAddressForEori                 => render(registeredAddressForEori)
    case returnSamples: returnSamples                                       => render(returnSamples)
    case ruling_information: ruling_information                             => render(ruling_information)
    case session_expired: session_expired                                   => render(session_expired)
    case similarItemCommodityCode: similarItemCommodityCode                 => render(similarItemCommodityCode)
    case supportingMaterialFileList: supportingMaterialFileList             => render(supportingMaterialFileList)
    case uploadSupportingMaterialMultiple: uploadSupportingMaterialMultiple =>
      implicit val arbRequest: Arbitrary[Request[_]] = fixed(CSRFTokenHelper.addCSRFToken(fakeRequest))
      render(uploadSupportingMaterialMultiple)
    case applicationView: applicationView                                   => render(applicationView)
    case rulingCertificateView: rulingCertificateView                       => render(rulingCertificateView)
  }

  override def viewPackageName: String = "views.html"

  override def layoutClasses: Seq[Class[GovukLayoutWrapper]] = Seq(classOf[GovukLayoutWrapper])

  runAccessibilityTests()
}
