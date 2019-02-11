package patientUseCase.flow

import co.paralleluniverse.fibers.Suspendable
import patientUseCase.State.PatientState
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import org.slf4j.LoggerFactory
import patientUseCase.Model.Patient
import patientUseCase.contract.PatientContract


@CordaSerializable
@InitiatingFlow(1)
@StartableByRPC
class createPatientFlow(val patient: Patient, val participant: List<Party>) : FlowLogic<SignedTransaction>() {
    companion object {
        private val logger = LoggerFactory.getLogger(createPatientFlow::class.java)
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction based on new FNOL.")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints.")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with our private key.")
        object GATHERING_SIGS : ProgressTracker.Step("Gathering the counterparty's signature.") {
            override fun childProgressTracker() = CollectSignaturesFlow.tracker() }
        object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(
                GENERATING_TRANSACTION,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                GATHERING_SIGS,
                FINALISING_TRANSACTION
        )
    }
    override val progressTracker = tracker()
    @Suspendable
    override fun call(): SignedTransaction {
        //node identity
        val me = serviceHub.myInfo.legalIdentities[0]
        logger.info("Entered into method call() for create FNOL for initiating party:" + me.name)

        progressTracker.currentStep = GENERATING_TRANSACTION
        logger.debug("progressTracker.currentStep" + progressTracker.currentStep)
        //write the state
        val patientState = PatientState(patient,participant)

        //declare the notary
        val notary = serviceHub.networkMapCache.notaryIdentities.single()
        //
        val cmd= Command(PatientContract.Commands.Create(), patientState.participants.map { it.owningKey })
        val txBuilder = TransactionBuilder(notary = notary)
                .addOutputState(patientState, PatientContract.PROGRAM_ID)
                .addCommand(cmd)
        progressTracker.currentStep = VERIFYING_TRANSACTION
        txBuilder.verify(serviceHub)
        logger.debug("progressTracker.currentStep" + progressTracker.currentStep)
        progressTracker.currentStep = SIGNING_TRANSACTION
        val signInitTx = serviceHub.signInitialTransaction(txBuilder)
        logger.debug("progressTracker.currentStep" + progressTracker.currentStep)
        val finalFlow: FinalityFlow
        if (patientState.participants.size > 1) {
            logger.debug("Processing Involved Parties")
            val requiredSignersMinusCurrentNode = patientState.participants - ourIdentity
            progressTracker.currentStep = GATHERING_SIGS
            logger.debug("progressTracker.currentStep" + progressTracker.currentStep)
            val flowSessions = (requiredSignersMinusCurrentNode.map { initiateFlow(it) }).toSet()
            val signedTx = subFlow(CollectSignaturesFlow(signInitTx, flowSessions, GATHERING_SIGS.childProgressTracker()))
            finalFlow = FinalityFlow(signedTx, FINALISING_TRANSACTION.childProgressTracker())
            logger.debug("progressTracker.currentStep" + progressTracker.currentStep)
        } else {
            logger.debug("No Involved Parties")
            finalFlow = FinalityFlow(signInitTx, FINALISING_TRANSACTION.childProgressTracker())
        }
        progressTracker.currentStep = FINALISING_TRANSACTION
        logger.debug("progressTracker.currentStep" + progressTracker.currentStep)
        logger.info("Exit method call() for create FNOL for initiating party:" + me.name)
        return subFlow(finalFlow)

    }
}

@InitiatedBy(createPatientFlow::class)
class PatientCreateFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs[0].data as PatientState
                "This must be an FNOL creation" using (output is PatientState)
            }
        }
        subFlow(signedTransactionFlow)
    }

}