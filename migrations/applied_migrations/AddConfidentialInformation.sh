#!/bin/bash

echo "Applying migration AddConfidentialInformation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /add-confidential-information                        controllers.AddConfidentialInformationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /add-confidential-information                        controllers.AddConfidentialInformationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /change-add-confidential-information                 controllers.AddConfidentialInformationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /change-add-confidential-information                 controllers.AddConfidentialInformationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "addConfidentialInformation.title = addConfidentialInformation" >> ../conf/messages.en
echo "addConfidentialInformation.heading = addConfidentialInformation" >> ../conf/messages.en
echo "addConfidentialInformation.checkYourAnswersLabel = addConfidentialInformation" >> ../conf/messages.en
echo "addConfidentialInformation.error.required = Select yes if addConfidentialInformation" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddConfidentialInformationUserAnswersEntry: Arbitrary[(AddConfidentialInformationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AddConfidentialInformationPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddConfidentialInformationPage: Arbitrary[AddConfidentialInformationPage.type] =";\
    print "    Arbitrary(AddConfidentialInformationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AddConfidentialInformationPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def addConfidentialInformation: Option[AnswerRow] = userAnswers.get(AddConfidentialInformationPage) map {";\
     print "    x => AnswerRow(\"addConfidentialInformation.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.AddConfidentialInformationController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AddConfidentialInformation completed"
