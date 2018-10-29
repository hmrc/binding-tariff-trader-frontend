#!/bin/bash

echo "Applying migration EnterContactDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /enterContactDetails                        controllers.EnterContactDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /enterContactDetails                        controllers.EnterContactDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeEnterContactDetails                  controllers.EnterContactDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeEnterContactDetails                  controllers.EnterContactDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "enterContactDetails.title = enterContactDetails" >> ../conf/messages.en
echo "enterContactDetails.heading = enterContactDetails" >> ../conf/messages.en
echo "enterContactDetails.field1 = Field 1" >> ../conf/messages.en
echo "enterContactDetails.field2 = Field 2" >> ../conf/messages.en
echo "enterContactDetails.checkYourAnswersLabel = enterContactDetails" >> ../conf/messages.en
echo "enterContactDetails.error.field1.required = Enter field1" >> ../conf/messages.en
echo "enterContactDetails.error.field2.required = Enter field2" >> ../conf/messages.en
echo "enterContactDetails.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "enterContactDetails.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryEnterContactDetailsUserAnswersEntry: Arbitrary[(EnterContactDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[EnterContactDetailsPage.type]";\
    print "        value <- arbitrary[EnterContactDetails].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryEnterContactDetailsPage: Arbitrary[EnterContactDetailsPage.type] =";\
    print "    Arbitrary(EnterContactDetailsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryEnterContactDetails: Arbitrary[EnterContactDetails] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield EnterContactDetails(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(EnterContactDetailsPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def enterContactDetails: Option[AnswerRow] = userAnswers.get(EnterContactDetailsPage) map {";\
     print "    x => AnswerRow(\"enterContactDetails.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.EnterContactDetailsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration EnterContactDetails completed"