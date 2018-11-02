#!/bin/bash

echo "Applying migration UploadSupportingMaterialMultiple"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /uploadSupportingMaterialMultiple                        controllers.UploadSupportingMaterialMultipleController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /uploadSupportingMaterialMultiple                        controllers.UploadSupportingMaterialMultipleController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeUploadSupportingMaterialMultiple                  controllers.UploadSupportingMaterialMultipleController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeUploadSupportingMaterialMultiple                  controllers.UploadSupportingMaterialMultipleController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "uploadSupportingMaterialMultiple.title = uploadSupportingMaterialMultiple" >> ../conf/messages.en
echo "uploadSupportingMaterialMultiple.heading = uploadSupportingMaterialMultiple" >> ../conf/messages.en
echo "uploadSupportingMaterialMultiple.checkYourAnswersLabel = uploadSupportingMaterialMultiple" >> ../conf/messages.en
echo "uploadSupportingMaterialMultiple.error.required = Enter uploadSupportingMaterialMultiple" >> ../conf/messages.en
echo "uploadSupportingMaterialMultiple.error.length = UploadSupportingMaterialMultiple must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUploadSupportingMaterialMultipleUserAnswersEntry: Arbitrary[(UploadSupportingMaterialMultiplePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[UploadSupportingMaterialMultiplePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUploadSupportingMaterialMultiplePage: Arbitrary[UploadSupportingMaterialMultiplePage.type] =";\
    print "    Arbitrary(UploadSupportingMaterialMultiplePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(UploadSupportingMaterialMultiplePage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def uploadSupportingMaterialMultiple: Option[AnswerRow] = userAnswers.get(UploadSupportingMaterialMultiplePage) map {";\
     print "    x => AnswerRow(\"uploadSupportingMaterialMultiple.checkYourAnswersLabel\", s\"$x\", false, routes.UploadSupportingMaterialMultipleController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration UploadSupportingMaterialMultiple completed"
