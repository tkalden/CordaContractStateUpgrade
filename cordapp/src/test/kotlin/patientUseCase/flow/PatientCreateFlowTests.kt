package patientUseCase.flow



import patientUseCase.State.PatientState
import patientUseCase.TestUtils
import patientUseCase.TestUtils.Companion.a
import patientUseCase.TestUtils.Companion.network
import net.corda.core.node.services.queryBy
import net.corda.core.utilities.getOrThrow
import org.junit.After
import org.junit.Before
import org.junit.Test
import patientUseCase.Model.Patient
import patientUseCase.PatientSchema.Address
import patientUseCase.TestUtils.Companion.aetna
import patientUseCase.TestUtils.Companion.metroPlus


class PatientCreateFlowTest : TestUtils.TestUtils {
    @Before
    override fun setup() {
        TestUtils.setup(PatientCreateFlowResponder::class.java)
    }

    @After
    override fun tearDown() {
        TestUtils.tearDown()
    }



    /* Tests the create fnol flow  */
    @Test
    fun flowReturnsPatientCreateWithOtherKnownParties() {
        val address = Address()
        val patient = Patient("Derek","R","Johnson",address)
        val participants = listOf(aetna, metroPlus)
        val flow = createPatientFlow(patient, participants)
        val future = a.startFlow(flow)
        network.runNetwork()

        val stx = future.getOrThrow()
        stx.verifyRequiredSignatures()
        val output = stx.coreTransaction.outputs.single().data as PatientState

        //dummy data for state created for node A
        var stateCreatedForA : PatientState? = null
        var stateCreatedForB : PatientState? = null

        // check node A and B vault
        a.transaction {
            stateCreatedForA = a.services.vaultService.queryBy<PatientState>().states.single().state.data
        }

        TestUtils.b.transaction {
            stateCreatedForB = TestUtils.b.services.vaultService.queryBy<PatientState>().states.single().state.data
        }
        //check if same record has been updated using the linearId
        assert(stateCreatedForA!!.linearId.id == output.linearId.id)
        assert(stateCreatedForB!!.linearId.id == output.linearId.id)
    }

}


