#!/bin/bash

echo "Applying migration InformationAboutYourItem"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /informationAboutYourItem               controllers.InformationAboutYourItemController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /informationAboutYourItem               controllers.InformationAboutYourItemController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeInformationAboutYourItem                  controllers.InformationAboutYourItemController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeInformationAboutYourItem                  controllers.InformationAboutYourItemController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "informationAboutYourItem.title = informationAboutYourItem" >> ../conf/messages.en
echo "informationAboutYourItem.heading = informationAboutYourItem" >> ../conf/messages.en
echo "informationAboutYourItem.yesIHaveInfo = Yes - I have some information about my item that I do not want published" >> ../conf/messages.en
echo "informationAboutYourItem.no = No - all information about my item can be published" >> ../conf/messages.en
echo "informationAboutYourItem.checkYourAnswersLabel = informationAboutYourItem" >> ../conf/messages.en
echo "informationAboutYourItem.error.required = Select informationAboutYourItem" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryInformationAboutYourItemUserAnswersEntry: Arbitrary[(InformationAboutYourItemPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[InformationAboutYourItemPage.type]";\
    print "        value <- arbitrary[InformationAboutYourItem].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryInformationAboutYourItemPage: Arbitrary[InformationAboutYourItemPage.type] =";\
    print "    Arbitrary(InformationAboutYourItemPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryInformationAboutYourItem: Arbitrary[InformationAboutYourItem] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(InformationAboutYourItem.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(InformationAboutYourItemPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def informationAboutYourItem: Option[AnswerRow] = userAnswers.get(InformationAboutYourItemPage) map {";\
     print "    x => AnswerRow(\"informationAboutYourItem.checkYourAnswersLabel\", s\"informationAboutYourItem.$x\", true, routes.InformationAboutYourItemController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration InformationAboutYourItem completed"
