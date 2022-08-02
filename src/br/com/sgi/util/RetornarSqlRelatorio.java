/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.util;

/**
 *
 * @author jairo.silva
 */
public class RetornarSqlRelatorio {

    public RetornarSqlRelatorio() {

    }

    public static String retornar(String opc, String parametro) {
        String sql = "";

        if (opc.equals("GRP")) {
            sql = "SELECT SUC.USU_CODCLI CLIENTE,\n"
                    + "       SUC.USU_AUTMOT AUTO_MOTO,\n"
                    + "       CLI.CODGRE     CODGRE,\n"
                    + "       CLI.NOMCLI     AS NOME_CLIENTE,\n"
                    + "       SUC.USU_DATGER EMISSAO,\n"
                    + "       SUC.USU_PESFAT PESO_FATURADO,\n"
                    + "       SUC.USU_PESREC PESO_RECEBIDO,\n"
                    + "       SUC.USU_PESMOV PESO_CREDIT0_MANUAL,\n"
                    + "       SUC.USU_PESAJU PESO_DEBITO_MANUAL, "
                    + "       SUC.USU_DEBCRE AS DEBITO,\n"
                    + "       GRE.NOMGRE     AS NOME_GRUPO,\n"
                    + "       CASE CLI.CODGRE WHEN\n"
                    + "         0 THEN 'NÃO INFORMADO'\n"
                    + "       ELSE\n"
                    + "         GRE.NOMGRE\n"
                    + "       END AS GRUPO,\n"
                    + "SUC.USU_CODLAN AS LANCAMENTO,\n"
                    + "SUC.USU_SEQMOV AS SEQUENCIA\n"
                    + "  FROM USU_TSUCMOV SUC, E085CLI CLI\n"
                    + "  LEFT JOIN E069GRE GRE\n"
                    + "    ON GRE.CODGRE = CLI.CODGRE\n"
                    + " WHERE CLI.CODCLI = SUC.USU_CODCLI\n"
                    + "   AND SUC.USU_DEBCRE IN ('3 - DEBITO', '4 - CREDITO')\n"
                    + "   AND USU_AUTMOT IN ('AUT', 'MOT')\n"
                    + "  AND CLI.CODGRE =" + parametro + ""
                    + " ORDER BY  CLI.CODGRE,  CLI.CODCLI, SUC.USU_DATGER";
        } else {
            if (opc.equals("CLI")) {
                sql = "SELECT SUC.USU_CODCLI CLIENTE,\n"
                        + "       SUC.USU_AUTMOT AUTO_MOTO,\n"
                        + "       CLI.CODGRE     CODGRE,\n"
                        + "       CLI.NOMCLI     AS NOME_CLIENTE,\n"
                        + "       SUC.USU_DATGER EMISSAO,\n"
                        + "       SUC.USU_PESFAT  PESO_FATURADO,\n"
                        + "       SUC.USU_PESMOV PESO_CREDIT0_MANUAL,\n"
                        + "       SUC.USU_PESAJU PESO_DEBITO_MANUAL, "
                        + "       SUC.USU_PESREC PESO_RECEBIDO,\n"
                        + "       SUC.USU_DEBCRE AS DEBITO,\n"
                        + "       GRE.NOMGRE     AS NOME_GRUPO,\n"
                        + "       CASE CLI.CODGRE WHEN\n"
                        + "         0 THEN 'NÃO INFORMADO'\n"
                        + "       ELSE\n"
                        + "         GRE.NOMGRE\n"
                        + "       END AS GRUPO,\n"
                        + "SUC.USU_CODLAN AS LANCAMENTO,\n"
                        + "SUC.USU_SEQMOV AS SEQUENCIA\n"
                        + "  FROM USU_TSUCMOV SUC, E085CLI CLI\n"
                        + "  LEFT JOIN E069GRE GRE\n"
                        + "    ON GRE.CODGRE = CLI.CODGRE\n"
                        + " WHERE CLI.CODCLI = SUC.USU_CODCLI\n"
                        + "   AND SUC.USU_DEBCRE IN ('3 - DEBITO', '4 - CREDITO')\n"
                        + "   AND USU_AUTMOT IN ('AUT', 'MOT')\n"
                        + "   AND CLI.CODCLI = " + parametro + ""
                        + " ORDER BY  CLI.CODGRE,  CLI.CODCLI, SUC.USU_DATGER";
            }
            if (opc.equals("ALL")) {
                sql = "SELECT SUC.USU_CODCLI CLIENTE,\n"
                        + "       SUC.USU_AUTMOT AUTO_MOTO,\n"
                        + "       CLI.CODGRE     CODGRE,\n"
                        + "       CLI.NOMCLI     AS NOME_CLIENTE,\n"
                        + "       SUC.USU_DATGER EMISSAO,\n"
                        + "       SUC.USU_PESFAT  PESO_FATURADO,\n"
                        + "       SUC.USU_PESMOV PESO_CREDIT0_MANUAL,\n"
                        + "       SUC.USU_PESAJU PESO_DEBITO_MANUAL, "
                        + "       SUC.USU_PESREC PESO_RECEBIDO,\n"
                        + "       SUC.USU_DEBCRE AS DEBITO,\n"
                        + "       GRE.NOMGRE     AS NOME_GRUPO,\n"
                        + "       CASE CLI.CODGRE WHEN\n"
                        + "         0 THEN 'NÃO INFORMADO'\n"
                        + "       ELSE\n"
                        + "         GRE.NOMGRE\n"
                        + "       END AS GRUPO,\n"
                        + "SUC.USU_CODLAN AS LANCAMENTO,\n"
                        + "SUC.USU_SEQMOV AS SEQUENCIA\n"
                        + "  FROM USU_TSUCMOV SUC, E085CLI CLI\n"
                        + "  LEFT JOIN E069GRE GRE\n"
                        + "    ON GRE.CODGRE = CLI.CODGRE\n"
                        + " WHERE CLI.CODCLI = SUC.USU_CODCLI\n"
                        + "   AND SUC.USU_DEBCRE IN ('3 - DEBITO', '4 - CREDITO')\n"
                        + "   AND USU_AUTMOT IN ('AUT', 'MOT')\n"
                        + " ORDER BY  CLI.CODGRE,  CLI.CODCLI, SUC.USU_DATGER";
            }
        }

        return sql;
    }

}
