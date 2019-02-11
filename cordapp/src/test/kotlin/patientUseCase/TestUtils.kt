package patientUseCase



import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.testing.core.TestIdentity
import net.corda.testing.internal.chooseIdentityAndCert
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockServices
import net.corda.testing.node.StartedMockNode


class TestUtils {
    companion object {

        //Need these for Contract Tests
        val Aetna = TestIdentity(CordaX500Name(organisation = "Aetna", locality = "London", country = "GB"))
        val MetroPlus = TestIdentity(CordaX500Name(organisation = "Metro Plus", locality = "NY", country = "US"))
        val Fidelity = TestIdentity(CordaX500Name(organisation = "Fidelity", locality = "PA", country = "US"))
        val Dummy = TestIdentity(CordaX500Name(organisation = "Dummy", locality = "MA", country = "US"))

        class DummyCommand : TypeOnlyCommandData()

        var ledgerServices = MockServices(listOf("patientUseCase"))


        //********************************************  STARTING NODES *************************************************
        lateinit var network: MockNetwork
        lateinit var a: StartedMockNode
        lateinit var b: StartedMockNode
        lateinit var c: StartedMockNode
        lateinit var d: StartedMockNode

        lateinit var aetna: Party
        lateinit var metroPlus: Party
        lateinit var fidelity: Party
        lateinit var metLife: Party


        fun setup(responder: Class<out FlowLogic<*>>) {
            network = MockNetwork(listOf("patientUseCase"))
            a = network.createPartyNode(CordaX500Name("Aetna", "NY", "US"))
            b = network.createPartyNode(CordaX500Name("MetroPlus", "SAT", "US"))
            c = network.createPartyNode(CordaX500Name("Fidelity", "WAR", "US"))
            d = network.createPartyNode(CordaX500Name("MetLife", "NY", "US"))

            aetna = a.info.chooseIdentityAndCert().party
            metroPlus = b.info.chooseIdentityAndCert().party
            fidelity = c.info.chooseIdentityAndCert().party
            metLife = d.info.chooseIdentityAndCert().party
            //For real nodes this happens automatically, but we have to manually register the flow for tests.
            listOf(a, b, c, d).forEach { it.registerInitiatedFlow(responder) }
            network.runNetwork()

        }


        fun tearDown() {
            network.stopNodes()
        }
    }

        interface TestUtils {
            fun setup()
            fun tearDown()

        }

    }



