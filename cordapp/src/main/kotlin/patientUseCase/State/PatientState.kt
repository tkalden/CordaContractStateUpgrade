package patientUseCase.State


import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import net.corda.core.serialization.CordaSerializable
import patientUseCase.Model.Patient
import patientUseCase.PatientSchema.PatientSchemaV1


// *********
// * FNOL State *
// *********
/*
    @param fnol-FNOL both parties FNOL Data
    @param initiatingParty- Party  initiating insurer as a party
    @param involvedParties- listOf(Party)  other insurers as list of party
    @param uniqueIdentifier - Identifier Object - adds unique identifier to state at generation
 */

@CordaSerializable
class PatientState(val patient: Patient,
                   val participant: List<Party>,
                   override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState, QueryableState {


    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return if (schema is PatientSchemaV1) {
            PatientSchemaV1.Patient(
                    this.linearId.id.toString(),
                    this.patient.firstName,
                    this.patient.middleName,
                    this.patient.lastName,
                    this.patient.address
            )

        }   else { throw IllegalArgumentException("Unrecognized schema: ${schema}")
        }
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.


    }


    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(PatientSchemaV1())


/*get involved parties */

    override val participants: List<Party>
        get() = this.participant

}



