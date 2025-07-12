<%@ page contentType="text/html;charset=UTF-8" %>

<footer class="footer">
    <div class="footer-content">
        <div class="footer-section footer-left">
            <ul>
                <li><a href="${pageContext.request.contextPath}/about.jsp">Chi Siamo</a></li>
                <li><a href="${pageContext.request.contextPath}/contact.jsp">Contattaci</a></li>
                <li><a href="${pageContext.request.contextPath}/terms.jsp">Termini e Condizioni</a></li>
                <li><a href="${pageContext.request.contextPath}/privacy.jsp">Informativa sulla privacy/Cookies</a></li>
            </ul>
        </div>
        <div class="footer-section footer-right">
            <div class="registration-accordion">
                <button class="accordion-btn" onclick="toggleRegistrationPanel()">
                    <span class="accordion-text">Scopri il Tuo Piano Ideale</span>
                    <span class="accordion-icon">+</span>
                </button>
                <div class="accordion-panel" id="registrationPanel">
                    <div class="registration-intro">
                        <h3>Scegli il piano perfetto per le tue esigenze</h3>
                        <p>OmniRide offre soluzioni personalizzate sia per viaggiatori individuali che per aziende. Scopri tutti i vantaggi!</p>
                    </div>
                    <div class="registration-options">
                        <div class="registration-option">
                            <div class="option-header">
                                <h4>PIANO UTENTE</h4>
                                <span class="option-badge">Popolare</span>
                            </div>
                            <div class="option-price">Gratuito</div>
                            <ul>
                                <li>✓ Prenotazione biglietti in tempo reale</li>
                                <li>✓ Gestione completa del profilo</li>
                                <li>✓ Storico viaggi e preferenze</li>
                                <li>✓ Notifiche personalizzate</li>
                                <li>✓ Supporto clienti dedicato</li>
                                <li>✓ App mobile gratuita</li>
                            </ul>
                            <a href="${pageContext.request.contextPath}/register" class="register-btn">Registrati Gratis</a>
                        </div>
                        <div class="registration-option business">
                            <div class="option-header">
                                <h4>PIANO BUSINESS</h4>
                                <span class="option-badge premium">Premium</span>
                            </div>
                            <div class="option-price">Su Misura</div>
                            <ul>
                                <li>✓ Tutte le funzionalità del Piano Utente</li>
                                <li>✓ Dashboard aziendale avanzata</li>
                                <li>✓ Gestione centralizzata dipendenti</li>
                                <li>✓ Reportistica e analytics dettagliate</li>
                                <li>✓ Fatturazione centralizzata</li>
                                <li>✓ API dedicate per integrazione</li>
                                <li class="premium-feature">Supporto premium dedicato <span class="tooltip" data-tooltip="Accesso prioritario al supporto 24/7, account manager dedicato, consulenze personalizzate e servizi su misura per la tua azienda.">?</span></li>
                            </ul>
                            <a href="${pageContext.request.contextPath}/register/registerAzienda.jsp" class="register-btn premium">Richiedi Demo</a>
                        </div>
                    </div>
                    <div class="registration-footer">
                        <p>Hai dubbi su quale piano scegliere? <a href="${pageContext.request.contextPath}/contact.jsp">Contattaci</a> per una consulenza gratuita!</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="footer-bottom">
        <div class="footer-company-info">
            <div class="company-logo">
                <h3>OmniRide</h3>
                <p class="tagline">Il futuro della mobilità smart</p>
            </div>
            <div class="company-details">
                <p><strong>Sede Legale:</strong> Via Roma 123, 84121 Salerno (SA), Italia</p>
                <p><strong>Partita IVA:</strong> IT12345678901 • <strong>REA:</strong> SA-123456</p>
                <p><strong>Capitale Sociale:</strong> € 100.000,00 i.v.</p>
            </div>
        </div>
        <div class="footer-copyright">
            <p class="copyright-main">Copyright © 2025 <strong>OmniRide Limited</strong> e le sue società affiliate. Tutti i diritti riservati.</p>
            <p class="copyright-sub">OmniRide Limited è registrata presso il Registro delle Imprese di Salerno, Italia.</p>
            <p id="info_owner" class="owners">Proprietà privata di <strong>Arjel Buzi</strong> e <strong>Daniele Capentieri</strong></p>
            <p class="compliance">Conforme alle normative GDPR • Servizio clienti attivo 24/7 • Piattaforma certificata ISO 27001</p>
        </div>
    </div>
</footer>

<script>
function toggleRegistrationPanel() {
    const panel = document.getElementById('registrationPanel');
    const icon = document.querySelector('.accordion-icon');
    
    if (panel.style.display === 'none' || panel.style.display === '') {
        panel.style.display = 'block';
        icon.textContent = '-';
    } else {
        panel.style.display = 'none';
        icon.textContent = '+';
    }
}
</script>
