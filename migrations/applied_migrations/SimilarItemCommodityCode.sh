#!/bin/bash

echo "Applying migration SimilarItemCommodityCode"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /similarItemCommodityCode               controllers.SimilarItemCommodityCodeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /similarItemCommodityCode               controllers.SimilarItemCommodityCodeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSimilarItemCommodityCode                  controllers.SimilarItemCommodityCodeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSimilarItemCommodityCode                  controllers.SimilarItemCommodityCodeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "similarItemCommodityCode.title = SimilarItemCommodityCode" >> ../conf/messages.en
echo "similarItemCommodityCode.heading = SimilarItemCommodityCode" >> ../conf/messages.en
echo "similarItemCommodityCode.yesAwareSimilarCode = Yes - I am aware of a similar commodity code ruling" >> ../conf/messages.en
echo "similarItemCommodityCode.noNotAware = No - I am not aware of a similar commodity code ruling" >> ../conf/messages.en
echo "similarItemCommodityCode.checkYourAnswersLabel = SimilarItemCommodityCode" >> ../conf/messages.en
echo "similarItemCommodityCode.error.required = Select similarItemCommodityCode" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySimilarItemCommodityCodeUserAnswersEntry: Arbitrary[(SimilarItemCommodityCodePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SimilarItemCommodityCodePage.type]";\
    print "        value <- arbitrary[SimilarItemCommodityCode].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySimilarItemCommodityCodePage: Arbitrary[SimilarItemCommodityCodePage.type] =";\
    print "    Arbitrary(SimilarItemCommodityCodePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySimilarItemCommodityCode: Arbitrary[SimilarItemCommodityCode] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(SimilarItemCommodityCode.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SimilarItemCommodityCodePage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def similarItemCommodityCode: Option[AnswerRow] = userAnswers.get(SimilarItemCommodityCodePage) map {";\
     print "    x => AnswerRow(\"similarItemCommodityCode.checkYourAnswersLabel\", s\"similarItemCommodityCode.$x\", true, routes.SimilarItemCommodityCodeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SimilarItemCommodityCode completed"
