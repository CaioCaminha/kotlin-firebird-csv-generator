package gateway.database

import com.fasterxml.jackson.annotation.JsonProperty

data class CsvResultSet(
    @field:JsonProperty("codigo_do_produto") val codigo_do_produto: String,
    @field:JsonProperty("descricao") val descricao: String,
    @field:JsonProperty("ncm") val ncm: String,
    @field:JsonProperty("preco_compra") val preco_compra: String,
    @field:JsonProperty("preco_venda") val preco_venda: String,
    @field:JsonProperty("unidade") val unidade: String,
    @field:JsonProperty("marca") val marca: String,
    @field:JsonProperty("local_de_estoque") val local_de_estoque: String,
    @field:JsonProperty("grupo") val grupo: String,
) {

    constructor() : this("", "", "", "", "", "", "", "", "")

}

