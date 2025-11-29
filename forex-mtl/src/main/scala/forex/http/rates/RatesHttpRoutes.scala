package forex.http
package rates

import cats.effect.Sync
import cats.syntax.flatMap._
import forex.programs.RatesProgram
import forex.programs.rates.{Protocol => RatesProgramProtocol}
import forex.programs.rates.errors.{Error => ProgramError}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import forex.domain.Currency

class RatesHttpRoutes[F[_]: Sync](rates: RatesProgram[F]) extends Http4sDsl[F] {

  import Converters._, Protocol._

  private[http] val prefixPath = "/rates"

  private val supportedCurrencies = forex.domain.Currency.all.map(_.code)

  private def extractCurrencyParams(req: org.http4s.Request[F]): Either[String, (Currency, Currency)] = {
    val fromResult = req.params.get("from").flatMap(Currency.fromStringOption)
    val toResult = req.params.get("to").flatMap(Currency.fromStringOption)
    (fromResult, toResult) match {
      case (Some(from), Some(to)) if from != to => Right((from, to))
      case (Some(from), Some(to)) if from == to => Left("from and to currencies should be different")
      case _ =>
        val supported = supportedCurrencies.mkString(", ")
        Left(s"Requested currencies are not supported. Supported currencies are: $supported")
    }
  }

  private def handleProgramError(error: ProgramError): ErrorResponse = error match {
    case ProgramError.RateLookupFailed(msg) =>
      ErrorResponse("500", msg)
  }

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ GET -> Root =>
      extractCurrencyParams(req) match {
        case Right((from, to)) =>
          rates.get(RatesProgramProtocol.GetRatesRequest(from, to)).flatMap {
            case Right(rate) =>
              Ok(rate.asGetApiResponse)
            case Left(error) =>
              val errorResponse = handleProgramError(error)
              if (errorResponse.code == "400") {
                BadRequest(errorResponse)
              } else {
                InternalServerError(errorResponse)
              }
          }
        case Left(errorMsg) =>
          val errorJson = ErrorResponse("400", errorMsg)
          BadRequest(errorJson)
      }
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
