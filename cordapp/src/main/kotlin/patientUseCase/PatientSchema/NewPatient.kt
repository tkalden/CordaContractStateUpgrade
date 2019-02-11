package patientUseCase.PatientSchema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.serialization.CordaSerializable
import java.util.*
import javax.persistence.*

class NewPatientSchema

@CordaSerializable
class PatientSchemaV2 : MappedSchema(schemaFamily = NewPatientSchema::class.java,
        version = 1,
        mappedTypes = listOf(NewPatient::class.java, Address::class.java)) {
    @Entity
    @Table(name = "patient")
    class NewPatient : PersistentState {

        @Column(name = "patient_id")
        var patientId: String?

        @Column(name = "firstName")
        var firstName: String?

        @Column(name = "middleName")
        var middleName: String?


        @Column(name = "lastName")
        var lastName: String?

        @Column(name = "age")
        var age: Int?


        @OneToMany(cascade = arrayOf(CascadeType.PERSIST))
        @JoinColumns(JoinColumn(name = "output_index", referencedColumnName = "output_index"),
                JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id"))


        @OneToOne(cascade = arrayOf(CascadeType.PERSIST))
        @JoinColumn(name = "addressId")
        var address: Address?



        constructor(patientId: String?, firstName: String?, middleName: String?,
                    lastName: String?, address: Address?, age:Int?) {
            this.patientId = patientId
            this.firstName = firstName
            this.middleName = middleName
            this.lastName = lastName
            this.address = address
            this.age = age

        }

        constructor() {
            this.patientId = UUID.randomUUID().toString()
            this.firstName = ""
            this.middleName = ""
            this.lastName = ""
            this.address = null
            this.age = 12


        }
    }
}


