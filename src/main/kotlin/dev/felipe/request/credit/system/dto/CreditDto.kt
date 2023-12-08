package dev.felipe.request.credit.system.dto

import dev.felipe.request.credit.system.entity.Credit
import dev.felipe.request.credit.system.entity.Customer
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
        @field:NotNull(message = "Invalid input") val creditValue: BigDecimal,
        @field:Future val dayFirstOfInstallment: LocalDate,
        val numberOfInstallments: Int,
        @field:NotNull(message = "Invalid input") val customerId: Long
) {

    fun toEntity(): Credit = Credit(
            creditValue = this.creditValue,
            dayFirstInstallment = this.dayFirstOfInstallment,
            numberOfInstallments = this.numberOfInstallments,
            customer = Customer(id = this.customerId)
    )
}
