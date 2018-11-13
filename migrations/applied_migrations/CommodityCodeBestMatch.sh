#!/bin/bash

echo "Applying migration CommodityCodeBestMatch"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /commodityCodeBestMatch               controllers.CommodityCodeBestMatchController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /commodityCodeBestMatch               controllers.CommodityCodeBestMatchController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCommodityCodeBestMatch                  controllers.CommodityCodeBestMatchController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCommodityCodeBestMatch                  controllers.CommodityCodeBestMatchController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "commodityCodeBestMatch.title = CommodityCodeBestMatch" >> ../conf/messages.en
echo "commodityCodeBestMatch.heading = CommodityCodeBestMatch" >> ../conf/messages.en
echo "commodityCodeBestMatch.yesFoundCommodityCode = NoHaventFoundCommodityCode" >> ../conf/messages.en
echo "commodityCodeBestMatch.noHaventFoundCommodityCode = No - I have not found a commodity code" >> ../conf/messages.en
echo "commodityCodeBestMatch.checkYourAnswersLabel = CommodityCodeBestMatch" >> ../conf/messages.en
echo "commodityCodeBestMatch.error.required = Select commodityCodeBestMatch" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCommodityCodeBestMatchUserAnswersEntry: Arbitrary[(CommodityCodeBestMatchPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[CommodityCodeBestMatchPage.type]";\
    print "        value <- arbitrary[CommodityCodeBestMatch].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCommodityCodeBestMatchPage: Arbitrary[CommodityCodeBestMatchPage.type] =";\
    print "    Arbitrary(CommodityCodeBestMatchPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCommodityCodeBestMatch: Arbitrary[CommodityCodeBestMatch] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(CommodityCodeBestMatch.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(CommodityCodeBestMatchPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def commodityCodeBestMatch: Option[AnswerRow] = userAnswers.get(CommodityCodeBestMatchPage) map {";\
     print "    x => AnswerRow(\"commodityCodeBestMatch.checkYourAnswersLabel\", s\"commodityCodeBestMatch.$x\", true, routes.CommodityCodeBestMatchController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration CommodityCodeBestMatch completed"
