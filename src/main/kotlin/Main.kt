import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import gateway.database.CsvResultSet
import java.io.FileOutputStream
import java.sql.DriverManager


fun main(args: Array<String>) {
    Class.forName("org.firebirdsql.jdbc.FBDriver")
    //Class.forName("interbase.interclient.Driver")
    val connection = DriverManager.getConnection(
        "jdbc:firebirdsql://localhost/3050:C:/gestor/BANCO/",
    )
    val resultSet = connection.createStatement().executeQuery("""
        select pf.produto_id as codigo_do_produto,
            pd.produto_descricao as descricao,
            pd.codfis_id as ncm,
            pf.produto_vr_custo as preco_compra,
            pf.produto_vr_venda_varejo as preco_venda,
            pf.produto_qtd_estoque as unidade,
            ma.marca_descricao as marca,
            fi.filial_nome as local_de_estoque,
            gp.grupo_descricao as grupo
        from produtos_filial pf
            left join produto pd on pd.produto_id = pf.produto_id
            inner join marca ma on ma.marca_id = pd.marca_id
            inner join filial fi on fi.filial_id = pf.filial_id
            inner join grupo gp on gp.grupo_id = pd.grupo_id
        where pd.produto_situacao = 0
    """.trimIndent())

    val resultSetList = mutableListOf<CsvResultSet>()

    val csvResultList = mutableListOf<CsvResultSet>()



    while (resultSet.next()) {
        resultSetList.add(
            CsvResultSet(
                produtoId = resultSet.getInt("produtdo_id"),
                produtoDescricao = resultSet.getString("descricao"),
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






