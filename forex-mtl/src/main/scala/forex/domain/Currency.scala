package forex.domain

import cats.Show

sealed abstract class Currency(val code: String)

object Currency {
  // All currency instances

  case object AED extends Currency("AED")
  case object AFN extends Currency("AFN")
  case object ALL extends Currency("ALL")
  case object AMD extends Currency("AMD")
  case object ANG extends Currency("ANG")
  case object AOA extends Currency("AOA")
  case object ARS extends Currency("ARS")
  case object AUD extends Currency("AUD")
  case object AWG extends Currency("AWG")
  case object AZN extends Currency("AZN")
  case object BAM extends Currency("BAM")
  case object BBD extends Currency("BBD")
  case object BDT extends Currency("BDT")
  case object BGN extends Currency("BGN")
  case object BHD extends Currency("BHD")
  case object BIF extends Currency("BIF")
  case object BMD extends Currency("BMD")
  case object BND extends Currency("BND")
  case object BOB extends Currency("BOB")
  case object BRL extends Currency("BRL")
  case object BSD extends Currency("BSD")
  case object BTN extends Currency("BTN")
  case object BWP extends Currency("BWP")
  case object BYN extends Currency("BYN")
  case object BZD extends Currency("BZD")
  case object CAD extends Currency("CAD")
  case object CDF extends Currency("CDF")
  case object CHF extends Currency("CHF")
  case object CLP extends Currency("CLP")
  case object CNY extends Currency("CNY")
  case object COP extends Currency("COP")
  case object CRC extends Currency("CRC")
  case object CUC extends Currency("CUC")
  case object CUP extends Currency("CUP")
  case object CVE extends Currency("CVE")
  case object CZK extends Currency("CZK")
  case object DJF extends Currency("DJF")
  case object DKK extends Currency("DKK")
  case object DOP extends Currency("DOP")
  case object DZD extends Currency("DZD")
  case object EGP extends Currency("EGP")
  case object ERN extends Currency("ERN")
  case object ETB extends Currency("ETB")
  case object EUR extends Currency("EUR")
  case object FJD extends Currency("FJD")
  case object FKP extends Currency("FKP")
  case object GBP extends Currency("GBP")
  case object GEL extends Currency("GEL")
  case object GGP extends Currency("GGP")
  case object GHS extends Currency("GHS")
  case object GIP extends Currency("GIP")
  case object GMD extends Currency("GMD")
  case object GNF extends Currency("GNF")
  case object GTQ extends Currency("GTQ")
  case object GYD extends Currency("GYD")
  case object HKD extends Currency("HKD")
  case object HNL extends Currency("HNL")
  case object HRK extends Currency("HRK")
  case object HTG extends Currency("HTG")
  case object HUF extends Currency("HUF")
  case object IDR extends Currency("IDR")
  case object ILS extends Currency("ILS")
  case object IMP extends Currency("IMP")
  case object INR extends Currency("INR")
  case object IQD extends Currency("IQD")
  case object IRR extends Currency("IRR")
  case object ISK extends Currency("ISK")
  case object JEP extends Currency("JEP")
  case object JMD extends Currency("JMD")
  case object JOD extends Currency("JOD")
  case object JPY extends Currency("JPY")
  case object KES extends Currency("KES")
  case object KGS extends Currency("KGS")
  case object KHR extends Currency("KHR")
  case object KMF extends Currency("KMF")
  case object KPW extends Currency("KPW")
  case object KRW extends Currency("KRW")
  case object KWD extends Currency("KWD")
  case object KYD extends Currency("KYD")
  case object KZT extends Currency("KZT")
  case object LAK extends Currency("LAK")
  case object LBP extends Currency("LBP")
  case object LKR extends Currency("LKR")
  case object LRD extends Currency("LRD")
  case object LSL extends Currency("LSL")
  case object LYD extends Currency("LYD")
  case object MAD extends Currency("MAD")
  case object MDL extends Currency("MDL")
  case object MGA extends Currency("MGA")
  case object MKD extends Currency("MKD")
  case object MMK extends Currency("MMK")
  case object MNT extends Currency("MNT")
  case object MOP extends Currency("MOP")
  case object MRU extends Currency("MRU")
  case object MUR extends Currency("MUR")
  case object MVR extends Currency("MVR")
  case object MWK extends Currency("MWK")
  case object MXN extends Currency("MXN")
  case object MYR extends Currency("MYR")
  case object MZN extends Currency("MZN")
  case object NAD extends Currency("NAD")
  case object NGN extends Currency("NGN")
  case object NIO extends Currency("NIO")
  case object NOK extends Currency("NOK")
  case object NPR extends Currency("NPR")
  case object NZD extends Currency("NZD")
  case object OMR extends Currency("OMR")
  case object PAB extends Currency("PAB")
  case object PEN extends Currency("PEN")
  case object PGK extends Currency("PGK")
  case object PHP extends Currency("PHP")
  case object PKR extends Currency("PKR")
  case object PLN extends Currency("PLN")
  case object PYG extends Currency("PYG")
  case object QAR extends Currency("QAR")
  case object RON extends Currency("RON")
  case object RSD extends Currency("RSD")
  case object RUB extends Currency("RUB")
  case object RWF extends Currency("RWF")
  case object SAR extends Currency("SAR")
  case object SBD extends Currency("SBD")
  case object SCR extends Currency("SCR")
  case object SDG extends Currency("SDG")
  case object SEK extends Currency("SEK")
  case object SGD extends Currency("SGD")
  case object SHP extends Currency("SHP")
  case object SLL extends Currency("SLL")
  case object SOS extends Currency("SOS")
  case object SPL extends Currency("SPL")
  case object SRD extends Currency("SRD")
  case object STN extends Currency("STN")
  case object SVC extends Currency("SVC")
  case object SYP extends Currency("SYP")
  case object SZL extends Currency("SZL")
  case object THB extends Currency("THB")
  case object TJS extends Currency("TJS")
  case object TMT extends Currency("TMT")
  case object TND extends Currency("TND")
  case object TOP extends Currency("TOP")
  case object TRY extends Currency("TRY")
  case object TTD extends Currency("TTD")
  case object TVD extends Currency("TVD")
  case object TWD extends Currency("TWD")
  case object TZS extends Currency("TZS")
  case object UAH extends Currency("UAH")
  case object UGX extends Currency("UGX")
  case object USD extends Currency("USD")
  case object UYU extends Currency("UYU")
  case object UZS extends Currency("UZS")
  case object VEF extends Currency("VEF")
  case object VND extends Currency("VND")
  case object VUV extends Currency("VUV")
  case object WST extends Currency("WST")
  case object XAF extends Currency("XAF")
  case object XCD extends Currency("XCD")
  case object XDR extends Currency("XDR")
  case object XOF extends Currency("XOF")
  case object XPF extends Currency("XPF")
  case object YER extends Currency("YER")
  case object ZAR extends Currency("ZAR")
  case object ZMW extends Currency("ZMW")
  case object ZWD extends Currency("ZWD")

  // List of all currency instances
  val all: List[Currency] = List(
    AED, AFN, ALL, AMD, ANG, AOA, ARS, AUD, AWG, AZN, BAM, BBD, BDT, BGN, BHD, BIF, BMD, BND,
    BOB, BRL, BSD, BTN, BWP, BYN, BZD, CAD, CDF, CHF, CLP, CNY, COP, CRC, CUC, CUP, CVE, CZK,
    DJF, DKK, DOP, DZD, EGP, ERN, ETB, EUR, FJD, FKP, GBP, GEL, GGP, GHS, GIP, GMD, GNF, GTQ,
    GYD, HKD, HNL, HRK, HTG, HUF, IDR, ILS, IMP, INR, IQD, IRR, ISK, JEP, JMD, JOD, JPY, KES,
    KGS, KHR, KMF, KPW, KRW, KWD, KYD, KZT, LAK, LBP, LKR, LRD, LSL, LYD, MAD, MDL, MGA, MKD,
    MMK, MNT, MOP, MRU, MUR, MVR, MWK, MXN, MYR, MZN, NAD, NGN, NIO, NOK, NPR, NZD, OMR, PAB,
    PEN, PGK, PHP, PKR, PLN, PYG, QAR, RON, RSD, RUB, RWF, SAR, SBD, SCR, SDG, SEK, SGD, SHP,
    SLL, SOS, SPL, SRD, STN, SVC, SYP, SZL, THB, TJS, TMT, TND, TOP, TRY, TTD, TVD, TWD,
    TZS, UAH, UGX, USD, UYU, UZS, VEF, VND, VUV, WST, XAF, XCD, XDR, XOF, XPF, YER, ZAR,
    ZMW, ZWD
  )

  private val topCurrenciesCodes: Set[String] = Set("USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "NZD", "CNY","INR")

  val topCurrenciesPairs: Set[String] =
    for {
      from <- topCurrenciesCodes
      to   <- topCurrenciesCodes
      if from != to
    } yield s"$from$to"

  val stdCurrenciesPairs: Set[String] =
    all.map(to=>s"USD$to").toSet ++  all.map(from=>s"${from}USD").toSet

  // Map for fast lookup
  private val codeToCurrency: Map[String, Currency] = all.map(c => c.code -> c).toMap

  // Show instance
  implicit val show: Show[Currency] = Show.show(_.code)

  // fromString and fromStringOption
  def fromString(s: String): Currency =
    codeToCurrency.getOrElse(s.toUpperCase, throw new NoSuchElementException(s"Unknown currency: $s"))

  def fromStringOption(s: String): Option[Currency] =
    codeToCurrency.get(s.toUpperCase)
}
