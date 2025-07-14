<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Percorsi</title>
  <style>
    .card {
      border: 1px solid #ccc;
      padding: 15px;
      margin-bottom: 10px;
      cursor: pointer;
      border-radius: 8px;
    }
    .hidden {
      display: none;
    }
    .dettagli {
      background-color: #f9f9f9;
      margin-top: 10px;
      padding: 10px;
      border-radius: 6px;
    }
  </style>
  <script>
    function toggleDetails(id) {
      const el = document.getElementById(id);
      el.classList.toggle("hidden");
    }
  </script>
</head>
<body>

<%-- Simulazione dati, da sostituire con un backend o servlet --%>
<%
  class Segmento {
    String partenza, arrivo, orarioPartenza, orarioArrivo, treno;
    Segmento(String p, String a, String op, String oa, String t) {
      partenza = p; arrivo = a; orarioPartenza = op; orarioArrivo = oa; treno = t;
    }
  }
  class Percorso {
    String orarioTotale, prezzo, id;
    java.util.List<Segmento> segmenti;
    Percorso(String id, String ot, String pr, java.util.List<Segmento> segs) {
      this.id = id; orarioTotale = ot; prezzo = pr; segmenti = segs;
    }
  }

  java.util.List<Percorso> percorsi = new java.util.ArrayList<>();
  java.util.List<Segmento> tratta1 = java.util.Arrays.asList(
          new Segmento("Capua", "Napoli Centrale", "06:08", "07:14", "RE 5803"),
          new Segmento("Napoli Centrale", "Salerno", "07:35", "08:15", "RE 5211")
  );
  percorsi.add(new Percorso("p1", "2h 07m", "6,50 €", tratta1));

  java.util.List<Segmento> tratta2 = java.util.Arrays.asList(
          new Segmento("Capua", "Napoli Centrale", "06:08", "07:14", "Itabus"),
          new Segmento("Napoli Centrale", "Salerno", "07:35", "08:40", "RE")
  );
  percorsi.add(new Percorso("p2", "2h 32m", "8,99 €", tratta2));

  request.setAttribute("percorsi", percorsi);
%>

<h2>Tratte disponibili</h2>

<c:forEach var="p" items="${percorsi}">
  <div class="card" onclick="toggleDetails('${p.id}')">
    <strong>Partenza: 06:08 → Arrivo: ${p.segmenti[p.segmenti.size()-1].orarioArrivo}</strong><br>
    Durata: ${p.orarioTotale}, Prezzo: ${p.prezzo}
    <div class="dettagli hidden" id="${p.id}">
      <ul>
        <c:forEach var="s" items="${p.segmenti}">
          <li>
            <strong>${s.partenza}</strong> (${s.orarioPartenza}) → <strong>${s.arrivo}</strong> (${s.orarioArrivo}) - Treno: ${s.treno}
          </li>
        </c:forEach>
      </ul>
    </div>
  </div>
</c:forEach>

</body>
</html>
