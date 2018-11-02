#!/bin/bash

echo "Applying migration ReturnSamples"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /returnSamples               controllers.ReturnSamplesController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /returnSamples               controllers.ReturnSamplesController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReturnSamples                  controllers.ReturnSamplesController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReturnSamples                  controllers.ReturnSamplesController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "returnSamples.title = ReturnSamples" >> ../conf/messages.en
echo "returnSamples.heading = ReturnSamples" >> ../conf/messages.en
echo "returnSamples.yesReturnSamples = Yes - I would like my sample returned" >> ../conf/messages.en
echo "returnSamples.noDontReturnSamples = No - I do not want my sample returned" >> ../conf/messages.en
echo "returnSamples.checkYourAnswersLabel = ReturnSamples" >> ../conf/messages.en
echo "returnSamples.error.required = Select returnSamples" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReturnSamplesUserAnswersEntry: Arbitrary[(ReturnSamplesPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReturnSamplesPage.type]";\
    print "        value <- arbitrary[ReturnSamples].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReturnSamplesPage: Arbitrary[ReturnSamplesPage.type] =";\
    print "    Arbitrary(ReturnSamplesPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReturnSamples: Arbitrary[ReturnSamples] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(ReturnSamples.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReturnSamplesPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def returnSamples: Option[AnswerRow] = userAnswers.get(ReturnSamplesPage) map {";\
     print "    x => AnswerRow(\"returnSamples.checkYourAnswersLabel\", s\"returnSamples.$x\", true, routes.ReturnSamplesController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReturnSamples completed"
