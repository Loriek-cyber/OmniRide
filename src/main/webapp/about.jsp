<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Chi Siamo - Omniride</title>
    <%@ include file="import/metadata.jsp"%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        .about-container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 2rem;
            background: white;
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            position: relative;
        }
        
        .about-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #32cd32, #228b22, #32cd32);
            border-radius: 20px 20px 0 0;
        }
        
        .about-header {
            text-align: center;
            margin-bottom: 3rem;
            padding-top: 1rem;
        }
        
        .about-header h1 {
            color: #1f2937;
            font-size: 2.5rem;
            margin-bottom: 1rem;
            font-weight: 700;
        }
        
        .about-content {
            line-height: 1.6;
            color: #4a5568;
        }
        
        .about-content h2 {
            color: #1f2937;
            font-size: 1.5rem;
            margin: 2rem 0 1rem 0;
            font-weight: 600;
            padding-left: 1rem;
            border-left: 4px solid #32cd32;
        }
        
        .about-content p {
            margin-bottom: 1rem;
            font-size: 1.1rem;
        }
        
        .about-content ul {
            margin: 1rem 0;
            padding-left: 0;
            list-style: none;
        }
        
        .about-content li {
            margin: 0.75rem 0;
            padding: 1rem;
            background: #f8f9fa;
            border-radius: 8px;
            border-left: 4px solid #32cd32;
            transition: all 0.3s ease;
        }
        
        .about-content li:hover {
            background: #e8f5e8;
            transform: translateX(5px);
        }
        
        .services-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 2rem;
            margin: 2rem 0;
        }
        
        .service-item {
            background: #f8f9fa;
            border: 2px solid #e5e7eb;
            border-radius: 16px;
            padding: 2rem;
            text-align: center;
            transition: all 0.3s ease;
        }
        
        .service-item:hover {
            border-color: #32cd32;
            box-shadow: 0 8px 25px rgba(50, 205, 50, 0.15);
            transform: translateY(-5px);
        }
        
        .service-item h3 {
            color: #32cd32;
            font-size: 1.3rem;
            margin-bottom: 1rem;
            font-weight: 600;
        }
        
        .service-item p {
            color: #6b7280;
            margin: 0;
        }
        
        @media (max-width: 768px) {
            .about-container {
                margin: 1rem;
                padding: 1rem;
            }
            
            .about-header h1 {
                font-size: 2rem;
            }
            
            .services-grid {
                grid-template-columns: 1fr;
                gap: 1rem;
            }
        }
    </style>
</head>
<body>
<%@ include file="import/header.jsp"%>

<main>
    <div class="about-container">
        <div class="about-header">
            <h1><i class="fas fa-info-circle"></i> Chi Siamo</h1>
        </div>
        <div class="about-content">
            <p>Benvenuti in <strong>OmniRide</strong>, la piattaforma di trasporti innovativa che sta rivoluzionando il modo di viaggiare in Italia. 
            Nata dalla passione per la tecnologia e l'esperienza di viaggio, OmniRide rappresenta il futuro della mobilità urbana e interurbana.</p>
            
            <h2>La Nostra Storia</h2>
            <p>Fondata con l'obiettivo di semplificare e migliorare l'esperienza di viaggio, OmniRide nasce dall'esigenza di offrire 
            una soluzione completa e integrata per tutti i mezzi di trasporto. Dalla prenotazione di biglietti ferroviari 
            alla gestione di servizi aziendali, la nostra piattaforma è progettata per soddisfare ogni esigenza di mobilità.</p>
            
            <h2>La Nostra Missione</h2>
            <p>Connettere persone, luoghi e opportunità attraverso servizi di trasporto affidabili e tecnologia all'avanguardia. 
            Vogliamo rendere ogni viaggio un'esperienza semplice, sicura e sostenibile, facilitando l'accesso a soluzioni 
            di mobilità intelligenti per privati e aziende.</p>
            
            <h2>I Nostri Valori</h2>
            <ul>
                <li><strong>Eccellenza:</strong> Garantiamo standard qualitativi elevati in ogni servizio offerto</li>
                <li><strong>Innovazione:</strong> Investiamo costantemente nelle migliori tecnologie per migliorare l'esperienza utente</li>
                <li><strong>Affidabilità:</strong> Assicuriamo servizi puntuali, sicuri e sempre disponibili</li>
                <li><strong>Sostenibilità:</strong> Promuoviamo un trasporto eco-friendly e responsabile verso l'ambiente</li>
                <li><strong>Customer-centricity:</strong> Il cliente è al centro di ogni nostra decisione e innovazione</li>
            </ul>
            
            <h2>I Nostri Servizi</h2>
            <div class="services-grid">
                <div class="service-item">
                    <h3>Per Privati</h3>
                    <p>Prenotazione semplice e veloce di biglietti per treni, autobus e altri mezzi di trasporto. 
                    Gestione completa dei tuoi viaggi in un'unica app.</p>
                </div>
                <div class="service-item">
                    <h3>Per Aziende</h3>
                    <p>Soluzioni avanzate per la gestione delle trasferte aziendali, reportistica dettagliata 
                    e supporto dedicato per ottimizzare i costi di trasporto.</p>
                </div>
            </div>
            
            <h2>Perché Scegliere OmniRide</h2>
            <ul>
                <li><strong>Piattaforma Unica:</strong> Tutti i servizi di trasporto in un'unica soluzione integrata</li>
                <li><strong>Tecnologia Avanzata:</strong> App e website intuitivi, sicuri e sempre aggiornati</li>
                <li><strong>Supporto 24/7:</strong> Assistenza clienti sempre disponibile per risolvere ogni esigenza</li>
                <li><strong>Prezzi Competitivi:</strong> Tariffe trasparenti e vantaggi esclusivi per i nostri utenti</li>
                <li><strong>Esperienza Personalizzata:</strong> Servizi su misura per ogni tipo di viaggiatore</li>
            </ul>
            
            <h2>Il Futuro con OmniRide</h2>
            <p>Continuiamo a crescere e innovare, espandendo la nostra rete di partner e sviluppando nuove funzionalità 
            per rendere il viaggio sempre più smart e sostenibile. Unisciti a noi in questo viaggio verso il futuro della mobilità!</p>
        </div>
    </div>
</main>

<%@ include file="import/footer.jsp"%>
</body>
</html>
