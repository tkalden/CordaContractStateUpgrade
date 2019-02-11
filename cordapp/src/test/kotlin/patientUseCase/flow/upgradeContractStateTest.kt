package patientUseCase.flow

import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.ContractUpgradeFlow
import net.corda.core.node.services.queryBy
import net.corda.core.utilities.getOrThrow
import org.junit.After
import org.junit.Before
import org.junit.Test
import patientUseCase.Model.Patient
import patientUseCase.PatientSchema.Address
import patientUseCase.State.PatientState
import patientUseCase.TestUtils
import patientUseCase.contract.NewPatientContract.NewPatientContract
import patientUseCase.contract.PatientContract.PatientContract

class upgradeContractStateTest: TestUtils.TestUtils {


    @Before
    override fun setup() {
        TestUtils.setup(PatientCreateFlowResponder::class.java)
    }

    @After
    override fun tearDown() {
        TestUtils.tearDown()
    }

    @Test

    fun authorizeAndInitiateContractUpgrade(){

        System.out.println("STRINGHELLO");
        System.out.println(NewPatientContract::class.qualifiedName)
        System.out.println(PatientContract::class.qualifiedName)

        // create old states
        val address = Address()
        val patient = Patient("Derek","R","Johnson",address)
        val participants = listOf(TestUtils.aetna, TestUtils.metroPlus)
        val createFlow = createPatientFlow(patient, participants)
        val createFuture = TestUtils.a.startFlow(createFlow)
        TestUtils.network.runNetwork()
        val stx = createFuture.getOrThrow()
        stx.verifyRequiredSignatures()

        val oldStateAndRef = stx.tx.outRef<PatientState>(0)



        // authorize upgrade by a
        val authorizeFlowA= ContractUpgradeFlow.Authorise(oldStateAndRef!!, NewPatientContract::class.java)
        TestUtils.a.startFlow(authorizeFlowA).getOrThrow()
        TestUtils.network.runNetwork()

        // authorize upgrade by b

        val authorizeFlowB = ContractUpgradeFlow.Authorise(oldStateAndRef!!, NewPatientContract::class.java)
        TestUtils.b.startFlow(authorizeFlowB).getOrThrow()
        TestUtils.network.runNetwork()

        // initiate by a only one party needs to do this flow S

        val initiateFlowA= ContractUpgradeFlow.Initiate(oldStateAndRef!!, NewPatientContract::class.java)
        TestUtils.a.startFlow(initiateFlowA).getOrThrow()
        TestUtils.network.runNetwork()
    }


}

