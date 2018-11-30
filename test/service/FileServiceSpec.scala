package service

import connectors.BindingTariffFilestoreConnector
import models.response.UploadFileResponse
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class FileServiceSpec extends UnitSpec with MockitoSugar {

  private val connector = mock[BindingTariffFilestoreConnector]
  private val service = new FileService(connector)
  private implicit val headers: HeaderCarrier = HeaderCarrier()

  "Service" should {
    val fileUploading = mock[MultipartFormData.FilePart[TemporaryFile]]
    val fileUploaded = mock[UploadFileResponse]

    "Delegate to connector" in {
      given(connector.upload(refEq(fileUploading))(any[HeaderCarrier])).willReturn(Future.successful(fileUploaded))

      await(service.upload(fileUploading)) shouldBe fileUploaded
    }
  }

}
