#!/bin/bash

echo "Applying migration LegalChallengeDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /legalChallengeDetails                        controllers.LegalChallengeDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /legalChallengeDetails                        controllers.LegalChallengeDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeLegalChallengeDetails                  controllers.LegalChallengeDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeLegalChallengeDetails                  controllers.LegalChallengeDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "legalChallengeDetails.title = legalChallengeDetails" >> ../conf/messages.en
echo "legalChallengeDetails.heading = legalChallengeDetails" >> ../conf/messages.en
echo "legalChallengeDetails.checkYourAnswersLabel = legalChallengeDetails" >> ../conf/messages.en
echo "legalChallengeDetails.error.required = Enter legalChallengeDetails" >> ../conf/messages.en
echo "legalChallengeDetails.error.length = LegalChallengeDetails must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLegalChallengeDetailsUserAnswersEntry: Arbitrary[(LegalChallengeDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[LegalChallengeDetailsPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLegalChallengeDetailsPage: Arbitrary[LegalChallengeDetailsPage.type] =";\
    print "    Arbitrary(LegalChallengeDetailsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(LegalChallengeDetailsPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def legalChallengeDetails: Option[AnswerRow] = userAnswers.get(LegalChallengeDetailsPage) map {";\
     print "    x => AnswerRow(\"legalChallengeDetails.checkYourAnswersLabel\", s\"$x\", false, routes.LegalChallengeDetailsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration LegalChallengeDetails completed"
