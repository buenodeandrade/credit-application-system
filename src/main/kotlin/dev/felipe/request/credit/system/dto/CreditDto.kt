package dev.felipe.request.credit.system.dto

import dev.felipe.request.credit.system.entity.Credit
import dev.felipe.request.credit.system.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
        val creditValue: BigDecimal,
        val dayFirstOfInstallment: LocalDate,
        val numberOfInstallments: Int,
        val customerId: Long
) {

    fun toEntity(): Credit = Credit(
            creditValue = this.creditValue,
            dayFirstInstallment = this.dayFirstOfInstallment,
            numberOfInstallments = this.numberOfInstallments,
            customer = Customer(id = this.customerId)
    )
}
