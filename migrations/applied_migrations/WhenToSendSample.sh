#!/bin/bash

echo "Applying migration WhenToSendSample"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whenToSendSample               controllers.WhenToSendSampleController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whenToSendSample               controllers.WhenToSendSampleController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhenToSendSample                  controllers.WhenToSendSampleController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhenToSendSample                  controllers.WhenToSendSampleController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whenToSendSample.title = WhenToSendSample" >> ../conf/messages.en
echo "whenToSendSample.heading = WhenToSendSample" >> ../conf/messages.en
echo "whenToSendSample.yesProvideSample = Yes - I want to provide a sample" >> ../conf/messages.en
echo "whenToSendSample.notSendingSample = No - I will not be sending a sample" >> ../conf/messages.en
echo "whenToSendSample.checkYourAnswersLabel = WhenToSendSample" >> ../conf/messages.en
echo "whenToSendSample.error.required = Select whenToSendSample" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhenToSendSampleUserAnswersEntry: Arbitrary[(WhenToSendSamplePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhenToSendSamplePage.type]";\
    print "        value <- arbitrary[WhenToSendSample].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhenToSendSamplePage: Arbitrary[WhenToSendSamplePage.type] =";\
    print "    Arbitrary(WhenToSendSamplePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhenToSendSample: Arbitrary[WhenToSendSample] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhenToSendSample.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhenToSendSamplePage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whenToSendSample: Option[AnswerRow] = userAnswers.get(WhenToSendSamplePage) map {";\
     print "    x => AnswerRow(\"whenToSendSample.checkYourAnswersLabel\", s\"whenToSendSample.$x\", true, routes.WhenToSendSampleController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhenToSendSample completed"
