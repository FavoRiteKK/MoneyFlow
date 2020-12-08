package data

import DataFactory
import app.cash.turbine.test
import com.prof18.moneyflow.db.MoneyFlowDB
import data.db.DatabaseSource
import data.db.DatabaseSourceImpl
import data.db.default.defaultCategories
import data.db.model.TransactionType
import domain.repository.MoneyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import presentation.addtransaction.TransactionToSave
import utilities.BaseTest
import utilities.closeDriver
import utilities.createDriver
import utilities.getDb
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class MoneyRepositoryImplTest : BaseTest() {

    lateinit var databaseSource: DatabaseSource
    lateinit var moneyRepository: MoneyRepository
    lateinit var database: MoneyFlowDB

    @BeforeTest
    fun setup() {
        createDriver()
        database = getDb()
        databaseSource = DatabaseSourceImpl(dbRef = database, dispatcher = Dispatchers.Main)
        moneyRepository = MoneyRepositoryImpl(databaseSource)
    }

    @AfterTest
    fun tearDown() {
        closeDriver()
    }

    @Test
    fun getBalanceRecap_UpdatesDataWhenAddingIncomeAndNoDataPresent() = runTest {
        moneyRepository.getBalanceRecap().test {
            assertEquals(DataFactory.getEmptyBalanceRecap(), expectItem())
        }

        moneyRepository.insertTransaction(
            TransactionToSave(
                dateMillis = Clock.System.now().toEpochMilliseconds(),
                amount = 10.0,
                description = null,
                categoryId = 0,
                transactionType = TransactionType.OUTCOME
            )
        )

        moneyRepository.getBalanceRecap().test {
            val balance = expectItem()
            assertEquals(-10.0, balance.totalBalance)
            assertEquals(10.0, balance.monthlyExpenses)
            assertEquals(0.0, balance.monthlyIncome)
        }
    }

    @Test
    fun getBalanceRecap_UpdatesDataCorrectlyWhenAddingIncome() = runTest {

        // Setup
        moneyRepository.insertTransaction(
            TransactionToSave(
                dateMillis = Clock.System.now().toEpochMilliseconds(),
                amount = 10.0,
                description = null,
                categoryId = 0,
                transactionType = TransactionType.OUTCOME
            )
        )

        moneyRepository.insertTransaction(
            TransactionToSave(
                dateMillis = Clock.System.now().toEpochMilliseconds(),
                amount = 10.0,
                description = null,
                categoryId = 0,
                transactionType = TransactionType.OUTCOME
            )
        )

        // Act
        moneyRepository.insertTransaction(
            TransactionToSave(
                dateMillis = Clock.System.now().toEpochMilliseconds(),
                amount = 20.0,
                description = null,
                categoryId = 0,
                transactionType = TransactionType.INCOME
            )
        )

        // Assert
        moneyRepository.getBalanceRecap().test {
            val balance = expectItem()
            assertEquals(0.0, balance.totalBalance)
            assertEquals(20.0, balance.monthlyExpenses)
            assertEquals(20.0, balance.monthlyIncome)
        }
    }

    @Test
    fun getBalanceRecap_UpdatesDataCorrectlyWhenAddingOutcome() = runTest {
        // Setup
        moneyRepository.insertTransaction(
            TransactionToSave(
                dateMillis = Clock.System.now().toEpochMilliseconds(),
                amount = 10.0,
                description = null,
                categoryId = 0,
                transactionType = TransactionType.OUTCOME
            )
        )

        moneyRepository.insertTransaction(
            TransactionToSave(
                dateMillis = Clock.System.now().toEpochMilliseconds(),
                amount = 10.0,
                description = null,
                categoryId = 0,
                transactionType = TransactionType.INCOME
            )
        )

        // Act
        moneyRepository.insertTransaction(
            TransactionToSave(
                dateMillis = Clock.System.now().toEpochMilliseconds(),
                amount = 20.0,
                description = null,
                categoryId = 0,
                transactionType = TransactionType.OUTCOME
            )
        )

        // Assert
        moneyRepository.getBalanceRecap().test {
            val balance = expectItem()
            assertEquals(-20.0, balance.totalBalance)
            assertEquals(30.0, balance.monthlyExpenses)
            assertEquals(10.0, balance.monthlyIncome)
        }
    }

    @Test
    fun getLatestTransactions_returnsCategoryIfDescriptionIsNull() = runTest {

        database.transactionTableQueries.insertTransaction(
            dateMillis = Clock.System.now().toEpochMilliseconds(),
            amount = 10.0,
            description = null,
            categoryId = 1,
            type = TransactionType.INCOME
        )

        moneyRepository.getLatestTransactions().test {
            val transactions = expectItem()
            print(transactions)
            assertEquals(defaultCategories.first().name, transactions.first().title)
        }
    }

    @Test
    fun getLatestTransactions_returnsDescriptionIfDescriptionIsNotNull() = runTest {

        database.transactionTableQueries.insertTransaction(
            dateMillis = Clock.System.now().toEpochMilliseconds(),
            amount = 10.0,
            description = "Description",
            categoryId = 1,
            type = TransactionType.INCOME
        )

        moneyRepository.getLatestTransactions().test {
            val transactions = expectItem()
            print(transactions)
            assertEquals("Description", transactions.first().title)
        }
    }

    // TODO: test delete transaction


}