package br.com.sgi.interfaces;

import br.com.sgi.bean.PedidoReport;

import java.sql.SQLException;
import java.util.List;

public interface InterfacePedidoReportDAO<T> {

    List<PedidoReport> getPedidoReports(String PESQUISA, String PESQUISA_POR) throws SQLException;

}
