// Utilidades para llamadas a la API con autenticación JWT
class ApiClient {
    constructor() {
        this.baseURL = '';
    }

    // Obtener token de localStorage
    getToken() {
        return localStorage.getItem('jwt_token');
    }

    // Headers por defecto para llamadas autenticadas
    getHeaders() {
        const token = this.getToken();
        const headers = {
            'Content-Type': 'application/json',
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        return headers;
    }

    // Manejo de errores
    async handleResponse(response) {
        if (response.status === 401) {
            // Token expirado o inválido, redirigir a login
            localStorage.removeItem('jwt_token');
            window.location.reload();
            throw new Error('Sesión expirada');
        }

        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || `HTTP ${response.status}: ${response.statusText}`);
        }

        return response.json();
    }

    // GET request
    async get(endpoint) {
        try {
            const response = await fetch(`${this.baseURL}${endpoint}`, {
                method: 'GET',
                headers: this.getHeaders()
            });

            return this.handleResponse(response);
        } catch (error) {
            console.error(`GET ${endpoint} error:`, error);
            throw error;
        }
    }

    // POST request
    async post(endpoint, data = {}) {
        try {
            const response = await fetch(`${this.baseURL}${endpoint}`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(data)
            });

            return this.handleResponse(response);
        } catch (error) {
            console.error(`POST ${endpoint} error:`, error);
            throw error;
        }
    }

    // PUT request
    async put(endpoint, data = {}) {
        try {
            const response = await fetch(`${this.baseURL}${endpoint}`, {
                method: 'PUT',
                headers: this.getHeaders(),
                body: JSON.stringify(data)
            });

            return this.handleResponse(response);
        } catch (error) {
            console.error(`PUT ${endpoint} error:`, error);
            throw error;
        }
    }

    // DELETE request
    async delete(endpoint) {
        try {
            const response = await fetch(`${this.baseURL}${endpoint}`, {
                method: 'DELETE',
                headers: this.getHeaders()
            });

            return this.handleResponse(response);
        } catch (error) {
            console.error(`DELETE ${endpoint} error:`, error);
            throw error;
        }
    }

    // POST para formulario multipart (file upload)
    async postMultipart(endpoint, formData) {
        try {
            const token = this.getToken();
            const headers = {};

            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }

            const response = await fetch(`${this.baseURL}${endpoint}`, {
                method: 'POST',
                headers: headers,
                body: formData
            });

            return this.handleResponse(response);
        } catch (error) {
            console.error(`POST multipart ${endpoint} error:`, error);
            throw error;
        }
    }
}

// Instancia global del cliente API
window.apiClient = new ApiClient();

// Funciones específicas para cada entidad
window.api = {
    // Autenticación
    async login(username, password) {
        return window.apiClient.post('/api/auth/signin', {
            nombreUsuario: username,
            clave: password
        });
    },

    async getCurrentUser() {
        return window.apiClient.get('/api/auth/me');
    },

    async validateToken() {
        return window.apiClient.get('/api/auth/validate');
    },

    // Solicitantes
    async getSolicitantes() {
        return window.apiClient.get('/api/solicitantes');
    },

    async createSolicitante(data) {
        return window.apiClient.post('/api/solicitantes', data);
    },

    // Expedientes
    async getExpedientes() {
        return window.apiClient.get('/api/expedientes');
    },

    async createExpediente(data) {
        return window.apiClient.post('/api/expedientes', data);
    },

    async updateExpediente(id, data) {
        return window.apiClient.put(`/api/expedientes/${id}`, data);
    },

    async deleteExpediente(id) {
        return window.apiClient.delete(`/api/expedientes/${id}`);
    },

    // Sesiones
    async getSesiones(idExpediente) {
        return window.apiClient.get(`/api/sesiones/expediente/${idExpediente}`);
    },

    async createSesion(data) {
        return window.apiClient.post('/api/sesiones', data);
    },

    // Documentos
    async uploadDocument(file, description) {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('description', description);

        return window.apiClient.postMultipart('/api/documentos/upload', formData);
    },

    // Usuarios
    async getUsers() {
        return window.apiClient.get('/api/usuarios');
    },

    async createUser(data) {
        return window.apiClient.post('/api/usuarios', data);
    }
};