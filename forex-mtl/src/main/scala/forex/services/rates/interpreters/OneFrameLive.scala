package forex.services.rates.interpreters

import cats.effect.Sync
import cats.syntax.either._
import cats.syntax.flatMap._
import forex.http.rates.Protocol.OneFrameApiResponse
import forex.services.rates.Algebra
import forex.services.rates.errors._
import io.circe.parser.decode
import org.http4s.Header.Raw
import org.http4s.{ Method, Request, Uri }
import org.http4s.client.Client
import org.slf4j.LoggerFactory
import org.typelevel.ci.CIString

class OneFrameLive[F[_]: Sync](
    client: Client[F],
    apiUrl: String,
    token: String
) extends Algebra[F] {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def getOpenFrameExchange(pair: List[String]): F[Either[Error, List[OneFrameApiResponse]]] = {
    val pairString = pair.mkString("&pair=")
    val uri = Uri.unsafeFromString(s"$apiUrl?pair=$pairString")

    // Create request with token as header
    val request = Request[F](
      method = Method.GET,
      uri = uri
    ).withHeaders(Raw(CIString("token"), token))

    Sync[F].handleErrorWith(
      client.expect[String](request).flatMap { body =>
        Sync[F].delay {
          decode[List[OneFrameApiResponse]](body)
            .leftMap[Error](err => {
              logger.error(s"Failed to parse OneFrame response for pairs $pairString: ${err.getMessage}")
              Error.ApiResponseError(s"Failed to parse response: ${err.getMessage}")
            })
            .flatMap { responses =>
              if (responses.nonEmpty) {
                logger.info(s"Successfully fetched ${responses.length} exchange rates for pairs $pairString")
                Right(responses)
              } else {
                logger.warn(s"Empty response array from OneFrame for pairs $pairString")
                Left(Error.ApiResponseError("Empty response array from API"): Error)
              }
            }
        }
      }
    ) { throwable =>
      logger.error(s"Connection error while fetching OneFrame rates for pairs $pairString: ${throwable.getMessage}", throwable)
      Sync[F].pure(
        (Error.ApiConnectionError(s"Failed to connect to OneFrame API: ${throwable.getMessage}"): Error).asLeft[List[OneFrameApiResponse]]
      )

    }
  }
}

