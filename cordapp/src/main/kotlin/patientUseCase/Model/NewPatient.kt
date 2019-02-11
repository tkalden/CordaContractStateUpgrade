package patientUseCase.Model

import net.corda.core.serialization.CordaSerializable
import patientUseCase.PatientSchema.Address
import java.io.Serializable


@CordaSerializable
data class NewPatient(
        var firstName: String?,
        var middleName: String?,
        var lastName: String?,
        var address: Address?,
        var age :Int?
) : Serializable