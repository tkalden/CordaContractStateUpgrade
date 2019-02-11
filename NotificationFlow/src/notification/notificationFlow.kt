package notification

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.*
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@Deprecated("This Flow has been deprecated. If you are trying to notify another party in the system" +
        ", speak with the Platform team or refer to the ProcedureDocumentation")
class NotificationFlow(/*val archivedStates: List<StateRef>,*/
                       val committedStates: List<ContractState>): FlowLogic<Any>(){
    override fun call(): Boolean {
        val txBuilder = TransactionBuilder()
        //archivedStates.map { txBuilder.addInputState(it) }
        committedStates.map { txBuilder.addOutputState(it, NotifyContract.NOTIFY_CONTRACT_ID) }
        val allParticipants= committedStates.flatMap { it.participants - ourIdentity}
        val allParticipantsKeys = allParticipants.map { it.owningKey }

        txBuilder.addCommand(Command(NotifyContract.Commands.Notify(), allParticipantsKeys))
        txBuilder.verify(serviceHub)

        val flowSessions = allParticipants.map { initiateFlow(it as Party) }.toSet()

        val signedNotification = subFlow(CollectSignaturesFlow(serviceHub.signInitialTransaction(txBuilder), flowSessions))
        return true
    }
}

@InitiatedBy(NotificationFlow::class)
class NotificationFlowResponder(val flowSession: FlowSession): FlowLogic<Unit>(){
    @Suspendable
    override fun call() {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                /*Potentially run some verifications*/
                /*Call Notification API*/
                val committedLinearState = stx.tx.outputStates.first() as LinearState
            }
        }
        subFlow(signedTransactionFlow)
    }
}


open class NotifyContract : Contract{

    companion object {
        @JvmStatic
        val NOTIFY_CONTRACT_ID = "notification.NotifyContract"
    }

    interface Commands: CommandData{
        class Notify: TypeOnlyCommandData(), Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command  = tx.commands.requireSingleCommand<Commands>()
        when(command.value){
            is Commands.Notify -> requireThat {
                "There must be at least one state for notification" using !(tx.inputStates.isEmpty() && tx.outputStates.isEmpty())
            }
        }
    }
}