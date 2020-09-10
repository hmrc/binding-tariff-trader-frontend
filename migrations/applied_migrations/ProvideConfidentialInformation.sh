#!/bin/bash

echo "Applying migration ProvideConfidentialInformation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /provideConfidentialInformation                        controllers.ProvideConfidentialInformationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /provideConfidentialInformation                        controllers.ProvideConfidentialInformationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeProvideConfidentialInformation                  controllers.ProvideConfidentialInformationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeProvideConfidentialInformation                  controllers.ProvideConfidentialInformationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "provideConfidentialInformation.title = provideConfidentialInformation" >> ../conf/messages.en
echo "provideConfidentialInformation.heading = provideConfidentialInformation" >> ../conf/messages.en
echo "provideConfidentialInformation.checkYourAnswersLabel = provideConfidentialInformation" >> ../conf/messages.en
echo "provideConfidentialInformation.error.required = Enter provideConfidentialInformation" >> ../conf/messages.en
echo "provideConfidentialInformation.error.length = ProvideConfidentialInformation must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryProvideConfidentialInformationUserAnswersEntry: Arbitrary[(ProvideConfidentialInformationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ProvideConfidentialInformationPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryProvideConfidentialInformationPage: Arbitrary[ProvideConfidentialInformationPage.type] =";\
    print "    Arbitrary(ProvideConfidentialInformationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ProvideConfidentialInformationPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def provideConfidentialInformation: Option[AnswerRow] = userAnswers.get(ProvideConfidentialInformationPage) map {";\
     print "    x => AnswerRow(\"provideConfidentialInformation.checkYourAnswersLabel\", s\"$x\", false, routes.ProvideConfidentialInformationController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ProvideConfidentialInformation completed"
