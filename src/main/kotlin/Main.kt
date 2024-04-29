import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import gateway.database.CsvResultSet
import java.io.FileOutputStream
import java.sql.DriverManager


fun main(args: Array<String>) {


    Class.forName("org.firebirdsql.jdbc.FBDriver")
    val connection = DriverManager.getConnection(
        "jdbc:firebirdsql:localhost/3050:/Users/caiocaminha/Documents/monkey.fdb",
        "SYSDBA","password")


    val resultSet = connection.createStatement().executeQuery("SELECT pf.PRODUTO_ID, p.PRODUTO_DESCRICAO, p.GRUPO_ID, f.FILIAL_NAME, pf.PRODUTO_VR_CUSTO, pf.PRODUTO_VR_VENDA_ATACADO FROM PRODUTOS_FILIAL pf " +
            "INNER JOIN PRODUTO p ON p.PRODUTO_ID = pf.PRODUTO_ID " +
            "INNER JOIN FILIAL f ON f.FILIAL_ID = pf.FILIAL_ID")

    val resultSetList = mutableListOf<CsvResultSet>()

    val csvResultList = mutableListOf<CsvResultSet>()



    while (resultSet.next()) {
        resultSetList.add(
            CsvResultSet(
                produtoId = resultSet.getInt("produto_id"),
                produtoDescricao = resultSet.getString("produto_descricao"),
                grupoId = resultSet.getInt("grupo_id"),
                filialName = resultSet.getString("filial_name"),
                valorCusto = resultSet.getInt("PRODUTO_VR_CUSTO"),
                valorVenda = resultSet.getInt("PRODUTO_VR_VENDA_ATACADO"),
            )
        )
    }

    resultSetList.forEach { csvResult ->
        val filteredList = resultSetList
            .filter { it.produtoId == csvResult.produtoId }

        if(csvResult.valorCusto == 0) {
            val valorCusto = filteredList.filter { it.valorCusto != 0 }
                .first().valorCusto

            csvResultList.add(
                csvResult.copy(valorCusto = valorCusto)
            )
        } else if(csvResult.valorVenda == 0) {
            val valorVenda = filteredList.filter { it.valorVenda != 0 }
                .first().valorVenda

            csvResultList.add(
                csvResult.copy(valorVenda = valorVenda)
            )
        }
    }

    val csvMapper = CsvMapper().apply {
        enable(CsvParser.Feature.TRIM_SPACES)
        enable(CsvParser.Feature.SKIP_EMPTY_LINES)
    }

    val schema = CsvSchema.builder()
        .addNumberColumn("PRODUTO_ID")
        .addColumn("PRODUTO_DESCRICAO")
        .addColumn("GRUPO")
        .addColumn("FILIAL_NAME")
        .addColumn("VALOR_CUSTO")
        .addColumn("VALOR_VENDA")
        .build()

    schema.withSkipFirstDataRow(true)



    csvResultList.forEach {
        println(it)
    }


    csvMapper.writer().with(schema.withHeader()).writeValues(FileOutputStream("/Users/caiocaminha/Desktop/gestor_planilha.csv")).writeAll(csvResultList)

}






