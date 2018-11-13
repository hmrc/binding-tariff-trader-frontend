#!/bin/bash

echo "Applying migration CommodityCodeRulingReference"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /commodityCodeRulingReference                        controllers.CommodityCodeRulingReferenceController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /commodityCodeRulingReference                        controllers.CommodityCodeRulingReferenceController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCommodityCodeRulingReference                  controllers.CommodityCodeRulingReferenceController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCommodityCodeRulingReference                  controllers.CommodityCodeRulingReferenceController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "commodityCodeRulingReference.title = commodityCodeRulingReference" >> ../conf/messages.en
echo "commodityCodeRulingReference.heading = commodityCodeRulingReference" >> ../conf/messages.en
echo "commodityCodeRulingReference.checkYourAnswersLabel = commodityCodeRulingReference" >> ../conf/messages.en
echo "commodityCodeRulingReference.error.required = Enter commodityCodeRulingReference" >> ../conf/messages.en
echo "commodityCodeRulingReference.error.length = CommodityCodeRulingReference must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCommodityCodeRulingReferenceUserAnswersEntry: Arbitrary[(CommodityCodeRulingReferencePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[CommodityCodeRulingReferencePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCommodityCodeRulingReferencePage: Arbitrary[CommodityCodeRulingReferencePage.type] =";\
    print "    Arbitrary(CommodityCodeRulingReferencePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(CommodityCodeRulingReferencePage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def commodityCodeRulingReference: Option[AnswerRow] = userAnswers.get(CommodityCodeRulingReferencePage) map {";\
     print "    x => AnswerRow(\"commodityCodeRulingReference.checkYourAnswersLabel\", s\"$x\", false, routes.CommodityCodeRulingReferenceController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration CommodityCodeRulingReference completed"
