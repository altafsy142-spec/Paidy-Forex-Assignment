package forex.http
package rates

import forex.domain.Currency.show
import forex.domain.Rate.Pair
import forex.domain._
import io.circe._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredEncoder, deriveConfiguredDecoder}

object Protocol {

  implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames

  final case class GetApiRequest(
      from: Currency,
      to: Currency
  )

  final case class GetApiResponse(
      from: Currency,
      to: Currency,
      price: Price,
      timestamp: Timestamp
  )

  // External OneFrame API response model
  final case class OneFrameApiResponse(
      from: String,
      to: String,
      bid: BigDecimal,
      ask: BigDecimal,
      price: BigDecimal,
      time_stamp: String
  )

  final case class ErrorResponse(code: String="400", error: String="Internal Server Error") {
    def InvalidCurrencyPair(errorMsg: String): ErrorResponse =
      ErrorResponse("400", errorMsg)
  }

  implicit val currencyEncoder: Encoder[Currency] =
    Encoder.instance[Currency] { show.show _ andThen Json.fromString }

  implicit val pairEncoder: Encoder[Pair] =
    deriveConfiguredEncoder[Pair]

  implicit val rateEncoder: Encoder[Rate] =
    deriveConfiguredEncoder[Rate]

  implicit val responseEncoder: Encoder[GetApiResponse] =
    deriveConfiguredEncoder[GetApiResponse]

  implicit val errorResponseEncoder: Encoder[ErrorResponse] =
    deriveConfiguredEncoder[ErrorResponse]

  implicit val oneFrameApiResponseDecoder: Decoder[OneFrameApiResponse] =
    deriveConfiguredDecoder[OneFrameApiResponse]

}
