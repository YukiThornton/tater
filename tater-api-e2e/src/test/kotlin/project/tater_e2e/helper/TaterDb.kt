package project.tater_e2e.helper

import org.dbunit.JdbcDatabaseTester
import org.dbunit.database.DatabaseConfig
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.csv.CsvDataSet
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory
import org.dbunit.operation.DatabaseOperation
import project.tater_e2e.helper.Configurations.Companion.taterDbDriver
import project.tater_e2e.helper.Configurations.Companion.taterDbPassword
import project.tater_e2e.helper.Configurations.Companion.taterDbSchema
import project.tater_e2e.helper.Configurations.Companion.taterDbUrl
import project.tater_e2e.helper.Configurations.Companion.taterDbUserName
import java.io.File

class TaterDb {

    companion object {
        private const val dataSetBasePath = "/project/tater_e2e/tater-db/dataset"
    }

    private val connection: IDatabaseConnection =
        JdbcDatabaseTester(taterDbDriver, taterDbUrl, taterDbUserName, taterDbPassword, taterDbSchema).connection
            .also {
                it.config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, PostgresqlDataTypeFactory())
            }

    fun loadData(dataSetName: String) {
        val dataSet = CsvDataSet(File(TaterDb::class.java.getResource("$dataSetBasePath/$dataSetName").file))
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet)
    }
}