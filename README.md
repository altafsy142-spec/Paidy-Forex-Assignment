FOREX PROXY SERVICE – CACHING & TOKEN OPTIMIZATION STRATEGY
==========================================================

1. OVERVIEW
-----------
This document describes the caching strategy implemented for the Forex proxy 
service. The service maintains 5-minute data freshness while staying within an 
API limit of 1000 requests/day, supporting 167 currencies.

Challenges:
- 167 currencies → 27,722 unique pairs
- Only 300 pairs can be fetched per API call
- Only 1000 API calls per day allowed
- Required maximum TTL: 5 minutes

Refreshing all 27,722 pairs every 5 minutes is mathematically impossible. The 
solution is a dynamic priority-based caching algorithm.

----------------------------------------------------------

2. CURRENCY PAIR MATHEMATICS
----------------------------
Pair count:
167 × 166 = 27,722 unique pairs

API capacity:
1000 requests/day × 300 pairs/request = 300,000 pairs/day-- Full Capacity of One Frame API/day

Required for 5-minute refresh:
27,722 × 288 = 7.9 million pair values/day → impossible

Reverse pairs (A→B and B→A) cannot be inverted due to spreads and liquidity 
differences. Both must be fetched independently.

----------------------------------------------------------

3. SOLUTION SUMMARY
-------------------
The service uses a four-tier priority model to decide which 300 pairs to fetch per API call. Selection is based on:
- User requests
- Frequency of access
- Major currency significance
- USD global fallback coverage

----------------------------------------------------------

4. TIERED PRIORITY MODEL
------------------------

Tier 1 – User Requested Pairs (Highest Priority)
- Always fetch A→B and B→A
- Strict 5-minute TTL
- Highest priority on every call

Tier 2 – Frequently Accessed Pairs (0–298 pairs)
- User hit counter > 5
- Both directions included
- Max capacity: 298 pairs

Tier 3 – Major Global Currencies (~90 pairs)
Top 10: USD, EUR, GBP, JPY, CHF, CAD, AUD, NZD, CNY, INR
Combinations: 10 × 9 = 90 pairs
Included when Tier 2 has fewer than 298 pairs

Tier 4 – USD Global Coverage (~210 pairs)
- USD paired with all 167 currencies
- Used to fill remaining available slots

API Call Summary (Up to 300 pairs):
Tier 1: 2 pairs
Tier 2: 0–298 pairs
Tier 3: ~88 pairs
Tier 4: Remainder to complete 300

----------------------------------------------------------

5. TOKEN CONSUMPTION STRATEGY: (I have implimneted dynamic streategy to support all, belo is illustration of strategy)
-----------------------------

Case: Supporting ~30 Primary Currencies Fully
Pairs = 30 × 29 = 870
Calls needed = 3 per 5-minute cycle
Daily calls = 3 × 288 = 864 (within 1000 limit)
Remaining 136 calls: spikes, retries, on-demand pairs.

Scaling Strategies:

Option 1 – Idle-Time Token Savings
Idle hour saves: 36 calls
Daily savings: 288–360 calls
Supports up to 40–50 currencies at 5-minute TTL.

Option 2 – Tiered Refresh Rate
Tier 1–2: 5 minutes
Tier 3: 15 minutes
Tier 4: 30+ minutes
Total: 700–800 calls/day

Option 3 – Token Scaling
Full 27,722 pairs require ~20,000 API calls/day.

----------------------------------------------------------

6. FUTURE IMPROVEMENTS
----------------------

1. Adaptive Time-Window Frequency
Weighted by recency:
  weight = hits × (1 / (1 + lastAccessMinutes))

Benefits:
- Adapts to peak patterns
- Reduces wasted refreshes
- Reacts to recent trends

2. Type-Safe Currency Pair Model
Safer than strings like "USDJPY":
  ValidPair(from, to, rate)

Benefits:
- Compile-time validation
- Avoids invalid pair combinations
- Easier refactoring

----------------------------------------------------------

7. RECOMMENDATIONS
------------------
Immediate:
- Deploy priority caching
- Enable hit frequency tracking
- Monitor token use
- Add structured logging

1–3 Months:
- Expand major currency set dynamically
- Analyze traffic to refine tiers

3–6 Months:
- Add adaptive recency algorithm
- Use strongly typed pair model
- Add monitoring dashboards

6+ Months:
- ML-based request prediction
- Geo/timezone-aware refresh logic
- Evaluate API token upgrades

----------------------------------------------------------

8. CONCLUSION
-------------

The implemented solution provides **optimal token efficiency** while maintaining **24/7 availability** for a practical subset of currencies. The system is:

- **Scalable:** Can expand to 40-50 currencies with simple configuration
- **Adaptive:** Learns usage patterns automatically
- **Resilient:** Built-in buffer for demand spikes
- **Observable:** Comprehensive logging for monitoring

For 100% coverage of all 27,722 pairs, a token capacity increase to 20,000+ would be required. Current implementation balances **data freshness, availability, and token efficiency** within practical constraints.


