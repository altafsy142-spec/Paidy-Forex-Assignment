package forex.http.rates

import forex.domain.Currency
import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.{ParseFailure, Request}

object QueryParams {

  private[http] implicit val currencyQueryParam: QueryParamDecoder[Currency] =
    QueryParamDecoder[String].emap { str =>
      Currency.fromStringOption(str).toRight(org.http4s.ParseFailure("Not supported currency", s"$str is not supported"))
    }

  object FromQueryParam extends QueryParamDecoderMatcher[Currency]("from")
  object ToQueryParam extends QueryParamDecoderMatcher[Currency]("to")

  object FromQueryParamEither {
    def unapply[F[_]](req: Request[F]): Option[Either[ParseFailure, Currency]] =
      req.params.get("from").map { str =>
        Currency.fromStringOption(str).toRight(org.http4s.ParseFailure("Not supported currency", s"$str is not supported"))
      }
  }

  object ToQueryParamEither {
    def unapply[F[_]](req: Request[F]): Option[Either[ParseFailure, Currency]] =
      req.params.get("to").map { str =>
        Currency.fromStringOption(str).toRight(org.http4s.ParseFailure("Not supported currency", s"$str is not supported"))
      }
  }

}
