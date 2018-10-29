#!/bin/bash

echo "Applying migration SelectApplicationType"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /selectApplicationType               controllers.SelectApplicationTypeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /selectApplicationType               controllers.SelectApplicationTypeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSelectApplicationType                  controllers.SelectApplicationTypeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSelectApplicationType                  controllers.SelectApplicationTypeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "selectApplicationType.title = SelectApplicationType" >> ../conf/messages.en
echo "selectApplicationType.heading = SelectApplicationType" >> ../conf/messages.en
echo "selectApplicationType.newCommodity = New commodity code ruling" >> ../conf/messages.en
echo "selectApplicationType.previousCommodity = Renew a previous commodity code ruling" >> ../conf/messages.en
echo "selectApplicationType.checkYourAnswersLabel = SelectApplicationType" >> ../conf/messages.en
echo "selectApplicationType.error.required = Select selectApplicationType" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySelectApplicationTypeUserAnswersEntry: Arbitrary[(SelectApplicationTypePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SelectApplicationTypePage.type]";\
    print "        value <- arbitrary[SelectApplicationType].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySelectApplicationTypePage: Arbitrary[SelectApplicationTypePage.type] =";\
    print "    Arbitrary(SelectApplicationTypePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySelectApplicationType: Arbitrary[SelectApplicationType] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(SelectApplicationType.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SelectApplicationTypePage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def selectApplicationType: Option[AnswerRow] = userAnswers.get(SelectApplicationTypePage) map {";\
     print "    x => AnswerRow(\"selectApplicationType.checkYourAnswersLabel\", s\"selectApplicationType.$x\", true, routes.SelectApplicationTypeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SelectApplicationType completed"
