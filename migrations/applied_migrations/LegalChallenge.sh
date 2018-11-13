#!/bin/bash

echo "Applying migration LegalChallenge"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /legalChallenge               controllers.LegalChallengeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /legalChallenge               controllers.LegalChallengeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeLegalChallenge                  controllers.LegalChallengeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeLegalChallenge                  controllers.LegalChallengeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "legalChallenge.title = LegalChallenge" >> ../conf/messages.en
echo "legalChallenge.heading = LegalChallenge" >> ../conf/messages.en
echo "legalChallenge.yesLegalChallenge = Yes - I am aware of a legal challenge" >> ../conf/messages.en
echo "legalChallenge.noLegalChallenge = No - I am not aware of a legal challenge" >> ../conf/messages.en
echo "legalChallenge.checkYourAnswersLabel = LegalChallenge" >> ../conf/messages.en
echo "legalChallenge.error.required = Select legalChallenge" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLegalChallengeUserAnswersEntry: Arbitrary[(LegalChallengePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[LegalChallengePage.type]";\
    print "        value <- arbitrary[LegalChallenge].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLegalChallengePage: Arbitrary[LegalChallengePage.type] =";\
    print "    Arbitrary(LegalChallengePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLegalChallenge: Arbitrary[LegalChallenge] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(LegalChallenge.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(LegalChallengePage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def legalChallenge: Option[AnswerRow] = userAnswers.get(LegalChallengePage) map {";\
     print "    x => AnswerRow(\"legalChallenge.checkYourAnswersLabel\", s\"legalChallenge.$x\", true, routes.LegalChallengeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration LegalChallenge completed"
