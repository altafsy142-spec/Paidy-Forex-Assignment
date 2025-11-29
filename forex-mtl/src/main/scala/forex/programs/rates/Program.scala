package forex.programs.rates

import cats.effect.Concurrent
import cats.effect.implicits.toConcurrentOps
import cats.implicits._
import forex.domain._
import forex.http.rates.Protocol.OneFrameApiResponse
import forex.programs.rates.errors._
import forex.services.RatesService
import org.slf4j.LoggerFactory

class Program[F[_]: Concurrent](
    ratesService: RatesService[F],
    cacheManager: CacheManager[F]
) extends Algebra[F] {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def get(request: Protocol.GetRatesRequest): F[Error Either Rate] = {
    val requestPair = s"${request.from.code}${request.to.code}"
    val requestPairCombination = Set(requestPair, s"${request.to.code}${request.from.code}")

    // First, try to get from cache
    cacheManager.getCachedRate(requestPair) match {
      case Some(cachedRate) =>
        (Right(cachedRate): Either[Error, Rate]).pure[F]

      case None =>
        logger.info(s"Cache miss for pair: $requestPair, fetching from API")
        val pairString = cacheManager.buildPairString(requestPairCombination)
        ratesService.getOpenFrameExchange(pairString).flatMap {
          case Left(serviceError) =>
            (Left(toProgramError(serviceError)): Either[Error, Rate]).pure[F]

          case Right(responses) =>
            logger.info(s"Received ${responses.size} responses from API")
            for {
              // Start caching in background fiber - don't wait for completion
              _ <- cacheApiResponses(responses, requestPair).start
              // Find and return the requested pair immediately
              result <- findRequestedRate(responses, request)
            } yield result
        }
    }
  }

  private def cacheApiResponses(responses: List[OneFrameApiResponse], requestedPair: String): F[Unit] =
    responses.traverse_ { apiResponse =>
      val pairKey = s"${apiResponse.from}${apiResponse.to}"
      val pairArg = if (pairKey == requestedPair) requestedPair else ""
      cacheManager.cacheRate(pairKey, Program.convertToRate(apiResponse), pairArg).pure[F]
    }

  private def findRequestedRate(responses: List[OneFrameApiResponse],
                                request: Protocol.GetRatesRequest): F[Either[Error, Rate]] = {
    val requestPair = s"${request.from.code}${request.to.code}"
    responses.find(r => r.from == request.from.code && r.to == request.to.code) match {
      case Some(apiResponse) =>
        val rate = Program.convertToRate(apiResponse)
        logger.debug(s"Found $requestPair in API response, price: ${rate.price}")
        (Right(rate): Either[Error, Rate]).pure[F]

      case None =>
        logger.error(s"Requested pair $requestPair not found in API response")
        (Left(Error.RateLookupFailed("Requested pair not found in API response")): Either[Error, Rate]).pure[F]
    }
  }
}

object Program {

  def apply[F[_]: Concurrent](
      ratesService: RatesService[F],
      cacheManager: CacheManager[F]
  ): Algebra[F] = new Program[F](ratesService, cacheManager)

  def convertToRate(apiResponse: OneFrameApiResponse): Rate = {
    val from      = Currency.fromString(apiResponse.from)
    val to        = Currency.fromString(apiResponse.to)
    val pair      = Rate.Pair(from, to)
    val price     = Price(apiResponse.price)
    val timestamp = Timestamp(java.time.OffsetDateTime.parse(apiResponse.time_stamp))
    Rate(pair, price, timestamp)
  }

}
