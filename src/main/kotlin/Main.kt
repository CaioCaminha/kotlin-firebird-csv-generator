import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import gateway.database.CsvResultSet
import java.io.FileOutputStream
import java.sql.DriverManager
import java.text.NumberFormat
import java.util.*


fun main(args: Array<String>) {
    //Class.forName("org.firebirdsql.jdbc.FBDriver")
    //Class.forName("interbase.interclient.Driver")
    val connection = DriverManager.getConnection("jdbc:h2:mem:;INIT=RUNSCRIPT FROM 'classpath:generator.sql';")

    val resultSet = connection.createStatement().executeQuery("""
        select * from TEST;        
    """.trimIndent())

    val resultSetList = mutableListOf<CsvResultSet>()

    val csvResultList = mutableListOf<CsvResultSet>()

    while (resultSet.next()) {

        println(resultSet.getString("PRECO_COMPRA"))

        resultSetList.add(
            CsvResultSet(
                codigo_do_produto = resultSet.getString("CODIGO_DO_PRODUTO") ?: "0",
                descricao = resultSet.getString("DESCRICAO") ?: "0",
                ncm = resultSet.getString("NCM") ?: "0",
                preco_venda = resultSet.getString("PRECO_VENDA") ?: "0",
                preco_compra = resultSet.getString("PRECO_COMPRA") ?: "0",
                unidade = resultSet.getString("UNIDADE") ?: "0",
                marca = resultSet.getString("MARCA") ?: "0",
                local_de_estoque = resultSet.getString("LOCAL_DE_ESTOQUE") ?: "0",
                grupo = resultSet.getString("GRUPO") ?: "0",
            )
        )
    }


    resultSetList.removeIf {
        it.preco_venda == "0" && it.preco_compra == "0"
    }

    val numberFormat = NumberFormat.getInstance(Locale.GERMAN)

    resultSetList.forEach { csvResult ->
        val filteredList = resultSetList
            .filter { it.codigo_do_produto == csvResult.codigo_do_produto }



        if(numberFormat.parse(csvResult.preco_compra).toDouble() == 0.0) {
            val valorCusto = filteredList.firstOrNull { numberFormat.parse(it.preco_compra).toDouble() != 0.0 }?.preco_compra

            if (valorCusto != null) {
                csvResultList.add(
                    csvResult.copy(preco_compra = valorCusto)
                )
            }
        } else if(numberFormat.parse(csvResult.preco_venda).toDouble() == 0.0) {
            val valorVenda = filteredList.firstOrNull { numberFormat.parse(it.preco_venda).toDouble() != 0.0 }?.preco_venda

            if (valorVenda != null){
                csvResultList.add(
                    csvResult.copy(preco_venda = valorVenda)
                )
            }
        }
    }

    println(resultSetList)

    val csvMapper = CsvMapper().apply {
        enable(CsvParser.Feature.TRIM_SPACES)
        enable(CsvParser.Feature.SKIP_EMPTY_LINES)
    }

    val schema = CsvSchema.builder()
        .addNumberColumn("codigo_do_produto")
        .addColumn("descricao")
        .addColumn("ncm")
        .addColumn("preco_compra")
        .addColumn("preco_venda")
        .addColumn("unidade")
        .addColumn("marca")
        .addColumn("local_de_estoque")
        .addColumn("grupo")
        .build()

    schema.withSkipFirstDataRow(true)



    csvResultList.forEach {
        println(it)
    }


    csvMapper.writer().with(schema.withHeader()).writeValues(FileOutputStream("/Users/caiocaminha/Desktop/GESTOR_PLANILHA_NOVA.csv")).writeAll(csvResultList)

}






