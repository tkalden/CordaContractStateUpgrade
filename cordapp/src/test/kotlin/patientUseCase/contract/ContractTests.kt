//package patientUseCase.contract
//
//import patientUseCase.TestUtils
//import patientUseCase.TestUtils.Companion.Dummy
//import patientUseCase.TestUtils.Companion.Geico
//import patientUseCase.TestUtils.Companion.Marsh
//import patientUseCase.TestUtils.Companion.Usaa
//import patientUseCase.TestUtils.Companion.ledgerServices
//import patientUseCase.state.FNOLState
//import net.corda.testing.contracts.DummyState
//import net.corda.testing.node.ledger
//import org.junit.Test
//
//class FNOLContractTests {
//
//    @Test
//    fun mustIncludeCreateCommand() {
//        val fnol = FNOLState(TestUtils.sharedFNOL)
//        ledgerServices.ledger {
//            transaction {
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                command(listOf(Geico.publicKey, Usaa.publicKey,Marsh.publicKey), TestUtils.Companion.DummyCommand()) // Wrong type.
//                this.fails()
//            }
//            transaction {
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                command(listOf(Geico.publicKey, Usaa.publicKey,Marsh.publicKey), FNOLContract.Commands.Create()) // Correct type.
//                this.verifies()
//            }
//        }
//    }
//
//    @Test
//    fun createTransactionMustHaveNoInputs() {
//        val fnol = FNOLState(TestUtils.sharedFNOL)
//        //FNOLState(myPolicy, myAsset, myInsured, myClaim, listOf(otherInsured))
//        ledgerServices.ledger {
//
//            transaction {
//                input(FNOLContract.FNOL_CONTRACT_ID, DummyState())
//                command(listOf(Geico.publicKey, Usaa.publicKey,Marsh.publicKey), FNOLContract.Commands.Create())
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                this `fails with` "No inputs should be consumed when creating a FNOL."
//            }
//            transaction {
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                command(listOf(Geico.publicKey, Usaa.publicKey,Marsh.publicKey), FNOLContract.Commands.Create())
//                this.verifies() // As there are no input states.
//            }
//        }
//    }
//
//    @Test
//    fun issueTransactionMustHaveOneOutput() {
//        val fnol = FNOLState(TestUtils.sharedFNOL)
//        ledgerServices.ledger {
//            transaction {
//                command(listOf(Geico.publicKey, Usaa.publicKey,Marsh.publicKey), FNOLContract.Commands.Create())
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol) // Two outputs fails.
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                this `fails with` "Only one output state should be created when creating a FNOL."
//            }
//            transaction {
//                command(listOf(Geico.publicKey, Usaa.publicKey,Marsh.publicKey), FNOLContract.Commands.Create())
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol) // One output passes.
//                this.verifies()
//            }
//        }
//    }
//
//    @Test
//    fun involvedPartiesMustSignCreateTransaction() {
//        val fnol = FNOLState(TestUtils.sharedFNOL)
//        ledgerServices.ledger {
//            transaction {
//                command(Dummy.publicKey, FNOLContract.Commands.Create())
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                this `fails with` "Signatures are needed."
//            }
//            transaction {
//                command(Geico.publicKey, FNOLContract.Commands.Create())
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                this `fails with` "Signatures are needed."
//            }
//            transaction {
//                command(Usaa.publicKey, FNOLContract.Commands.Create())
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                this `fails with` "Signatures are needed."
//            }
//            transaction {
//                command(listOf(Geico.publicKey, Geico.publicKey, Geico.publicKey), FNOLContract.Commands.Create())
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                this `fails with` "Signatures are needed."
//            }
//            transaction {
//                command(listOf(Geico.publicKey, Geico.publicKey, Dummy.publicKey, Usaa.publicKey), FNOLContract.Commands.Create())
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                this `fails with` "Signatures are needed."
//            }
//            transaction {
//                command(listOf(Geico.publicKey, Geico.publicKey, Geico.publicKey, Usaa.publicKey,Marsh.publicKey), FNOLContract.Commands.Create())
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                this.verifies()
//            }
//            transaction {
//                command(listOf(Geico.publicKey, Usaa.publicKey,Marsh.publicKey), FNOLContract.Commands.Create())
//                output(FNOLContract.FNOL_CONTRACT_ID, fnol)
//                this.verifies()
//            }
//        }
//    }
//}
