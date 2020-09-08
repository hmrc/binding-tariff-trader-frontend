#!/bin/bash

echo "Applying migration ProvideGoodsDescription"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /provideGoodsDescription                        controllers.ProvideGoodsDescriptionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /provideGoodsDescription                        controllers.ProvideGoodsDescriptionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeProvideGoodsDescription                  controllers.ProvideGoodsDescriptionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeProvideGoodsDescription                  controllers.ProvideGoodsDescriptionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "provideGoodsDescription.title = provideGoodsDescription" >> ../conf/messages.en
echo "provideGoodsDescription.heading = provideGoodsDescription" >> ../conf/messages.en
echo "provideGoodsDescription.caption = provideGoodsDescription" >> ../conf/messages.en
echo "provideGoodsDescription.checkYourAnswersLabel = provideGoodsDescription" >> ../conf/messages.en
echo "provideGoodsDescription.error.required = Enter provideGoodsDescription" >> ../conf/messages.en
echo "provideGoodsDescription.error.length = ProvideGoodsDescription must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryProvideGoodsDescriptionUserAnswersEntry: Arbitrary[(ProvideGoodsDescriptionPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ProvideGoodsDescriptionPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryProvideGoodsDescriptionPage: Arbitrary[ProvideGoodsDescriptionPage.type] =";\
    print "    Arbitrary(ProvideGoodsDescriptionPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ProvideGoodsDescriptionPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def provideGoodsDescription: Option[AnswerRow] = userAnswers.get(ProvideGoodsDescriptionPage) map {";\
     print "    x => AnswerRow(\"provideGoodsDescription.checkYourAnswersLabel\", s\"$x\", false, routes.ProvideGoodsDescriptionController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ProvideGoodsDescription completed"
