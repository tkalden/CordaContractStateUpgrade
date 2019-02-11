package newFNOLUseCase.contract


import patientUseCase.State.PatientState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import patientUseCase.Model.NewPatient
import patientUseCase.State.NewPatientState
import patientUseCase.contract.PatientContract


// *****************
// * Upgraded Contract Code *
// *****************

// upgraded contract must implement the UpgradedContract interface

open class NewPatientContract : UpgradedContract<PatientState,NewPatientState>{

    companion object {
        const val PROGRAM_ID: ContractClassName = "patientUseCase.contract.NewPatientContract"
    }

    override  val legacyContract:String = PatientContract::class.java.name

//    override val legacyContractConstraint: AttachmentConstraint = AlwaysAcceptAttachmentConstraint

    // upgrading state
    override fun upgrade(oldState:PatientState) = NewPatientState(NewPatient(oldState.patient.firstName,oldState.patient.middleName,oldState.patient.lastName,oldState.patient.address,12),oldState.participant)

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Create : TypeOnlyCommandData(), Commands

    }

    // A transaction is considered valid if the verify() function of the contract of each of the transaction's input
    // and output states does not throw an exception.
    override fun verify(tx: LedgerTransaction) {

        val command = tx.commands.requireSingleCommand<Commands>()
        //Potentially check for similar FNOLStates already in ledger
        when (command.value) {
            is Commands.Create -> requireThat {
                "No inputs should be consumed when creating a FNOL." using (tx.inputs.isEmpty())
                "Only one output state should be created when creating a FNOL." using (tx.outputs.size == 1)
                val fnol = tx.outputStates.single() as NewPatientState
                "Signatures are needed." using
                        (command.signers.toSet() == fnol.participants.map { it.owningKey }.toSet())
            }

        }
    }
}