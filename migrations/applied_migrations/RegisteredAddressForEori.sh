#!/bin/bash

echo "Applying migration RegisteredAddressForEori"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /registeredAddressForEori                        controllers.RegisteredAddressForEoriController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /registeredAddressForEori                        controllers.RegisteredAddressForEoriController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeRegisteredAddressForEori                  controllers.RegisteredAddressForEoriController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeRegisteredAddressForEori                  controllers.RegisteredAddressForEoriController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "registeredAddressForEori.title = registeredAddressForEori" >> ../conf/messages.en
echo "registeredAddressForEori.heading = registeredAddressForEori" >> ../conf/messages.en
echo "registeredAddressForEori.field1 = Field 1" >> ../conf/messages.en
echo "registeredAddressForEori.field2 = Field 2" >> ../conf/messages.en
echo "registeredAddressForEori.checkYourAnswersLabel = registeredAddressForEori" >> ../conf/messages.en
echo "registeredAddressForEori.error.field1.required = Enter field1" >> ../conf/messages.en
echo "registeredAddressForEori.error.field2.required = Enter field2" >> ../conf/messages.en
echo "registeredAddressForEori.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "registeredAddressForEori.error.field2.length = field2 must be 75 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRegisteredAddressForEoriUserAnswersEntry: Arbitrary[(RegisteredAddressForEoriPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[RegisteredAddressForEoriPage.type]";\
    print "        value <- arbitrary[RegisteredAddressForEori].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRegisteredAddressForEoriPage: Arbitrary[RegisteredAddressForEoriPage.type] =";\
    print "    Arbitrary(RegisteredAddressForEoriPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRegisteredAddressForEori: Arbitrary[RegisteredAddressForEori] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield RegisteredAddressForEori(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(RegisteredAddressForEoriPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def registeredAddressForEori: Option[AnswerRow] = userAnswers.get(RegisteredAddressForEoriPage) map {";\
     print "    x => AnswerRow(\"registeredAddressForEori.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.RegisteredAddressForEoriController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration RegisteredAddressForEori completed"