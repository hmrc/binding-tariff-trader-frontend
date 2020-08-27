#!/bin/bash

echo "Applying migration ProvideGoodsName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /provideGoodsName                        controllers.ProvideGoodsNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /provideGoodsName                        controllers.ProvideGoodsNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeprovideGoodsName                  controllers.ProvideGoodsNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeprovideGoodsName                  controllers.ProvideGoodsNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "provideGoodsName.title = provideGoodsName" >> ../conf/messages.en
echo "provideGoodsName.heading = provideGoodsName" >> ../conf/messages.en
echo "provideGoodsName.checkYourAnswersLabel = provideGoodsName" >> ../conf/messages.en
echo "provideGoodsName.error.required = Enter provideGoodsName" >> ../conf/messages.en
echo "provideGoodsName.error.length = provideGoodsName must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryProvideGoodsNameUserAnswersEntry: Arbitrary[(provideGoodsNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[provideGoodsNamePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryProvideGoodsNamePage: Arbitrary[provideGoodsNamePage.type] =";\
    print "    Arbitrary(provideGoodsNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(provideGoodsNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def provideGoodsName: Option[AnswerRow] = userAnswers.get(provideGoodsNamePage) map {";\
     print "    x => AnswerRow(\"provideGoodsName.checkYourAnswersLabel\", s\"$x\", false, routes.provideGoodsNameController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration provideGoodsName completed"
