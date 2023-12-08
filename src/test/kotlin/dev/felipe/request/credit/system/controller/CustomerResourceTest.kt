package dev.felipe.request.credit.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dev.felipe.request.credit.system.dto.CustomerDto
import dev.felipe.request.credit.system.dto.CustomerUpdateDto
import dev.felipe.request.credit.system.entity.Address
import dev.felipe.request.credit.system.entity.Customer
import dev.felipe.request.credit.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import kotlin.random.Random

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerResourceTest {
    @Autowired private lateinit var customerRepository: CustomerRepository
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach fun setup() = customerRepository.deleteAll()
    @AfterEach fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should create a customer and return 201 status` () {
        //given
        val customerDto: CustomerDto = buildCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Felipe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Bueno"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("felipe@mail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("12345"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua ao Lado"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with same CPF and return 409 status`() {
        //given
        customerRepository.save(buildCustomerDto().toEntity())
        val customerDto: CustomerDto = buildCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isConflict)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult the documentation"))                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.exception")
                                .value("class org.springframework.dao.DataIntegrityViolationException")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with firstName empty and return 400 status`() {
        //given
        val customerDto: CustomerDto = buildCustomerDto(firstName = "")
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .content(valueAsString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.web.bind.MethodArgumentNotValidException"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find customer by id and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Felipe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Bueno"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("felipe@mail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("12345"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua ao Lado"))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find customer with invalid id and return 400 status`() {
        //given
        val invalidId: Long = 2L
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.get("$URL/$invalidId")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class dev.felipe.request.credit.system.exception.BusinessException")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should delete customer by id and return 204 status`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
                .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not delete customer by id and return 400 status`() {
        //given
        val invalidId: Long = java.util.Random().nextLong()
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${invalidId}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class dev.felipe.request.credit.system.exception.BusinessException")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should update a customer and return 200 status` () {
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val customerUpdateDto: CustomerUpdateDto = buildCostumerUpdateDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Maria"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Eduarda"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("felipe@mail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("4000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("54321"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua do Outro"))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not update a customer with invalid id and return 400 status` () {
        //given
        val invalidId: Long = java.util.Random().nextLong()
        val customerUpdateDto: CustomerUpdateDto = buildCostumerUpdateDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=$invalidId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class dev.felipe.request.credit.system.exception.BusinessException")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

private fun buildCustomerDto(
        firstName: String = "Felipe",
        lastName: String = "Bueno",
        cpf: String = "28475934625",
        email: String = "felipe@mail.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua ao Lado"
) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street,
        )

    private fun buildCostumerUpdateDto(
        firstName: String = "Maria",
        lastName: String = "Eduarda",
        income: BigDecimal = BigDecimal.valueOf(4000.0),
        zipCode: String = "54321",
        street: String = "Rua do Outro"
    ): CustomerUpdateDto = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street,
    )


}