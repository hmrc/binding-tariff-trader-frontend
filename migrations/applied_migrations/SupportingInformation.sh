#!/bin/bash

echo "Applying migration SupportingInformation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /supportingInformation               controllers.SupportingInformationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /supportingInformation               controllers.SupportingInformationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSupportingInformation                  controllers.SupportingInformationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSupportingInformation                  controllers.SupportingInformationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "supportingInformation.title = SupportingInformation" >> ../conf/messages.en
echo "supportingInformation.heading = SupportingInformation" >> ../conf/messages.en
echo "supportingInformation.yesInformation = Yes - I have other information" >> ../conf/messages.en
echo "supportingInformation.noInformation = No - I do not have any other information" >> ../conf/messages.en
echo "supportingInformation.checkYourAnswersLabel = SupportingInformation" >> ../conf/messages.en
echo "supportingInformation.error.required = Select supportingInformation" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySupportingInformationUserAnswersEntry: Arbitrary[(SupportingInformationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SupportingInformationPage.type]";\
    print "        value <- arbitrary[SupportingInformation].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySupportingInformationPage: Arbitrary[SupportingInformationPage.type] =";\
    print "    Arbitrary(SupportingInformationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySupportingInformation: Arbitrary[SupportingInformation] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(SupportingInformation.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SupportingInformationPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def supportingInformation: Option[AnswerRow] = userAnswers.get(SupportingInformationPage) map {";\
     print "    x => AnswerRow(\"supportingInformation.checkYourAnswersLabel\", s\"supportingInformation.$x\", true, routes.SupportingInformationController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SupportingInformation completed"
