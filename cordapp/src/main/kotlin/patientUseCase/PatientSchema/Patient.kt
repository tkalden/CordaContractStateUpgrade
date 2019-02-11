package patientUseCase.PatientSchema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.serialization.CordaSerializable
import java.util.*
import javax.persistence.*

class PatientSchema

@CordaSerializable
class PatientSchemaV1 : MappedSchema(schemaFamily = PatientSchema::class.java,
        version = 1,
        mappedTypes = listOf(Patient::class.java, Address::class.java)) {
    @Entity
    @Table(name = "patient")
    class Patient : PersistentState {

        @Column(name = "patient_id")
        var patientId: String?

        @Column(name = "firstName")
        var firstName: String?

        @Column(name = "middleName")
        var middleName: String?


        @Column(name = "lastName")
        var lastName: String?

        @OneToMany(cascade = arrayOf(CascadeType.PERSIST))
        @JoinColumns(JoinColumn(name = "output_index", referencedColumnName = "output_index"),
                JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id"))


        @OneToOne(cascade = arrayOf(CascadeType.PERSIST))
        @JoinColumn(name = "addressId")
        var address: Address?

        constructor(patientId: String?, firstName: String?, middleName: String?,
                    lastName: String?, address: Address?) {
            this.patientId = patientId
            this.firstName = firstName
            this.middleName = middleName
            this.lastName = lastName
            this.address = address

        }

        constructor() {
            this.patientId = UUID.randomUUID().toString()
            this.firstName = ""
            this.middleName = ""
            this.lastName = ""
            this.address = null


        }
    }
}


