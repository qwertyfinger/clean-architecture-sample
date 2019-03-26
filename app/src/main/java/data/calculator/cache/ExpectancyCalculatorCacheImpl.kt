/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.calculator.cache

import dagger.Reusable
import data.calculator.repository.ExpectancyCalculatorCache
import domain.calculator.model.ExpectancyCalculationParams
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.math.pow

/**
 * Cached implementation for retrieving calculation constants. This class implements the
 * [ExpectancyCalculatorCache] from the Data layer as it is that layers responsibility for defining the
 * operations in which data store implementation layers can carry out.
 */
@Reusable
class ExpectancyCalculatorCacheImpl
@Inject constructor() : ExpectancyCalculatorCache {

  private lateinit var params: ExpectancyCalculationParams

  override fun calculateExpectancy(calculationParams: ExpectancyCalculationParams): Single<BigDecimal> {
    params = calculationParams
    return Single
        .just("")
        .observeOn(Schedulers.computation())
        .map {
          Timber.d(
              "Calculating expectancy with following params: \n %s",
              calculationParams.toString()
          )
          Timber.d("Thread: %s", Thread.currentThread().toString())
          (getMbn() + getKlm()).multiply(
              million() / getTnz()
          )
        }
  }

  private fun getTor(year: Int): BigDecimal {
    if (year == -4) return BigDecimal.ONE
    val power = ((params.rrt.toDouble() + 1).pow(year + 3.5))
    val tor = BigDecimal.ONE / power.toBigDecimal()
    Timber.d("TOR for %d: 1 / %s = %s", year, power, tor.toEngineeringString())
    return tor
  }

  private fun getMbn(): BigDecimal {
    var mbn = BigDecimal.ZERO
    val yearsNumber = params.oip.toInt()
    for (i in -4..yearsNumber) {
      mbn += getGgb(i)
    }
    Timber.d("MBN: %s", mbn.toEngineeringString())
    return mbn
  }

  private fun getGgb(year: Int): BigDecimal {
    val ggb = params.tnz.multiply(getIab(year))
        .multiply(getTor(year))
    Timber.d("GGB for %d: %s", year, ggb.toEngineeringString())
    return ggb
  }

  private fun getIab(year: Int): BigDecimal {
    return when (year) {
      -4 -> m43()
      -3 -> m44()
      -2 -> m45()
      -1 -> m46()
      0 -> m47()
      1 -> m48()
      else -> BigDecimal.ZERO
    }
  }

  private fun getKlm(): BigDecimal {
    var klm = BigDecimal.ZERO
    val yearsNumber = params.oip.toInt()
    for (i in 1..yearsNumber) {
      klm += getAsp(i)
    }
    Timber.d("KLM: %s", klm.toEngineeringString())
    return klm
  }

  private fun getAsp(year: Int): BigDecimal {
    val asp = getTor(year)
        .multiply(params.klm / params.oip)
    Timber.d("ASP: %s", asp.toEngineeringString())
    return asp
  }

  private fun getTnz(): BigDecimal {
    var tnz = BigDecimal.ZERO
    val yearsNumber = params.oip.toInt()
    for (i in 0..yearsNumber) {
      tnz += getGpaForYear(i)
    }
    Timber.d("TNZ: %s", tnz.toEngineeringString())
    return tnz
  }

  private fun getGpaForYear(year: Int): BigDecimal {
    val gpa = gpa().multiply(getTor(year))
    Timber.d("GPA for %d: %s", year, gpa.toEngineeringString())
    return gpa
  }

  private fun gpa(): BigDecimal {
    val ccp = params.ggb.multiply(params.aab)
        .multiply(8760.toBigDecimal())
        .multiply(0.8.toBigDecimal())
    Timber.d("CCP for: %s", ccp.toEngineeringString())
    return ccp
  }

  private fun m43(): BigDecimal = 0.0582450781440759.toBigDecimal()

  private fun m44(): BigDecimal = 0.00849353448332113.toBigDecimal()

  private fun m45(): BigDecimal = 0.121283691168162.toBigDecimal()

  private fun m46(): BigDecimal = 0.334685408055706.toBigDecimal()

  private fun m47(): BigDecimal = 0.454912297376351.toBigDecimal()

  private fun m48(): BigDecimal = 0.0223799907723841.toBigDecimal()

  private fun million(): BigDecimal = 1000000.toBigDecimal()

  private operator fun BigDecimal.div(other: BigDecimal): BigDecimal =
    this.divide(other, java.math.MathContext.DECIMAL32)

}