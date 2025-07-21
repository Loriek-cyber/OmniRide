// Gestisci-tratte JavaScript cleaned up for new design

// Conferma eliminazione per i pulsanti di eliminazione
document.addEventListener('DOMContentLoaded', () => {
    // Add confirmation for action buttons that might need it
    const deleteButtons = document.querySelectorAll('button[name="action"][value="delete"]');
    deleteButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            if (!confirm('Sei sicuro di voler eliminare questa tratta? L\'azione è irreversibile.')) {
                e.preventDefault();
            }
        });
    });
    
    // Add confirmation for deactivation buttons
    const deactivateButtons = document.querySelectorAll('button[name="action"][value="disattiva"]');
    deactivateButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            if (!confirm('Sei sicuro di voler disattivare questa tratta?')) {
                e.preventDefault();
            }
        });
    });
});
