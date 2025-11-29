package forex.programs.rates

import com.github.benmanes.caffeine.cache.Caffeine
import forex.domain.{ Currency, Rate }
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import java.time.Instant

/**
  * Thread-safe in-memory cache for exchange rates using Caffeine
  *
  * Features:
  * - Automatic TTL expiration (default 5 minutes)
  * - Tracks request frequency for each pair
  * - Returns top pairs (hit count > 5) for API optimization
  */
class CacheManager[F[_]](ttl: FiniteDuration = 5.minute) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private case class CachedRate(rate: Rate, frequency: Int = 1, timestamp: Long)

  private val rateCache = Caffeine
    .newBuilder()
    .expireAfterWrite(ttl.length, ttl.unit)
    .recordStats()
    .build[String, CachedRate]()

  // Track top requested pairs (hit count > 3)
  private val pairFrequency = scala.collection.mutable.Map[String, Int]()

  /**
    * Get a cached rate if available and not expired
    * Returns None if not cached or expired
    * Only increments frequency on actual cache hits
    */
  def getCachedRate(pairKey: String): Option[Rate] =
    Option(rateCache.getIfPresent(pairKey)).flatMap { cachedRate =>
      val ageInMins   = (Instant.now().toEpochMilli - cachedRate.timestamp) / 60000
      val currentFreq = pairFrequency.getOrElse(pairKey, 0) + 1
      if (pairKey.nonEmpty) pairFrequency.update(pairKey, currentFreq)
      logger.debug(s"Fetching rates from Cache hit for pair: $pairKey, age=${ageInMins} mins")
      Some(cachedRate.rate)
    }

  /**
    * Cache a rate and track its frequency
    * Increments frequency counter for the pair
    */
  def cacheRate(pairKey: String, rate: Rate, requestPair: String): Unit = {
    val currentFreq = pairFrequency.getOrElse(requestPair, 0) + 1
    if (requestPair.nonEmpty) pairFrequency.update(requestPair, currentFreq)
    rateCache.put(pairKey, CachedRate(rate, currentFreq, Instant.now().toEpochMilli))
  }

  /**
    * Get all pairs with hit count > 3 (top pairs)
    * These are the most frequently requested pairs and should be included in API requests
    */
  private def getTopPairsCaptured: List[String] =
    pairFrequency
      .filter { case (_, count) => count > 5 }
      .keys
      .toList

  /**
    * Build the pair string for API request
    * Includes: requestPair + topPairsCaptured + stdCurrenciesPairs + topCurrenciesPairs (limited to 300)
    */
  def buildPairString(
      requestPair: Set[String]
  ): List[String] =
    (requestPair ++ getTopPairsCaptured.toSet ++ Currency.stdCurrenciesPairs ++ Currency.topCurrenciesPairs).toList
      .take(300)
}

object CacheManager {

  /**
    * Create a new CacheManager with default TTL (5 minutes)
    */
  def apply[F[_]](ttl: FiniteDuration = 5.minute): CacheManager[F] =
    new CacheManager[F](ttl)
}
