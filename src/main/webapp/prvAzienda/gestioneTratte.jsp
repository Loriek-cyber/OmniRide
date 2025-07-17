<%@ page import="model.sdata.Tratta" %>
<%@ page import="java.util.List" %><%
    List<Tratta> tratte = (List<Tratta>) request.getAttribute("tratte");
%>


<div class="controller-tratte">
  <h1>Gestione Tratte</h1>
  <%--
      Allora questa pagina sara molto semplice con la gestione delle azienda, non sara con il context quindi ovviamente è molto lenza,
      ma visto che ovviamente non posso avere caricate tutte le tratte all'interno della context, poiche sarebbe ripetivo in maniera catastrofica,
      semplicemente vedo le tratte che sono dell'azienda e do i dati
  --%>
  <%for (Tratta tratta:tratte){%>
    <%if(tratta.getAttiva()){%>
      <div class="tratta-body">
        <h2><%=tratta.getNome()%></h2>
        <a href="/deactivate?id=<%=tratta.getId()%>">Dissattiva</a>
      </div>
  <%}else{%>
      <div class="tratta-body">
        <h2><%=tratta.getNome()%></h2>
        <a href="/deactivate?id=<%=tratta.getId()%>">Attiva</a>
      </div>
      <%}%>
  <%}%>
</div>