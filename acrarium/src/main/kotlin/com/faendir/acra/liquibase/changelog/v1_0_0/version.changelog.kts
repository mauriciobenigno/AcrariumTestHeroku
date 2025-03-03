package com.faendir.acra.liquibase.changelog.v1_0_0

import com.faendir.acra.liquibase.changelog.Author
import com.faendir.acra.liquibase.changelog.ColumnType
import com.faendir.acra.liquibase.changelog.Table

databaseChangeLog {
    changeSet("1.0.0-create-version", Author.F43ND1R) {
        val codeColumn = "code"
        val appColumn = "app_id"
        createTable(Table.VERSION) {
            column(name = "id", type = ColumnType.INT, autoIncrement = true) {
                constraints(nullable = false, primaryKey = true, primaryKeyName = "PK_version")
            }
            column(name = codeColumn, type = ColumnType.INT) {
                constraints(nullable = false)
            }
            column(name = "name", type = ColumnType.STRING) {
                constraints(nullable = false)
            }
            column(name = appColumn, type = ColumnType.INT) {
                constraints(nullable = false, referencedTableName = Table.APP, referencedColumnNames = "id", deleteCascade = true, foreignKeyName = "FK_version_app")
            }
            column(name = "mappings", type = ColumnType.TEXT) {
                constraints(nullable = true)
            }
        }
        addUniqueConstraint(Table.VERSION, "$codeColumn, $appColumn", constraintName = "UK_version")
    }
}