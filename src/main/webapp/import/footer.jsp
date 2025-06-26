<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // Get current year for copyright
    java.util.Calendar cal = java.util.Calendar.getInstance();
    int currentYear = cal.get(java.util.Calendar.YEAR);
%>

<footer class="main-footer">
    <div class="footer-container">
        <!-- Footer Top Section -->
        <div class="footer-top">
            <div class="footer-section company-info">
                <div class="footer-logo">
                    <img src="Images/logo.png" alt="Omniride Logo" class="footer-logo-img">
                    <h3>Omniride</h3>
                </div>
                <p class="company-description">
                    La tua piattaforma di fiducia per servizi di trasporto e biglietti online. 
                    Viaggia comodamente e in sicurezza con Omniride.
                </p>
                <div class="social-links">
                    <a href="#" class="social-link facebook" aria-label="Facebook">
                        <i class="social-icon">📘</i>
                    </a>
                    <a href="#" class="social-link twitter" aria-label="Twitter">
                        <i class="social-icon">🐦</i>
                    </a>
                    <a href="#" class="social-link instagram" aria-label="Instagram">
                        <i class="social-icon">📷</i>
                    </a>
                    <a href="#" class="social-link linkedin" aria-label="LinkedIn">
                        <i class="social-icon">💼</i>
                    </a>
                </div>
            </div>

            <div class="footer-section quick-links">
                <h4>Link Rapidi</h4>
                <ul class="footer-links">
                    <li><a href="index.jsp">Home</a></li>
                    <li><a href="products">Prodotti</a></li>
                    <li><a href="biglietti.jsp">Biglietti</a></li>
                    <li><a href="about.jsp">Chi Siamo</a></li>
                    <li><a href="contact.jsp">Contatti</a></li>
                </ul>
            </div>

            <div class="footer-section customer-service">
                <h4>Servizio Clienti</h4>
                <ul class="footer-links">
                    <li><a href="help.jsp">Centro Assistenza</a></li>
                    <li><a href="faq.jsp">FAQ</a></li>
                    <li><a href="terms.jsp">Termini di Servizio</a></li>
                    <li><a href="privacy.jsp">Privacy Policy</a></li>
                    <li><a href="refund.jsp">Rimborsi</a></li>
                </ul>
            </div>

            <div class="footer-section contact-info">
                <h4>Contatti</h4>
                <div class="contact-details">
                    <div class="contact-item">
                        <i class="contact-icon">📧</i>
                        <a href="mailto:info@omniride.com">info@omniride.com</a>
                    </div>
                    <div class="contact-item">
                        <i class="contact-icon">📞</i>
                        <a href="tel:+390123456789">+39 012 345 6789</a>
                    </div>
                    <div class="contact-item">
                        <i class="contact-icon">📍</i>
                        <span>Via Roma 123, 00100 Roma, Italia</span>
                    </div>
                    <div class="contact-item">
                        <i class="contact-icon">🕒</i>
                        <span>Lun-Ven: 9:00-18:00</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Newsletter Signup -->
        <div class="footer-newsletter">
            <div class="newsletter-content">
                <h4>Resta aggiornato</h4>
                <p>Iscriviti alla nostra newsletter per ricevere offerte esclusive e aggiornamenti</p>
                <form class="newsletter-form" id="newsletterForm">
                    <input type="email" placeholder="Inserisci la tua email" required class="newsletter-input">
                    <button type="submit" class="newsletter-btn">Iscriviti</button>
                </form>
            </div>
        </div>

        <!-- Footer Bottom -->
        <div class="footer-bottom">
            <div class="footer-bottom-content">
                <div class="copyright">
                    <p>&copy; <%= currentYear %> Omniride. Tutti i diritti riservati.</p>
                </div>
                <div class="footer-bottom-links">
                    <a href="sitemap.jsp">Mappa del Sito</a>
                    <span class="separator">|</span>
                    <a href="accessibility.jsp">Accessibilità</a>
                    <span class="separator">|</span>
                    <a href="cookies.jsp">Cookie Policy</a>
                </div>
                <div class="payment-methods">
                    <span class="payment-text">Metodi di pagamento:</span>
                    <div class="payment-icons">
                        <i class="payment-icon">💳</i>
                        <i class="payment-icon">🏦</i>
                        <i class="payment-icon">📱</i>
                    </div>
                </div>
            </div>
        </div>
    </div>
</footer>

<script>
// Newsletter form handling
document.addEventListener('DOMContentLoaded', function() {
    const newsletterForm = document.getElementById('newsletterForm');

    if (newsletterForm) {
        newsletterForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const emailInput = this.querySelector('.newsletter-input');
            const email = emailInput.value.trim();

            if (email && isValidEmail(email)) {
                // Here you would typically send the email to your server
                alert('Grazie per esserti iscritto alla nostra newsletter!');
                emailInput.value = '';
            } else {
                alert('Per favore inserisci un indirizzo email valido.');
            }
        });
    }

    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
});
</script>
