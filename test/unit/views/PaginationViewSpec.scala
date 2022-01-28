/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package views

import models.Paged
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.BeforeAndAfterEach
import play.api.mvc.Call
import play.twirl.api.Html
import views.ViewMatchers._
import views.html.components.pagination

class PaginationViewSpec extends ViewSpecBase with BeforeAndAfterEach {

  private val goToPage: Int => Call = mock[Int => Call]

  protected def view(html: Html): Document = {
    Jsoup.parse(html.toString())
  }

  override def beforeEach(): Unit = {
    def returnThePage: Answer[Call] = {
      new Answer[Call] {
        override def answer(invocation: InvocationOnMock): Call = Call(method = "GET", url = "/page=" + invocation.getArgument(0))
      }
    }

    super.beforeEach()
    given(goToPage.apply(ArgumentMatchers.any[Int])) will returnThePage
  }

  "Pagination" should {

    "Render empty page" in {
      // When
      val doc = view(pagination(
        id = "ID",
        pager = Paged(Seq.empty[String], pageIndex = 1, pageSize = 1, resultCount = 0),
        onChange = goToPage
      )(messages))

      // Then
      doc should containElementWithID("ID-none")
      doc shouldNot containElementWithID("ID-some")
    }

    "Render 1 page" in {
      // When
      val doc = view(pagination(
        id = "ID",
        pager = Paged(Seq("", ""), pageIndex = 1, pageSize = 2, resultCount = 2),
        onChange = goToPage
      )(messages))

      // Then
      doc should containElementWithID("ID-one")
      doc shouldNot containElementWithClass("hmrc-pagination")

      val paragraphElement: Element = doc.getElementById("ID-one-result")
      val expectedText = s"${messages("pagination.showing")} 2 ${messages("pagination.results")}"
      assertElementHasText(paragraphElement, expectedText)
    }

    "Render 1 partially full page" in {
      // When
      val doc = view(pagination(
        id = "ID",
        pager = Paged(Seq(""), pageIndex = 1, pageSize = 2, resultCount = 1),
        onChange = goToPage
      )(messages))

      // Then
      doc should containElementWithID("ID-one")
      doc shouldNot containElementWithClass("hmrc-pagination")

      val paragraphElement: Element = doc.getElementById("ID-one-result")
      val expectedText = s"${messages("pagination.showing")} 1 ${messages("pagination.result")}"
      assertElementHasText(paragraphElement, expectedText)
    }

    "Render 2 pages" in {
      // When
      val doc = view(pagination(
        id = "ID",
        pager = Paged(Seq(""), pageIndex = 1, pageSize = 1, resultCount = 2),
        onChange = goToPage
      )(messages))

      // Then
      doc should containElementWithID("pagination-label")
      val paginationList = doc.getElementById("pagination-list")
      val allLinkElements = paginationList.getElementsByTag("li")
      assert(allLinkElements.size() == 3)
      assertElementHasText(allLinkElements.first(), "1")
      assertElementHasText(allLinkElements.get(1), "2")
      assertElementHasText(allLinkElements.get(2), messages("pagination.next"))

      doc shouldNot containElementWithID("ID-page_back")
      doc should containElementWithID("ID-page_next")
      doc.getElementById("ID-page_next") should haveAttribute("href", "/page=2")

      doc should containElementWithID("ID-page_2")
      doc.getElementById("ID-page_2") should haveAttribute("href", "/page=2")

      val expectedText = s"${messages("pagination.showing")} 1 ${messages("pagination.to")} 1 " +
        s"${messages("pagination.of")} 2 ${messages("pagination.results")}"
      val paragraphElement: Element = doc.getElementById("ID-some-result")
      assertElementHasText(paragraphElement, expectedText)

    }

    "Render 3 pages" in {
       //When
        val doc = view(pagination(
          id = "ID",
          pager = Paged(Seq(""), pageIndex = 1, pageSize = 1, resultCount = 3),
          onChange = goToPage
        )(messages))

       //Then

      doc should containElementWithID("pagination-label")
      val paginationList = doc.getElementById("pagination-list")
      val allLinkElements = paginationList.getElementsByTag("li")
      assert(allLinkElements.size() == 4)
      assertElementHasText(allLinkElements.first(), "1")
      assertElementHasText(allLinkElements.get(1), "2")
      assertElementHasText(allLinkElements.get(2), "3")
      assertElementHasText(allLinkElements.get(3), messages("pagination.next"))

      doc shouldNot containElementWithID("ID-page_back")
      doc should containElementWithID("ID-page_next")
      doc.getElementById("ID-page_next") should haveAttribute("href", "/page=2")

      doc should containElementWithID("ID-page_2")
      doc.getElementById("ID-page_2") should haveAttribute("href", "/page=2")
      doc should containElementWithID("ID-page_3")
      doc.getElementById("ID-page_3") should haveAttribute("href", "/page=3")

      val expectedText = s"${messages("pagination.showing")} 1 ${messages("pagination.to")} 1 " +
        s"${messages("pagination.of")} 3 ${messages("pagination.results")}"
      val paragraphElement: Element = doc.getElementById("ID-some-result")
      assertElementHasText(paragraphElement, expectedText)

    }

    "Render 4 pages" in {
       // When
      val doc = view(pagination(
        id = "ID",
        pager = Paged(Seq(""), pageIndex = 1, pageSize = 1, resultCount = 4),
        onChange = goToPage
      )(messages))

      // Then
      doc should containElementWithID("pagination-label")
      val paginationList = doc.getElementById("pagination-list")
      val allLinkElements = paginationList.getElementsByTag("li")
      assert(allLinkElements.size() == 5)
      assertElementHasText(allLinkElements.first(), "1")
      assertElementHasText(allLinkElements.get(1), "2")
      assertElementHasText(allLinkElements.get(2), "3")
      assertElementHasText(allLinkElements.get(3), "4")
      assertElementHasText(allLinkElements.get(4), messages("pagination.next"))

      doc shouldNot containElementWithID("ID-page_back")
      doc should containElementWithID("ID-page_next")
      doc.getElementById("ID-page_next") should haveAttribute("href", "/page=2")

      doc should containElementWithID("ID-page_2")
      doc.getElementById("ID-page_2") should haveAttribute("href", "/page=2")
      doc should containElementWithID("ID-page_3")
      doc.getElementById("ID-page_3") should haveAttribute("href", "/page=3")
      doc should containElementWithID("ID-page_4")
      doc.getElementById("ID-page_4") should haveAttribute("href", "/page=4")

      val expectedText = s"${messages("pagination.showing")} 1 ${messages("pagination.to")} 1 " +
        s"${messages("pagination.of")} 4 ${messages("pagination.results")}"
      val paragraphElement: Element = doc.getElementById("ID-some-result")
      assertElementHasText(paragraphElement, expectedText)
    }

    "Render more pages" in {
      // When
      val doc = view(pagination(
        id = "ID",
        pager = Paged(Seq(""), pageIndex = 1, pageSize = 1, resultCount = 100),
        onChange = goToPage
      )(messages))

      doc should containElementWithID("pagination-label")
      val paginationList = doc.getElementById("pagination-list")
      val allLinkElements = paginationList.getElementsByTag("li")
      assert(allLinkElements.size() == 6)
      assertElementHasText(allLinkElements.first(), "1")
      assertElementHasText(allLinkElements.get(1), "2")
      assertElementHasText(allLinkElements.get(2), "3")
      assertElementHasText(allLinkElements.get(3), "4")
      assertElementHasText(allLinkElements.get(4), "5")
      assertElementHasText(allLinkElements.get(5), messages("pagination.next"))

      doc shouldNot containElementWithID("ID-page_back")
      doc should containElementWithID("ID-page_next")
      doc.getElementById("ID-page_next") should haveAttribute("href", "/page=2")

      doc should containElementWithID("ID-page_2")
      doc.getElementById("ID-page_2") should haveAttribute("href", "/page=2")
      doc should containElementWithID("ID-page_3")
      doc.getElementById("ID-page_3") should haveAttribute("href", "/page=3")
      doc should containElementWithID("ID-page_4")
      doc.getElementById("ID-page_4") should haveAttribute("href", "/page=4")
      doc should containElementWithID("ID-page_5")
      doc.getElementById("ID-page_5") should haveAttribute("href", "/page=5")

      doc shouldNot containElementWithID("ID-page_6")
    }

    "Render 1 previous page" in {
      // When
      val doc = view(pagination(
        id = "ID",
        pager = Paged(Seq(""), pageIndex = 2, pageSize = 1, resultCount = 2),
        onChange = goToPage
      )(messages))

      // Then

      doc should containElementWithID("pagination-label")
      val paginationList = doc.getElementById("pagination-list")
      val allLinkElements = paginationList.getElementsByTag("li")
      assert(allLinkElements.size() == 3)
      assertElementHasText(allLinkElements.first(), messages("pagination.previous"))
      assertElementHasText(allLinkElements.get(1), "1")
      assertElementHasText(allLinkElements.get(2), "2")

      doc should containElementWithID("ID-page_back")
      doc.getElementById("ID-page_back") should haveAttribute("href", "/page=1")
      doc shouldNot containElementWithID("ID-page_next")

      doc should containElementWithID("ID-page_1")
      doc shouldNot containElementWithID("ID-page_2")
    }

  }

}