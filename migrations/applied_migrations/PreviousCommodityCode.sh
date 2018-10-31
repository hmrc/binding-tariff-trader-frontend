#!/bin/bash

echo "Applying migration PreviousCommodityCode"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /previousCommodityCode                        controllers.PreviousCommodityCodeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /previousCommodityCode                        controllers.PreviousCommodityCodeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePreviousCommodityCode                  controllers.PreviousCommodityCodeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePreviousCommodityCode                  controllers.PreviousCommodityCodeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "previousCommodityCode.title = previousCommodityCode" >> ../conf/messages.en
echo "previousCommodityCode.heading = previousCommodityCode" >> ../conf/messages.en
echo "previousCommodityCode.field1 = Field 1" >> ../conf/messages.en
echo "previousCommodityCode.field2 = Field 2" >> ../conf/messages.en
echo "previousCommodityCode.checkYourAnswersLabel = previousCommodityCode" >> ../conf/messages.en
echo "previousCommodityCode.error.field1.required = Enter field1" >> ../conf/messages.en
echo "previousCommodityCode.error.field2.required = Enter field2" >> ../conf/messages.en
echo "previousCommodityCode.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "previousCommodityCode.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPreviousCommodityCodeUserAnswersEntry: Arbitrary[(PreviousCommodityCodePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PreviousCommodityCodePage.type]";\
    print "        value <- arbitrary[PreviousCommodityCode].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPreviousCommodityCodePage: Arbitrary[PreviousCommodityCodePage.type] =";\
    print "    Arbitrary(PreviousCommodityCodePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPreviousCommodityCode: Arbitrary[PreviousCommodityCode] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield PreviousCommodityCode(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PreviousCommodityCodePage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def previousCommodityCode: Option[AnswerRow] = userAnswers.get(PreviousCommodityCodePage) map {";\
     print "    x => AnswerRow(\"previousCommodityCode.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.PreviousCommodityCodeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration PreviousCommodityCode completed"