package forex.services.rates

import cats.effect.Sync
import forex.config.OneFrameConfig
import forex.services.rates.interpreters._
import org.http4s.client.Client

object Interpreters {
  def live[F[_]: Sync](
      client: Client[F],
      config: OneFrameConfig
  ): Algebra[F] = new OneFrameLive[F](client, config.url, config.token)
}
