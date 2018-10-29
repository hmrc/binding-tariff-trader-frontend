#!/bin/bash

echo "Applying migration RegisterBusinessRepresenting"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /registerBusinessRepresenting                        controllers.RegisterBusinessRepresentingController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /registerBusinessRepresenting                        controllers.RegisterBusinessRepresentingController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeRegisterBusinessRepresenting                  controllers.RegisterBusinessRepresentingController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeRegisterBusinessRepresenting                  controllers.RegisterBusinessRepresentingController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "registerBusinessRepresenting.title = registerBusinessRepresenting" >> ../conf/messages.en
echo "registerBusinessRepresenting.heading = registerBusinessRepresenting" >> ../conf/messages.en
echo "registerBusinessRepresenting.field1 = Field 1" >> ../conf/messages.en
echo "registerBusinessRepresenting.field2 = Field 2" >> ../conf/messages.en
echo "registerBusinessRepresenting.checkYourAnswersLabel = registerBusinessRepresenting" >> ../conf/messages.en
echo "registerBusinessRepresenting.error.field1.required = Enter field1" >> ../conf/messages.en
echo "registerBusinessRepresenting.error.field2.required = Enter field2" >> ../conf/messages.en
echo "registerBusinessRepresenting.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "registerBusinessRepresenting.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRegisterBusinessRepresentingUserAnswersEntry: Arbitrary[(RegisterBusinessRepresentingPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[RegisterBusinessRepresentingPage.type]";\
    print "        value <- arbitrary[RegisterBusinessRepresenting].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRegisterBusinessRepresentingPage: Arbitrary[RegisterBusinessRepresentingPage.type] =";\
    print "    Arbitrary(RegisterBusinessRepresentingPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRegisterBusinessRepresenting: Arbitrary[RegisterBusinessRepresenting] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield RegisterBusinessRepresenting(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(RegisterBusinessRepresentingPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def registerBusinessRepresenting: Option[AnswerRow] = userAnswers.get(RegisterBusinessRepresentingPage) map {";\
     print "    x => AnswerRow(\"registerBusinessRepresenting.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.RegisterBusinessRepresentingController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration RegisterBusinessRepresenting completed"