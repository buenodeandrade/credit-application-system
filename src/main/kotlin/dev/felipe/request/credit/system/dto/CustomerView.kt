package dev.felipe.request.credit.system.dto

import com.fasterxml.jackson.core.sym.Name
import dev.felipe.request.credit.system.entity.Customer
import jakarta.validation.constraints.Email
import java.math.BigDecimal

data class CustomerView(
        val firstName: String,
        val lastName: String,
        val cpf: String,
        val income: BigDecimal,
        val email: String,
        val zipCode: String,
        val street: String
) {
    constructor(customer: Customer): this (
            firstName = customer.firstName,
            lastName = customer.lastName,
            cpf = customer.cpf,
            income = customer.income,
            email = customer.email,
            zipCode = customer.address.zipCode,
            street = customer.address.street
    )
}
