document.addEventListener('DOMContentLoaded', function () {
    // CSRF (Spring Security)
    const metaToken = document.querySelector('meta[name="_csrf"]');
    const metaHeader = document.querySelector('meta[name="_csrf_header"]');
    const csrfToken = metaToken ? metaToken.content : null;
    const csrfHeader = metaHeader ? metaHeader.content : null;

    // Modal para ver sesión
    var verSesionModal = document.getElementById('verSesionModal');
    if (verSesionModal) {
        verSesionModal.addEventListener('show.bs.modal', function (event) {
            try {
                var button = event.relatedTarget;
                if (!button) {
                    console.warn('No se encontró el botón que activó el modal');
                    return;
                }

                // Obtener los atributos data con manejo seguro
                var nroSesion = button.getAttribute('data-nrosesion') || 'N/A';
                var fecha = button.getAttribute('data-fecha') || 'N/A';
                var estado = button.getAttribute('data-estado') || 'N/A';
                var resumen = button.getAttribute('data-resumen') || 'Sin resumen';

                // Decodificar entidades HTML si las hay
                resumen = resumen.replace(/&quot;/g, '"')
                    .replace(/&#39;/g, "'")
                    .replace(/&lt;/g, '<')
                    .replace(/&gt;/g, '>')
                    .replace(/&amp;/g, '&');

                console.log('Datos del modal:', { nroSesion, fecha, estado, resumen });

                // Obtener los elementos del modal de forma segura
                var modalNroSesion = verSesionModal.querySelector('#verNroSesion');
                var modalFecha = verSesionModal.querySelector('#verFechaProgramada');
                var modalEstado = verSesionModal.querySelector('#verEstadoSesion');
                var modalResumen = verSesionModal.querySelector('#verResumenSesion');

                // Asignar valores con validación
                if (modalNroSesion) modalNroSesion.textContent = nroSesion;
                if (modalFecha) modalFecha.textContent = fecha;
                if (modalEstado) modalEstado.textContent = estado;
                if (modalResumen) modalResumen.textContent = resumen;

                // Verificar que todos los elementos se llenaron correctamente
                if (!modalNroSesion || !modalFecha || !modalEstado || !modalResumen) {
                    console.error('No se encontraron todos los elementos del modal');
                }
            } catch (error) {
                console.error('Error al mostrar el modal:', error);
                alert('Error al mostrar los detalles de la sesión');
                // Forzar cierre del modal en caso de error
                setTimeout(function () {
                    var modalInstance = bootstrap.Modal.getInstance(verSesionModal);
                    if (modalInstance) {
                        modalInstance.hide();
                    }
                }, 100);
            }
        });

        // Evento para cerrar modal con Escape
        verSesionModal.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') {
                var modalInstance = bootstrap.Modal.getInstance(verSesionModal);
                if (modalInstance) {
                    modalInstance.hide();
                }
            }
        });

        // Evento para asegurar que el modal se cierre correctamente al hacer clic fuera
        verSesionModal.addEventListener('click', function (event) {
            if (event.target === verSesionModal) {
                var modalInstance = bootstrap.Modal.getInstance(verSesionModal);
                if (modalInstance) {
                    modalInstance.hide();
                }
            }
        });
    }

    // Función para eliminar sesión
    window.deleteSesion = async function (button) {
        const sesionId = button.getAttribute('data-id');
        if (!sesionId) {
            alert('No se pudo encontrar el ID de la sesión.');
            return;
        }

        if (confirm('¿Está seguro de que desea eliminar esta sesión?')) {
            const url = `/expedientes/sesion/delete/${sesionId}`;
            const headers = {
                'Content-Type': 'application/json'
            };
            if (csrfHeader && csrfToken) {
                headers[csrfHeader] = csrfToken;
            }

            try {
                const response = await fetch(url, {
                    method: 'DELETE',
                    headers: headers
                });

                if (response.ok) {
                    alert('Sesión eliminada correctamente.');
                    // Eliminar la fila de la tabla
                    button.closest('tr').remove();
                } else {
                    try {
                        const error = await response.json();
                        alert('Error al eliminar la sesión: ' + (error.message || error));
                    } catch (e) {
                        const errorText = await response.text();
                        alert('Error al eliminar la sesión: ' + errorText);
                    }
                }
            } catch (error) {
                console.error('Error en la petición de eliminación:', error);
                alert('Ocurrió un error al intentar eliminar la sesión.');
            }
        }
    };
    // ============================================================
    // LÓGICA PARA BUSCAR SOLICITANTE
    // ============================================================
    const btnBuscarSolicitante = document.getElementById('btnBuscarSolicitante');
    const inputBuscar = document.getElementById('inputBuscarSolicitante');
    const tablaResultados = document.querySelector('#tablaResultadosSolicitantes tbody');
    const modalBuscar = new bootstrap.Modal(document.getElementById('buscarSolicitanteModal'));

    if (btnBuscarSolicitante) {
        btnBuscarSolicitante.addEventListener('click', function () {
            const query = inputBuscar.value;
            if (!query) {
                alert("Ingrese un término de búsqueda");
                return;
            }

            fetch(`/solicitantes/buscar?q=${encodeURIComponent(query)}`)
                .then(response => response.json())
                .then(data => {
                    tablaResultados.innerHTML = '';
                    if (data.length === 0) {
                        tablaResultados.innerHTML = '<tr><td colspan="3" class="text-center">No se encontraron resultados</td></tr>';
                        return;
                    }
                    data.forEach(sol => {
                        const nombreCompleto = `${sol.persona.nombres} ${sol.persona.apellidoPaterno} ${sol.persona.apellidoMaterno}`;
                        const tr = document.createElement('tr');
                        tr.innerHTML = `
                            <td>${sol.persona.numeroDocumento}</td>
                            <td>${nombreCompleto}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-success btn-seleccionar"
                                    data-id="${sol.idSolicitante}"
                                    data-nombre="${nombreCompleto}">
                                    Seleccionar
                                </button>
                            </td>
                        `;
                        tablaResultados.appendChild(tr);
                    });

                    // Asignar eventos a los botones de seleccionar
                    document.querySelectorAll('.btn-seleccionar').forEach(btn => {
                        btn.addEventListener('click', function () {
                            const id = this.getAttribute('data-id');
                            const nombre = this.getAttribute('data-nombre');

                            document.getElementById('solicitanteId').value = id;
                            document.getElementById('solicitanteNombre').value = nombre;

                            modalBuscar.hide();
                        });
                    });
                })
                .catch(err => console.error(err));
        });
    }

    // ============================================================
    // LÓGICA PARA CREAR NUEVO SOLICITANTE
    // ============================================================
    const btnGuardarSolicitante = document.getElementById('btnGuardarSolicitante');
    const modalCrearElement = document.getElementById('crearSolicitanteModal');
    const modalCrear = new bootstrap.Modal(modalCrearElement);

    if (btnGuardarSolicitante) {
        btnGuardarSolicitante.addEventListener('click', function () {
            const form = document.getElementById('formCrearSolicitante');
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }

            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());

            // Estructura esperada por el backend (Persona)
            const payload = {
                tipoDocumento: data.tipoDocumento,
                numeroDocumento: data.numeroDocumento,
                nombres: data.nombres,
                apellidoPaterno: data.apellidoPaterno,
                apellidoMaterno: data.apellidoMaterno,
                email: data.email,
                telefono: data.telefono,
                sexo: data.sexo
            };

            const headers = {
                'Content-Type': 'application/json'
            };
            if (csrfHeader && csrfToken) {
                headers[csrfHeader] = csrfToken;
            }

            fetch('/solicitantes/guardar', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(payload)
            })
                .then(response => {
                    if (!response.ok) throw new Error('Error al guardar');
                    return response.json();
                })
                .then(solicitante => {
                    alert("Solicitante creado correctamente");

                    // Seleccionar automáticamente
                    const nombreCompleto = `${solicitante.persona.nombres} ${solicitante.persona.apellidoPaterno} ${solicitante.persona.apellidoMaterno}`;
                    document.getElementById('solicitanteId').value = solicitante.idSolicitante;
                    document.getElementById('solicitanteNombre').value = nombreCompleto;

                    modalCrear.hide();
                    form.reset();
                })
                .catch(err => {
                    console.error(err);
                    alert("Error al guardar el solicitante. Verifique los datos.");
                });
        });
    }

    // ============================================================
    // LÓGICA PARA ACTUALIZACIÓN EN TIEMPO REAL (POLLING)
    // ============================================================
    const tablaBody = document.getElementById('tablaExpedientesBody');
    if (tablaBody) {
        // Ejecutar cada 15 segundos
        setInterval(() => {
            if (document.querySelector('.modal.show')) return; // No refrescar si hay un modal abierto

            const currentUrl = new URL(window.location.href);
            // Mantiene los parámetros actuales (paginación, ordenamiento, búsqueda)
            const fetchUrl = currentUrl.origin + '/expedientes/fragmento-tabla' + currentUrl.search;

            fetch(fetchUrl)
                .then(response => {
                    if (response.ok) return response.text();
                    throw new Error('Network response was not ok');
                })
                .then(html => {
                    // Actualiza solo el tbody
                    tablaBody.innerHTML = html;
                })
                .catch(error => console.error('Error fetching expedientes updates:', error));
        }, 15000);
    }

    // ============================================================
    // LÓGICA PARA CAMBIO DE ESTADO (HU-17) E HISTORIAL (HU-O5)
    // ============================================================

    // Event Delegation para botones en la tabla (ya que se recarga con polling)
    document.body.addEventListener('click', function (e) {

        // 1. Botón Cambiar Estado
        const btnEstado = e.target.closest('.btn-cambiar-estado');
        if (btnEstado) {
            const id = btnEstado.getAttribute('data-id');
            const estadoActual = btnEstado.getAttribute('data-estado');
            const espacialistaActual = btnEstado.getAttribute('data-especialista');

            document.getElementById('cmbEstadoExpedienteId').value = id;
            document.getElementById('cmbNuevoEstado').value = estadoActual || '';
            document.getElementById('cmbNuevoEspecialista').value = espacialistaActual || '';
            document.getElementById('txtObservaciones').value = '';

            const modal = new bootstrap.Modal(document.getElementById('modalCambiarEstado'));
            modal.show();
        }

        // 2. Botón Ver Historial
        const btnHistorial = e.target.closest('.btn-ver-historial');
        if (btnHistorial) {
            const id = btnHistorial.getAttribute('data-id');
            const contenedor = document.getElementById('contenedorHistorial');

            // Mostrar modal con spinner
            contenedor.innerHTML = `<div class="text-center py-5">
                                        <div class="spinner-border text-primary" role="status">
                                            <span class="visually-hidden">Cargando...</span>
                                        </div>
                                        <p class="mt-2 text-muted">Cargando línea de tiempo...</p>
                                    </div>`;
            const modal = new bootstrap.Modal(document.getElementById('modalVerHistorial'));
            modal.show();

            // Cargar fragmento HTML por AJAX
            fetch(`/expedientes/${id}/historial`)
                .then(response => response.text())
                .then(html => {
                    contenedor.innerHTML = html;
                })
                .catch(error => {
                    console.error('Error cargando historial:', error);
                    contenedor.innerHTML = `<div class="alert alert-danger">Error cargando el historial. Por favor, intente de nuevo.</div>`;
                });
        }

        // 3. Botón Cerrar Expediente
        const btnCerrar = e.target.closest('.btn-cerrar-expediente');
        if (btnCerrar) {
            const id = btnCerrar.getAttribute('data-id');
            document.getElementById('cerrarExpedienteId').value = id;
            document.getElementById('txtMotivoCierre').value = '';

            const modal = new bootstrap.Modal(document.getElementById('modalCerrarExpediente'));
            modal.show();
        }

        // 4. Botón Derivar Expediente
        const btnDerivar = e.target.closest('.btn-derivar-expediente');
        if (btnDerivar) {
            const id = btnDerivar.getAttribute('data-id');
            const asignadoId = btnDerivar.getAttribute('data-asignado');
            document.getElementById('derivarExpedienteId').value = id;
            document.getElementById('txtComentarioDerivacion').value = '';

            // Pre-seleccionar el abogado actualmente asignado si existe
            const cmbAbogado = document.getElementById('cmbAbogadoDerivacion');
            if (cmbAbogado && asignadoId) {
                cmbAbogado.value = asignadoId;
            } else if (cmbAbogado) {
                cmbAbogado.value = '';
            }

            const modal = new bootstrap.Modal(document.getElementById('modalDerivarExpediente'));
            modal.show();
        }
    });

    // Guardar Cambio de Estado
    const btnGuardarCambio = document.getElementById('btnGuardarCambioEstado');
    if (btnGuardarCambio) {
        btnGuardarCambio.addEventListener('click', function () {
            const form = document.getElementById('formCambiarEstado');
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }

            const formData = new FormData(form);
            const id = formData.get('expedienteId');

            // Generar payload en formato application/x-www-form-urlencoded
            const params = new URLSearchParams();
            params.append('estado', formData.get('estado'));
            const idEspecialista = formData.get('idEspecialista');
            if (idEspecialista) {
                params.append('idEspecialista', idEspecialista);
            }
            const obs = formData.get('observaciones');
            if (obs) {
                params.append('observaciones', obs);
            }

            // Headers con CSRF si existe
            const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
            const csrfMeta = document.querySelector('meta[name="_csrf"]');
            const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
            if (csrfMeta && csrfHeaderMeta) {
                headers[csrfHeaderMeta.content] = csrfMeta.content;
            }

            // Deshabilitar botón
            const originalText = btnGuardarCambio.innerHTML;
            btnGuardarCambio.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Guardando...';
            btnGuardarCambio.disabled = true;

            fetch(`/expedientes/${id}/actualizar-estado`, {
                method: 'POST',
                headers: headers,
                body: params
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Cerrar modal
                        bootstrap.Modal.getInstance(document.getElementById('modalCambiarEstado')).hide();
                        alert("Estado actualizado correctamente");
                        // Recargar url actual para ver los cambios
                        window.location.reload();
                    } else {
                        alert("Error: " + data.message);
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("Ocurrió un error al intentar actualizar el estado.");
                })
                .finally(() => {
                    btnGuardarCambio.innerHTML = originalText;
                    btnGuardarCambio.disabled = false;
                });
        });
    }

    // Guardar Cierre de Expediente
    const btnConfirmarCierre = document.getElementById('btnConfirmarCierre');
    if (btnConfirmarCierre) {
        btnConfirmarCierre.addEventListener('click', function () {
            const form = document.getElementById('formCerrarExpediente');
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }

            const formData = new FormData(form);
            const id = formData.get('expedienteId');

            const params = new URLSearchParams();
            params.append('motivo', formData.get('motivoCierre'));

            // Headers con CSRF si existe
            const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
            const csrfMeta = document.querySelector('meta[name="_csrf"]');
            const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
            if (csrfMeta && csrfHeaderMeta) {
                headers[csrfHeaderMeta.content] = csrfMeta.content;
            }

            const originalText = btnConfirmarCierre.innerHTML;
            btnConfirmarCierre.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Cerrando...';
            btnConfirmarCierre.disabled = true;

            fetch(`/expedientes/${id}/cerrar`, {
                method: 'POST',
                headers: headers,
                body: params
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        bootstrap.Modal.getInstance(document.getElementById('modalCerrarExpediente')).hide();
                        alert("Expediente cerrado exitosamente. Se ha notificado al cliente.");
                        window.location.reload();
                    } else {
                        alert("Error: " + data.message);
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("Ocurrió un error al intentar cerrar el expediente.");
                })
                .finally(() => {
                    btnConfirmarCierre.innerHTML = originalText;
                    btnConfirmarCierre.disabled = false;
                });
        });
    }
});

// Confirmar Derivación de Expediente
const btnConfirmarDerivacion = document.getElementById('btnConfirmarDerivacion');
if (btnConfirmarDerivacion) {
    btnConfirmarDerivacion.addEventListener('click', function () {
        const form = document.getElementById('formDerivarExpediente');
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const formData = new FormData(form);
        const id = formData.get('expedienteId');

        const params = new URLSearchParams();
        params.append('idAbogado', formData.get('idAbogado'));
        params.append('comentario', formData.get('comentario'));

        const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
        const csrfMeta = document.querySelector('meta[name="_csrf"]');
        const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
        if (csrfMeta && csrfHeaderMeta) {
            headers[csrfHeaderMeta.content] = csrfMeta.content;
        }

        const originalText = btnConfirmarDerivacion.innerHTML;
        btnConfirmarDerivacion.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Derivando...';
        btnConfirmarDerivacion.disabled = true;

        fetch(`/expedientes/${id}/derivar`, {
            method: 'POST',
            headers: headers,
            body: params
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    bootstrap.Modal.getInstance(document.getElementById('modalDerivarExpediente')).hide();
                    alert(data.message);
                    window.location.reload();
                } else {
                    alert('Error: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Ocurrió un error al intentar derivar el expediente.');
            })
            .finally(() => {
                btnConfirmarDerivacion.innerHTML = originalText;
                btnConfirmarDerivacion.disabled = false;
            });
    });
}
});
