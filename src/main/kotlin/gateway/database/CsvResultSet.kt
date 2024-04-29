package gateway.database

import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.csv.CSVFormat

data class CsvResultSet(
    @field:JsonProperty("codigo_do_produto") val produtoId: Int,
    @field:JsonProperty("produto_descricao") val produtoDescricao: String,
    @field:JsonProperty("GRUPO") val grupoId: Int,
    @field:JsonProperty("FILIAL_NAME") val filialName: String,
    @field:JsonProperty("VALOR_CUSTO") val valorCusto: Int,
    @field:JsonProperty("VALOR_VENDA") val valorVenda: Int,
) {

    constructor() : this(2, "", 2, "", 2, 2)

}

