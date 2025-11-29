## Forex API Caching Strategy - Technical Analysis & Implementation
## Executive Summary

The Forex proxy service has been enhanced with an intelligent caching strategy to optimize API token usage while maintaining 24/7 availability.
Given the constraint of 1000 API requests per day and 167 supported currencies, a smart prioritization algorithm ensures efficient data refresh without exhausting token limits.

### Currency Pair Complexity
- **Total Supported Currencies:** 167
- **Unique Possible Pairs:** 167 × 166 = **27,722 pairs**
- **API Constraint:** Maximum 1000 requests per day per token
- **Data Freshness Requirement:** Maximum 5-minute TTL

### Mathematical Challenge
```
Worst Case Scenario:
  Total Pairs: 27,722
  API Capacity: 1000 requests/day × 300 pairs/request = 300,000 pair data points
  Required Refresh Rate: Every 5 minutes (288 refreshes/day minimum)

  Result: Impossible to refresh all 27K pairs every 5 mins with 1000 tokens
```

### Why Reverse Pair Optimization Isn't Applicable
- **Rate(A→B) ≠ 1/Rate(B→A)** - These are different market rates
- Market dynamics, bid-ask spreads, and exchange fees mean reverse rates are independently determined
- Cannot reliably compute one from the other

---

## Implemented Solution: Smart Priority-Based Caching

### Strategy Overview
Instead of attempting to cache all 27,722 pairs, we implement a **dynamic priority ranking system** that intelligently selects which pairs to fetch and refresh.

### Priority Ranking Algorithm

**Tier 1: User-Requested Pairs (Highest Priority)**
- Direct user requests: `A → B`
- Reverse pair: `B → A`
- Frequency threshold: Always cached

**Tier 2: Top User-Requested Pairs. (On first run it will be 0)
- As number of hits grows it keeps track of most requested pairs (both directions)
- Frequency threshold: Hit count > 5
- Frequency threshold: Always cached.(MAX 298 pairs)

**Tier 3: Top 10 Major Currencies (Medium Priority if tier 2 has <298 apirs)**
- Global currency pairs among major economies
- Base currencies: USD, EUR, GBP, JPY, CHF, CAD, AUD, NZD, CNY, INR
- Approximate combinations: 10 × 9 = ~90 pairs
- Frequency threshold: Hit count > 5

**Tier 4: USD Global Pairs (Low Priority: If all above pairs < 300)**
- USD as global reference currency
- USD paired with all 167 currencies (both directions)

- **Selected:** Top 300 pairs from USD combinations
- Refresh strategy: Every API call

**Result per API Call:**
```
Tier 1: 2 pairs (requested A→B, B→A)
Tier 2: 0-298 pairs (based on frequency)
Tier 3: ~88 pairs (top 10 currencies)
Tier 4: ~210 pairs (USD combinations)
─────────────────────────────
Total: ~300 pairs max per API request
```

---

## Token Efficiency Analysis

### Capacity Planning for 1000 Tokens/Day

**Supporting 30 Primary Currencies (Optimized Scenario):**

```
Primary Currency Set Size: 30
Possible Pairs: 30 × 29 = 870 unique pairs

API Call Strategy:
  Pairs per call: 300
  Calls needed: 3 calls to cover all 870 pairs
  Refresh interval: 5 minutes

Daily Load:
  API calls/hour: 3 calls × (60 mins / 5 mins) = 36 calls
  API calls/day: 36 × 24 hours = 864 calls ✓ (within 1000 limit)

Buffer: 136 API calls remaining for:
  - Peak hour demand spikes
  - Ad-hoc currency pair requests
  - Error recovery and retries
```

### Scaling to All 27,722 Pairs

**Option 1: Dynamic Token Conservation**
```
If any hour has no requests:
  Tokens saved that hour: 36 calls
  Over-time savings: 36 × 8-10 idle hours ≈ 288-360 calls/day

Cumulative Buffer:
  Base capacity: 864 calls
  Dynamic savings: ~300 calls
  Total: ~1164 calls available for extended coverage

Result: Can support ~40-50 active currencies with full 5-min refresh
```

**Option 2: Tiered Refresh Strategy**
```
High-frequency pairs (Tier 1 & 2):
  Refresh rate: 5 minutes (36 calls/hour)

Medium-frequency pairs (Tier 3):
  Refresh rate: 15 minutes (12 calls/hour)

Low-frequency pairs (Fallback):
  Refresh rate: 30+ minutes (on-demand only)

Daily usage: ~750 calls for balanced coverage
```

**Option 3: Token Capacity Increase**
```
For 100% availability of all 27,722 pairs:
  Required daily tokens: 27,722 pairs ÷ 300 pairs/call ÷ 288 refreshes/day
  Estimated tokens needed: 20,000+ tokens/day

Cost-benefit analysis: Evaluate against business requirements

## Future Improvements

### 1. Advanced Frequency Algorithm
**Current:** Simple hit counter (count > 5)

**Proposed:** Time-window based ranking
```scala
// Weighted by recency
def calculateWeight(hits: Int, lastAccessAge: Duration): Double = {
  val recencyFactor = 1.0 / (1.0 + lastAccessAge.toMinutes)
  hits * recencyFactor
}

// Top pairs weighted by recent usage patterns
def getAdaptiveTopPairs: List[String] = {
  pairFrequency
    .mapValues { count => calculateWeight(count, ...) }
    .filter { case (_, weight) => weight > threshold }
    .keys.toList
}
```

**Benefits:**
- Adapts to time-of-day patterns
- Seasonal variations captured
- Reduces tokens during off-peak hours

### 2. Type-Safe Currency Pairs
**Current:** String-based pairs ("USDJPY")

**Proposed:** Tagged types
```scala
trait CurrencyPair {
  type From
  type To
  def from: Currency[From]
  def to: Currency[To]
  def rate: Rate
}

// Compile-time verification of pair validity
case class ValidPair[F, T](from: Currency[F], to: Currency[T], rate: Rate)
```

**Benefits:**
- Compile-time safety
- Prevents invalid pair combinations
- Better IDE support and refactoring


## Recommendations

### Immediate (Current Implementation)
✅ Deploy smart caching with priority algorithm
✅ Monitor token usage daily
✅ Track frequency patterns for optimization
✅ Implement comprehensive logging

### Short-term (1-3 months)
⚠ Analyze real usage patterns
⚠ Identify most-requested currency pairs
⚠ Consider increasing to 40-50 primary currencies if demand warrants

### Medium-term (3-6 months)
🔄 Implement time-window based frequency algorithm
🔄 Add type-safe currency pair handling
🔄 Consider token budget increase if requirements expand

### Long-term (6+ months)
📊 Deploy ML-based prediction model
📊 Implement geo-temporal optimization
📊 Evaluate partnerships for additional tokens

---

## Conclusion

The implemented solution provides **optimal token efficiency** while maintaining **24/7 availability** for a practical subset of currencies. The system is:

- **Scalable:** Can expand to 40-50 currencies with simple configuration
- **Adaptive:** Learns usage patterns automatically
- **Resilient:** Built-in buffer for demand spikes
- **Observable:** Comprehensive logging for monitoring

For 100% coverage of all 27,722 pairs, a token capacity increase to 20,000+ would be required. Current implementation balances **data freshness, availability, and token efficiency** within practical constraints.

---

## Appendix: Token Usage Formula

```
Minimum API calls/day = (Total Pairs ÷ Pairs per call) × (86,400 secs ÷ TTL secs)

