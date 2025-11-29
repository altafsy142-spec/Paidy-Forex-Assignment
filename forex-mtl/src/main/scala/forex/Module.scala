package forex

import cats.effect.{ Concurrent, Timer }
import forex.config.ApplicationConfig
import forex.http.rates.RatesHttpRoutes
import forex.services._
import forex.programs._
import forex.programs.rates.CacheManager
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.middleware.{ AutoSlash, Timeout }
import org.http4s.client.Client
import scala.concurrent.duration._

class Module[F[_]: Concurrent: Timer](config: ApplicationConfig, httpClient: Client[F]) {

  private val ratesService: RatesService[F] =
    RatesServices.live[F](httpClient, config.rates.oneframe)

  private val cacheManager: CacheManager[F] =  CacheManager[F](5.minutes)

  private val ratesProgram: RatesProgram[F] = RatesProgram[F](ratesService, cacheManager)

  private val ratesHttpRoutes: HttpRoutes[F] = new RatesHttpRoutes[F](ratesProgram).routes

  type PartialMiddleware = HttpRoutes[F] => HttpRoutes[F]
  type TotalMiddleware   = HttpApp[F] => HttpApp[F]

  private val routesMiddleware: PartialMiddleware = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    }
  }

  private val appMiddleware: TotalMiddleware = { http: HttpApp[F] =>
    Timeout(config.http.timeout)(http)
  }

  private val http: HttpRoutes[F] = ratesHttpRoutes

  val httpApp: HttpApp[F] = appMiddleware(routesMiddleware(http).orNotFound)

}
