#!/bin/bash

echo "Applying migration ConfidentialInformation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /confidentialInformation                        controllers.ConfidentialInformationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /confidentialInformation                        controllers.ConfidentialInformationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeConfidentialInformation                  controllers.ConfidentialInformationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeConfidentialInformation                  controllers.ConfidentialInformationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "confidentialInformation.title = confidentialInformation" >> ../conf/messages.en
echo "confidentialInformation.heading = confidentialInformation" >> ../conf/messages.en
echo "confidentialInformation.field1 = Field 1" >> ../conf/messages.en
echo "confidentialInformation.field2 = Field 2" >> ../conf/messages.en
echo "confidentialInformation.checkYourAnswersLabel = confidentialInformation" >> ../conf/messages.en
echo "confidentialInformation.error.field1.required = Enter field1" >> ../conf/messages.en
echo "confidentialInformation.error.field2.required = Enter field2" >> ../conf/messages.en
echo "confidentialInformation.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "confidentialInformation.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryConfidentialInformationUserAnswersEntry: Arbitrary[(ConfidentialInformationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ConfidentialInformationPage.type]";\
    print "        value <- arbitrary[ConfidentialInformation].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryConfidentialInformationPage: Arbitrary[ConfidentialInformationPage.type] =";\
    print "    Arbitrary(ConfidentialInformationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryConfidentialInformation: Arbitrary[ConfidentialInformation] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield ConfidentialInformation(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ConfidentialInformationPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def confidentialInformation: Option[AnswerRow] = userAnswers.get(ConfidentialInformationPage) map {";\
     print "    x => AnswerRow(\"confidentialInformation.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.ConfidentialInformationController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ConfidentialInformation completed"