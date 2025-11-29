package forex.config

import scala.concurrent.duration.FiniteDuration

case class ApplicationConfig(
    http: HttpConfig,
    rates: RatesConfig,
)

case class HttpConfig(
    host: String,
    port: Int,
    timeout: FiniteDuration
)

case class RatesConfig(
    oneframe: OneFrameConfig,
    interval: FiniteDuration,
)

case class OneFrameConfig(
    url: String,
    token: String,
)

