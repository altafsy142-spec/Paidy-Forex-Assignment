package forex.services.rates

import forex.http.rates.Protocol.OneFrameApiResponse
import forex.services.rates.errors._

trait Algebra[F[_]] {
  def getOpenFrameExchange(pair: List[String]): F[Either[Error, List[OneFrameApiResponse]]]
}
