#!/bin/bash

echo "Applying migration Declaration"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /declaration                        controllers.DeclarationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /declaration                        controllers.DeclarationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDeclaration                  controllers.DeclarationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDeclaration                  controllers.DeclarationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "declaration.title = declaration" >> ../conf/messages.en
echo "declaration.heading = declaration" >> ../conf/messages.en
echo "declaration.checkYourAnswersLabel = declaration" >> ../conf/messages.en
echo "declaration.error.required = Enter declaration" >> ../conf/messages.en
echo "declaration.error.length = Declaration must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDeclarationUserAnswersEntry: Arbitrary[(DeclarationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DeclarationPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDeclarationPage: Arbitrary[DeclarationPage.type] =";\
    print "    Arbitrary(DeclarationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DeclarationPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def declaration: Option[AnswerRow] = userAnswers.get(DeclarationPage) map {";\
     print "    x => AnswerRow(\"declaration.checkYourAnswersLabel\", s\"$x\", false, routes.DeclarationController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration Declaration completed"
