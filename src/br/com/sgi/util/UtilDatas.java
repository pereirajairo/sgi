/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 *
 * @author jairosilva
 */
public class UtilDatas {

    private long diascalculado = Long.MIN_VALUE;
    private int dia = 0;
    private int mes = 0;
    private int ano = 0;

    public static String retornarEstruturaPasta(Date datarecebida) {
        String retorno = "";

        Calendar c = GregorianCalendar.getInstance();
        c.setTime(datarecebida);
        int ano = c.get(Calendar.YEAR);
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int mes = c.get(Calendar.MONTH) + 1;

        retorno = "\\" + ano + "\\" + mes + "\\" + dia + "\\";
        return retorno;
    }

    public int retornaDia(Date datarecebida) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);
        setDia(cal.get(Calendar.DAY_OF_MONTH));
        return dia;
    }

    public int retornaMes(Date datarecebida) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);
        setMes(cal.get(Calendar.MONTH) + 1);
        return mes;
    }

    public int retornaAno(Date datarecebida) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);
        setAno(cal.get(Calendar.YEAR));
        return ano;
    }

    public final static String converteMes(int mes) {
        String mesDes = "";
        if (mes == 1) {
            mesDes = "Jan";
        } else {
            if (mes == 2) {
                mesDes = "Fec";
            } else if (mes == 3) {
                mesDes = "Mar";
            } else if (mes == 3) {
                mesDes = "Mar";
            } else if (mes == 4) {
                mesDes = "Abr";
            } else if (mes == 5) {
                mesDes = "Mai";
            } else if (mes == 6) {
                mesDes = "Jun";
            } else if (mes == 7) {
                mesDes = "Jul";
            } else if (mes == 8) {
                mesDes = "Ago";
            } else if (mes == 9) {
                mesDes = "Set";
            } else if (mes == 10) {
                mesDes = "Out";
            } else if (mes == 11) {
                mesDes = "Nov";
            } else {
                mesDes = "Dez";
            }
        }
        return mesDes;
    }

   public String dataInicioMes(Date datarecebida) {
        String data = "";
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);

        setDia(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMes(cal.get(Calendar.MONDAY) + 1);
        setAno(cal.get(Calendar.YEAR));

        System.out.println(getDia() + "/" + getMes() + "/" + getAno());
        if (this.mes < 10) {                        
            data = "01/0" + getMes() + "/" + getAno();

        } else {
            data = "01/" + getMes() + "/" + getAno();
        }        
        
        return data;
    }

    public String dataFimMes(Date datarecebida) {
        String data = "";
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);

        setDia(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMes(cal.get(Calendar.MONDAY) + 1);
        setAno(cal.get(Calendar.YEAR));

        System.out.println(getDia() + "/" + getMes() + "/" + getAno());

        if (this.mes < 10) {            
            data = getDia() + "/0" + getMes() + "/" + getAno();

        } else {
            data = getDia() + "/" + getMes() + "/" + getAno();
        }

        return data;
    }

   public String dataInicioMesVen(Date datarecebida) {
        String data = "";
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);

        int mesAnt = cal.get(Calendar.MONTH);
        if (mesAnt == 0) {
            setMes(12);
            setAno(cal.get(Calendar.YEAR) - 1);
        } else {
            setMes(cal.get(Calendar.MONTH));
            setAno(cal.get(Calendar.YEAR));
        }

//        System.out.println(getDia() + "/" + getMes() + "/" + getAno());
        if (this.mes < 10) {
            data = "27/0" + getMes() + "/" + getAno();

        } else {
            data = "27/" + getMes() + "/" + getAno();
        }

//        System.out.println(data);
        return data;
    }

    public String dataFimMesVen(Date datarecebida) {
        String data = "";
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);

        setMes(cal.get(Calendar.MONDAY) + 1);
        setAno(cal.get(Calendar.YEAR));

        if (this.mes < 10) {
            data = "26/0" + getMes() + "/" + getAno();
        } else {
            data = "26/" + getMes() + "/" + getAno();
        }
//        System.out.println(data);
        return data;
    }

    public Date converterDataHora(String hora, Date dat) {
        String dataTexto;
        //String horaTexto ; // Digamos que seja o campo da tela  
        String dataHoraTexto;

        SimpleDateFormat formatDDMMYYYY = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatFinal = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Obtem data atual (sem hora)  
        dataTexto = formatDDMMYYYY.format(dat);

        // Juntar data com hora  
        dataHoraTexto = String.format("%s %s", dataTexto, hora);
        System.err.println("Data e hora: " + hora);

        // Agora gera a data/hora como java.util.Date  
        java.util.Date dataHora = null;

        try {
            dataHora = formatFinal.parse(dataHoraTexto);
        } catch (ParseException e) {
            System.err.println("A hora informada é inválida: " + hora);

        }

        java.sql.Timestamp dataHoraParaBD = new java.sql.Timestamp(dataHora.getTime());

        return dataHora;
    }

    public String retornarHoras(Date hora) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
        String horaFormatada = sdf.format(hora);
        return horaFormatada;
    }

    public double nrDias(Date dataFinal, Date dataInicial) {
        double result = 0;
        long diferenca = dataFinal.getTime() - dataInicial.getTime();
        double diferencaEmDias = (diferenca / 1000) / 60 / 60 / 24; //resultado é diferença entre as datas em dias
        long horasRestantes = (diferenca / 1000) / 60 / 60 % 24; //calcula as horas restantes
        result = diferencaEmDias + (horasRestantes / 24d); //transforma as horas restantes em fração de dias
        return result;

    }

    public  Date getDataRetorno(int dias) {
        Calendar mais5 = Calendar.getInstance();
        mais5.add(Calendar.DAY_OF_MONTH, dias);
        return mais5.getTime();
    }

    public Date getHojeMais5() {
        Calendar mais5 = Calendar.getInstance();
        mais5.add(Calendar.DAY_OF_MONTH, 5);
        return mais5.getTime();
    }

    public Date getHojeMais60() {
        Calendar mais = Calendar.getInstance();
        mais.add(Calendar.DAY_OF_MONTH, 60);
        return mais.getTime();
    }

    public Date getAdicionarDia(Date datger, int qtddia) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(datger); // Objeto Date() do usuário
        cal.add(cal.DAY_OF_MONTH, +qtddia);
        return cal.getTime();
    }

    public void retHor() {
        Calendar c = Calendar.getInstance();
        Date data = c.getTime();

        Locale brasil = new Locale("pt", "BR"); //Retorna do país e a língua
        Locale eua = Locale.US;
        Locale italia = Locale.ITALIAN;

        DateFormat f2 = DateFormat.getDateInstance(DateFormat.FULL, brasil);
        System.out.println("Data e hora brasileira : " + f2.format(data));

    }

    public float tranformarHoraMinuto(String horas, String minutos) {

        float hora = Float.parseFloat(horas);
        float minuto = Float.parseFloat(minutos);
        float segundo = 0;
        float minutosTrabalhado = (hora * 60) + minuto + (segundo / 60);

        System.out.println(minutosTrabalhado);

        return minutosTrabalhado;
    }

    public long qtdDias(Date datmov, Date datprg) throws ParseException {
        long diascalculado = 0;
        String datmovi = converterDateToStr(datmov);
        String datprgr = converterDateToStr(datprg);
        try {
            // constrói a primeira data
            DateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
            Date data1 = (Date) fm.parse(datmovi);

            // constrói a segunda data
            fm = new SimpleDateFormat("dd/MM/yyyy");
            Date data2 = (Date) fm.parse(datprgr);

            // vamos obter a diferença em dias
            long diff = data2.getTime()
                    - data1.getTime();

            diascalculado = data2.getTime()
                    - data1.getTime();

            diascalculado = diff / (1000 * 60 * 60 * 24);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diascalculado;

    }

    public long qtdAnos(Date datmov, Date datprg) throws ParseException {
        long diascalculado = 0;
        String datmovi = converterDateToStr(datmov);
        String datprgr = converterDateToStr(datprg);
        try {
            // constrói a primeira data
            DateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
            Date data1 = (Date) fm.parse(datmovi);

            // constrói a segunda data
            fm = new SimpleDateFormat("dd/MM/yyyy");
            Date data2 = (Date) fm.parse(datprgr);

            // vamos obter a diferença em dias
            long diff = data2.getTime()
                    - data1.getTime();

            diascalculado = data2.getTime()
                    - data1.getTime();

            diascalculado = diff / (1000 * 60 * 60 * 24);
            diascalculado = diascalculado / 365;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diascalculado;

    }

  

    public int retornaUltimoDia(Date datarecebida) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);

        setDia(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMes(cal.get(Calendar.MONDAY) + 1);
        setAno(cal.get(Calendar.YEAR));

        System.out.println(getDia() + "/" + getMes() + "/" + getAno());

        try {
            Date data = (new SimpleDateFormat("dd/MM/yyyy")).parse(getDia() + "/" + getMes() + "/" + getAno());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dia;
    }

    public Date retornaDataIniMes(Date datarecebida) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);

        setDia(cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        setMes(cal.get(Calendar.MONDAY) + 1);
        setAno(cal.get(Calendar.YEAR));
        setDia(1);

        System.out.println(getDia() + "/" + getMes() + "/" + getAno());
        Date data = null;
        try {
            data = (new SimpleDateFormat("dd/MM/yyyy")).parse(getDia() + "/" + getMes() + "/" + getAno());
        } catch (ParseException e) {
        }
        return data;
    }

    public Date retornaDataIniSucata(Date datarecebida) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);

        setDia(cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        setMes(cal.get(Calendar.MONDAY) + 1);
        setAno(cal.get(Calendar.YEAR));
        setDia(1);
        setMes(1);
        

        System.out.println(getDia() + "/" + getMes() + "/" + getAno());
        Date data = null;
        try {
            data = (new SimpleDateFormat("dd/MM/yyyy")).parse(getDia() + "/" + getMes() + "/2021");
        } catch (ParseException e) {
        }
        return data;
    }

    public Date retornaDataIni(Date datarecebida) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);

        setDia(cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        setMes(cal.get(Calendar.MONDAY) + 1);
        setAno(cal.get(Calendar.YEAR));
        setDia(1);
        setMes(1);

        System.out.println(getDia() + "/" + getMes() + "/" + getAno());
        Date data = null;
        try {
            data = (new SimpleDateFormat("dd/MM/yyyy")).parse(getDia() + "/" + getMes() + "/" + getAno());
        } catch (ParseException e) {
        }
        return data;
    }

    public Date retornaDataFim(Date datarecebida) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datarecebida);

        setDia(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMes(cal.get(Calendar.MONDAY) + 1);
        setAno(cal.get(Calendar.YEAR));

        System.out.println(getDia() + "/" + getMes() + "/" + getAno());
        Date data = null;
        try {
            data = (new SimpleDateFormat("dd/MM/yyyy")).parse(getDia() + "/" + getMes() + "/" + getAno());
        } catch (ParseException e) {
        }
        return data;
    }

    public Date converterDataddmmyyyy(String dataString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = formatter.parse(dataString);
        return date;
    }

    public static boolean analisarDataEqual(Date datprg, Date dathoj) {
        boolean retorno = false;
        if (datprg.equals(dathoj)) {
            retorno = true;
        }

        return retorno;
    }

    public boolean analisarData(Date datprg, Date dathoj) {
        boolean retorno = false;
        if (datprg.before(dathoj)) {
            retorno = true;
        }
        return retorno;
    }

    public boolean verificaDataApontamento(String dathoj, String datapo) throws ParseException {
        boolean data;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(sdf.parse(dathoj));
        cal2.setTime(sdf.parse(datapo));

        data = cal1.compareTo(cal2) == 0;

        return data;
    }

    public String converterStrtoDate(String data) throws ParseException {
        String result = null;
        result = data.trim();
        if (!result.equals("")) {
            SimpleDateFormat in = new SimpleDateFormat("yyMMdd");
            Date d = in.parse(data);
            SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy");
            result = out.format(in.parse(data.toString()));
        } else {
            result = "";
        }
        return result;
    }

    public String converterStrtoDateExc(String data) throws ParseException {
        String result = null;
        result = data.trim();
        if (!result.equals("")) {
            SimpleDateFormat in = new SimpleDateFormat("dd.MM.yy");
            Date d = in.parse(data);

            SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy");
            result = out.format(in.parse(data.toString()));
        } else {
            result = "";
        }
        return result;
    }

    public String converterDateToStr(Date data) throws ParseException {
        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat("dd/MM/yyyy");

        try {
            dateString = sdfr.format(data);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        if (dateString.equals("31/12/1900")) {
            dateString = "";
        }

        return dateString;

    }

    public String converterDateToStrYYYYMMDD(Date data) throws ParseException {
        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd");
        /*you can also use DateFormat reference instead of SimpleDateFormat 
         * like this: DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
         */
        try {
            dateString = sdfr.format(data);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return dateString;

    }

    public boolean analisarPeriodoFirme(Date datmov, Date datprg) throws ParseException {
        boolean retorno = true;

        String datmovi = converterDateToStr(datmov);
        String datprgr = converterDateToStr(datprg);
        try {
            // constrói a primeira data
            DateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
            Date data1 = (Date) fm.parse(datmovi);

            // constrói a segunda data
            fm = new SimpleDateFormat("dd/MM/yyyy");
            Date data2 = (Date) fm.parse(datprgr);

            // vamos obter a diferença em dias
            long diff = data2.getTime()
                    - data1.getTime();

            diascalculado = data2.getTime()
                    - data1.getTime();

            this.diascalculado = diff / (1000 * 60 * 60 * 24);

            if (this.diascalculado < 10) {
                retorno = false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return retorno;
    }

    /**
     * @return the diascalculado
     */
    public Long getDiascalculado() {
        return diascalculado;
    }

    /**
     * @param diascalculado the diascalculado to set
     */
    public void setDiascalculado(Long diascalculado) {
        this.diascalculado = diascalculado;
    }

    /**
     * @return the dia
     */
    public int getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(int dia) {
        this.dia = dia;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @return the ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * @param ano the ano to set
     */
    public void setAno(int ano) {
        this.ano = ano;
    }
}
