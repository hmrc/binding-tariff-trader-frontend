#!/bin/bash

echo "Applying migration WhichBestDescribesYou"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichBestDescribesYou               controllers.WhichBestDescribesYouController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichBestDescribesYou               controllers.WhichBestDescribesYouController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichBestDescribesYou                  controllers.WhichBestDescribesYouController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichBestDescribesYou                  controllers.WhichBestDescribesYouController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichBestDescribesYou.title = whichOfTheseBestDescribesYou" >> ../conf/messages.en
echo "whichBestDescribesYou.heading = whichOfTheseBestDescribesYou" >> ../conf/messages.en
echo "whichBestDescribesYou.option1 = I am an employee or owner of the business or organisation" >> ../conf/messages.en
echo "whichBestDescribesYou.option2 = I am a representative applying for a different business or organisation" >> ../conf/messages.en
echo "whichBestDescribesYou.checkYourAnswersLabel = whichOfTheseBestDescribesYou" >> ../conf/messages.en
echo "whichBestDescribesYou.error.required = Select whichBestDescribesYou" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichBestDescribesYouUserAnswersEntry: Arbitrary[(WhichBestDescribesYouPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhichBestDescribesYouPage.type]";\
    print "        value <- arbitrary[WhichBestDescribesYou].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichBestDescribesYouPage: Arbitrary[WhichBestDescribesYouPage.type] =";\
    print "    Arbitrary(WhichBestDescribesYouPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichBestDescribesYou: Arbitrary[WhichBestDescribesYou] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhichBestDescribesYou.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhichBestDescribesYouPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whichBestDescribesYou: Option[AnswerRow] = userAnswers.get(WhichBestDescribesYouPage) map {";\
     print "    x => AnswerRow(\"whichBestDescribesYou.checkYourAnswersLabel\", s\"whichBestDescribesYou.$x\", true, routes.WhichBestDescribesYouController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhichBestDescribesYou completed"
