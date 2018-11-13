#!/bin/bash

echo "Applying migration SupportingInformationDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /supportingInformationDetails                        controllers.SupportingInformationDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /supportingInformationDetails                        controllers.SupportingInformationDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSupportingInformationDetails                  controllers.SupportingInformationDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSupportingInformationDetails                  controllers.SupportingInformationDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "supportingInformationDetails.title = supportingInformationDetails" >> ../conf/messages.en
echo "supportingInformationDetails.heading = supportingInformationDetails" >> ../conf/messages.en
echo "supportingInformationDetails.checkYourAnswersLabel = supportingInformationDetails" >> ../conf/messages.en
echo "supportingInformationDetails.error.required = Enter supportingInformationDetails" >> ../conf/messages.en
echo "supportingInformationDetails.error.length = SupportingInformationDetails must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySupportingInformationDetailsUserAnswersEntry: Arbitrary[(SupportingInformationDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SupportingInformationDetailsPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySupportingInformationDetailsPage: Arbitrary[SupportingInformationDetailsPage.type] =";\
    print "    Arbitrary(SupportingInformationDetailsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SupportingInformationDetailsPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def supportingInformationDetails: Option[AnswerRow] = userAnswers.get(SupportingInformationDetailsPage) map {";\
     print "    x => AnswerRow(\"supportingInformationDetails.checkYourAnswersLabel\", s\"$x\", false, routes.SupportingInformationDetailsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SupportingInformationDetails completed"
