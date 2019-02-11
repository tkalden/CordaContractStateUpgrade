package patientUseCase.PatientSchema

import net.corda.core.serialization.CordaSerializable
import java.io.Serializable
import java.util.*
import javax.persistence.*

@CordaSerializable
@Entity
@Table(name = "address")
class Address : Serializable {
    @Id
    var addressId: UUID?

    @Column(name = "address_line")
    var addressLine: String?

    @Column(name = "zip")
    var zip: String?

    @Column(name = "country")

    val country: String?


    constructor(addressLine: String?, zip: String?, country:   String?) {
        this.addressId = UUID.randomUUID()
        this.addressLine = addressLine
        this.zip = zip
        this.country = country


    }

    constructor() {
        this.addressId = UUID.randomUUID()
        this.addressLine = ""
        this.zip = ""
        this.country = null
    }
}


