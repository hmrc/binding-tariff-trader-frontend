#!/bin/bash

echo "Applying migration DescribeYourItem"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /describeYourItem                        controllers.DescribeYourItemController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /describeYourItem                        controllers.DescribeYourItemController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDescribeYourItem                  controllers.DescribeYourItemController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDescribeYourItem                  controllers.DescribeYourItemController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "describeYourItem.title = describeYourItem" >> ../conf/messages.en
echo "describeYourItem.heading = describeYourItem" >> ../conf/messages.en
echo "describeYourItem.field1 = Field 1" >> ../conf/messages.en
echo "describeYourItem.field2 = Field 2" >> ../conf/messages.en
echo "describeYourItem.checkYourAnswersLabel = describeYourItem" >> ../conf/messages.en
echo "describeYourItem.error.field1.required = Enter field1" >> ../conf/messages.en
echo "describeYourItem.error.field2.required = Enter field2" >> ../conf/messages.en
echo "describeYourItem.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "describeYourItem.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDescribeYourItemUserAnswersEntry: Arbitrary[(DescribeYourItemPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DescribeYourItemPage.type]";\
    print "        value <- arbitrary[DescribeYourItem].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDescribeYourItemPage: Arbitrary[DescribeYourItemPage.type] =";\
    print "    Arbitrary(DescribeYourItemPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDescribeYourItem: Arbitrary[DescribeYourItem] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield DescribeYourItem(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DescribeYourItemPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def describeYourItem: Option[AnswerRow] = userAnswers.get(DescribeYourItemPage) map {";\
     print "    x => AnswerRow(\"describeYourItem.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.DescribeYourItemController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration DescribeYourItem completed"