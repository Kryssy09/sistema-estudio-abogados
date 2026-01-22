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
});
