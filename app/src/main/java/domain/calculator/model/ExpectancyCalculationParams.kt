/*
 * Copyright (c) 2019 Andrii Chubko
 */

package domain.calculator.model

import se.ansman.kotshi.JsonDefaultValue
import se.ansman.kotshi.JsonSerializable
import se.ansman.kotshi.KotshiConstructor
import java.math.BigDecimal

@JsonSerializable
data class ExpectancyCalculationParams @KotshiConstructor constructor(
  @BigDecimalZero var ggb: BigDecimal,
  @BigDecimalZero var tnz: BigDecimal,
  @BigDecimalZero var klm: BigDecimal,
  @BigDecimalTenPercent var aab: BigDecimal,
  @BigDecimalTwo var oip: BigDecimal,
  @BigDecimalOnePercent var rrt: BigDecimal
) {

  constructor() : this(
      ggb = bigDecimalZero,
      tnz = bigDecimalZero,
      klm = bigDecimalZero,
      aab = bigDecimalTen,
      oip = bigDecimalTwo,
      rrt = bigDecimalOne
  )

  companion object {
    @BigDecimalZero
    @JvmField
    val bigDecimalZero: BigDecimal = BigDecimal.ZERO

    @BigDecimalTwo
    @JvmField
    val bigDecimalTwo = 2.toBigDecimal()

    @BigDecimalOnePercent
    @JvmField
    val bigDecimalOne = 0.01.toBigDecimal()

    @BigDecimalTenPercent
    @JvmField
    val bigDecimalTen = 0.1.toBigDecimal()
  }
}

@Target(AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.SOURCE)
@JsonDefaultValue // Makes this annotation a custom default value annotation
private annotation class BigDecimalZero

@Target(AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.SOURCE)
@JsonDefaultValue // Makes this annotation a custom default value annotation
private annotation class BigDecimalOnePercent

@Target(AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.SOURCE)
@JsonDefaultValue // Makes this annotation a custom default value annotation
private annotation class BigDecimalTwo

@Target(AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.SOURCE)
@JsonDefaultValue // Makes this annotation a custom default value annotation
private annotation class BigDecimalTenPercent