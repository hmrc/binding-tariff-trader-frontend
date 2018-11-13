#!/bin/bash

echo "Applying migration CommodityCodeDigits"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /commodityCodeDigits                        controllers.CommodityCodeDigitsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /commodityCodeDigits                        controllers.CommodityCodeDigitsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCommodityCodeDigits                  controllers.CommodityCodeDigitsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCommodityCodeDigits                  controllers.CommodityCodeDigitsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "commodityCodeDigits.title = commodityCodeDigits" >> ../conf/messages.en
echo "commodityCodeDigits.heading = commodityCodeDigits" >> ../conf/messages.en
echo "commodityCodeDigits.checkYourAnswersLabel = commodityCodeDigits" >> ../conf/messages.en
echo "commodityCodeDigits.error.required = Enter commodityCodeDigits" >> ../conf/messages.en
echo "commodityCodeDigits.error.length = CommodityCodeDigits must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCommodityCodeDigitsUserAnswersEntry: Arbitrary[(CommodityCodeDigitsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[CommodityCodeDigitsPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCommodityCodeDigitsPage: Arbitrary[CommodityCodeDigitsPage.type] =";\
    print "    Arbitrary(CommodityCodeDigitsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(CommodityCodeDigitsPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def commodityCodeDigits: Option[AnswerRow] = userAnswers.get(CommodityCodeDigitsPage) map {";\
     print "    x => AnswerRow(\"commodityCodeDigits.checkYourAnswersLabel\", s\"$x\", false, routes.CommodityCodeDigitsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration CommodityCodeDigits completed"
