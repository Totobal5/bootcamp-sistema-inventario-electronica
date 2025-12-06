/**
 * API Client - Funciones para consumir la API REST
 */

const API_BASE_URL = '';  // Mismo dominio (Spring Boot + Thymeleaf)

/**
 * Obtener el token JWT del localStorage
 */
function getToken() {
    return localStorage.getItem('token');
}

/**
 * Verificar si el usuario está autenticado
 */
function checkAuth() {
    const token = getToken();
    if (!token) {
        window.location.href = '/login';
        return false;
    }
    return true;
}

/**
 * Cerrar sesión
 */
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    window.location.href = '/login';
}

/**
 * GET request
 */
async function apiGet(endpoint) {
    const response = await fetch(API_BASE_URL + endpoint, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    });

    if (response.status === 401) {
        logout();
        throw new Error('No autorizado');
    }

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Error en la petición');
    }

    return response.json();
}

/**
 * POST request
 */
async function apiPost(endpoint, data) {
    const response = await fetch(API_BASE_URL + endpoint, {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });

    if (response.status === 401) {
        logout();
        throw new Error('No autorizado');
    }

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Error en la petición');
    }

    return response.json();
}

/**
 * PUT request
 */
async function apiPut(endpoint, data) {
    const response = await fetch(API_BASE_URL + endpoint, {
        method: 'PUT',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });

    if (response.status === 401) {
        logout();
        throw new Error('No autorizado');
    }

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Error en la petición');
    }

    return response.json();
}

/**
 * DELETE request
 */
async function apiDelete(endpoint) {
    const response = await fetch(API_BASE_URL + endpoint, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    });

    if (response.status === 401) {
        logout();
        throw new Error('No autorizado');
    }

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Error en la petición');
    }

    // DELETE puede retornar 204 No Content
    if (response.status === 204) {
        return true;
    }

    return response.json();
}

/**
 * POST con archivo (multipart/form-data)
 */
async function apiPostFile(endpoint, file, additionalData = {}) {
    const formData = new FormData();
    formData.append('file', file);
    
    // Agregar datos adicionales
    Object.keys(additionalData).forEach(key => {
        formData.append(key, additionalData[key]);
    });

    const response = await fetch(API_BASE_URL + endpoint, {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + getToken()
            // No incluir Content-Type, el navegador lo establece automáticamente con boundary
        },
        body: formData
    });

    if (response.status === 401) {
        logout();
        throw new Error('No autorizado');
    }

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Error al subir archivo');
    }

    return response.json();
}

/**
 * Mostrar notificación temporal
 */
function showNotification(message, type = 'success') {
    // Remover notificaciones anteriores
    const existing = document.querySelector('.notification');
    if (existing) {
        existing.remove();
    }

    // Crear nueva notificación
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    document.body.appendChild(notification);

    // Remover después de 3 segundos
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

/**
 * Formatear fecha
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Formatear moneda
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CL', {
        style: 'currency',
        currency: 'CLP'
    }).format(amount);
}

/**
 * Obtener badge de estado
 */
function getEstadoBadge(estado) {
    const badges = {
        'PENDIENTE': 'badge-warning',
        'EN_PROCESO': 'badge-info',
        'COMPLETADO': 'badge-success',
        'SOLICITADO': 'badge-warning',
        'APROBADO': 'badge-success',
        'RECHAZADO': 'badge-danger',
        'PAUSADO': 'badge-secondary'
    };
    
    return badges[estado] || 'badge-secondary';
}

/**
 * Obtener ícono de categoría
 */
function getCategoriaIcon(categoria) {
    const icons = {
        'RESISTENCIA': '🔴',
        'CONDENSADOR': '🔵',
        'TRANSISTOR': '🔺',
        'DIODO': '🔻',
        'LED': '💡',
        'CIRCUITO_INTEGRADO': '🎛️',
        'CONECTORES': '🔌',
        'OTROS': '📦'
    };
    
    return icons[categoria] || '📦';
}

/**
 * Debounce function para búsquedas
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Validar formulario
 */
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    return form.checkValidity();
}

/**
 * Limpiar formulario
 */
function clearForm(formId) {
    const form = document.getElementById(formId);
    if (form) {
        form.reset();
    }
}
