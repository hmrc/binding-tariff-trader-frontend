#!/bin/bash

echo "Applying migration UploadWrittenAuthorisation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /uploadWrittenAuthorisation                        controllers.UploadWrittenAuthorisationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /uploadWrittenAuthorisation                        controllers.UploadWrittenAuthorisationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeUploadWrittenAuthorisation                  controllers.UploadWrittenAuthorisationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeUploadWrittenAuthorisation                  controllers.UploadWrittenAuthorisationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "uploadWrittenAuthorisation.title = uploadWrittenAuthorisation" >> ../conf/messages.en
echo "uploadWrittenAuthorisation.heading = uploadWrittenAuthorisation" >> ../conf/messages.en
echo "uploadWrittenAuthorisation.checkYourAnswersLabel = uploadWrittenAuthorisation" >> ../conf/messages.en
echo "uploadWrittenAuthorisation.error.required = Enter uploadWrittenAuthorisation" >> ../conf/messages.en
echo "uploadWrittenAuthorisation.error.length = UploadWrittenAuthorisation must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUploadWrittenAuthorisationUserAnswersEntry: Arbitrary[(UploadWrittenAuthorisationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[UploadWrittenAuthorisationPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUploadWrittenAuthorisationPage: Arbitrary[UploadWrittenAuthorisationPage.type] =";\
    print "    Arbitrary(UploadWrittenAuthorisationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(UploadWrittenAuthorisationPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def uploadWrittenAuthorisation: Option[AnswerRow] = userAnswers.get(UploadWrittenAuthorisationPage) map {";\
     print "    x => AnswerRow(\"uploadWrittenAuthorisation.checkYourAnswersLabel\", s\"$x\", false, routes.UploadWrittenAuthorisationController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration UploadWrittenAuthorisation completed"
