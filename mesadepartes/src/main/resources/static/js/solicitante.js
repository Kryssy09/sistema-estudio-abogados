document.addEventListener('DOMContentLoaded', () => {
  console.log('DOMContentLoaded fired in solicitante.js');

  // Referencias al DOM
  const form = document.getElementById('solicitanteForm');
  const modalEl = document.getElementById('solicitanteModal');
  const modalTitle = document.getElementById('solicitanteModalTitle');
  const btnGuardar = document.getElementById('btnGuardarSolicitante');
  const btnNuevo = document.getElementById('btnAddSolicitante');
  const btnBuscar = document.getElementById('btnBuscarSolicitantes');
  const btnLimpiar = document.getElementById('btnLimpiarBusqueda');
  const inputBuscar = document.getElementById('buscadorSolicitantes');
  const tbody = document.querySelector('#tableSolicitantes tbody');

  // Lazy-load modal instance
  let modalInstance = null;
  function getModal() {
    if (!modalInstance && modalEl) {
      modalInstance = new bootstrap.Modal(modalEl);
    }
    return modalInstance;
  }

  // URLs
  const URL_BASE = '/solicitantes';
  const URL_LISTAR = `${URL_BASE}/listar-todos`;
  const URL_GUARDAR = `${URL_BASE}/guardar`;
  const URL_BUSCAR = `${URL_BASE}/buscar`;

  // CSRF
  const metaToken = document.querySelector('meta[name="_csrf"]');
  const metaHeader = document.querySelector('meta[name="_csrf_header"]');
  const csrfToken = metaToken ? metaToken.content : null;
  const csrfHeader = metaHeader ? metaHeader.content : null;

  // Estado local
  let isEdit = false;

  // ============================================================
  // FUNCIONES PRINCIPALES
  // ============================================================

  // Cargar solicitantes
  async function cargarSolicitantes() {
    console.log("Cargar solicitantes called");
    try {
      tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-muted py-4">
                        <i class="fa fa-spinner fa-spin fa-2x mb-3 d-block"></i>
                        Cargando solicitantes...
                    </td>
                </tr>
            `;

      console.log("Fetching from:", URL_LISTAR);
      const res = await fetch(URL_LISTAR);
      console.log("Response status:", res.status);

      if (!res.ok) throw new Error('Error al listar solicitantes');
      const lista = await res.json();
      console.log("Data received:", lista);
      await renderTabla(lista);
    } catch (err) {
      console.error(err);
      tbody.innerHTML = `<tr><td colspan="8" class="text-center text-danger">Error al cargar datos</td></tr>`;
    }
  }

  // Renderizar tabla
  async function renderTabla(lista) {
    if (!Array.isArray(lista) || lista.length === 0) {
      tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-muted py-4">
                        <i class="fa fa-users fa-3x mb-3 d-block"></i>
                        No hay solicitantes registrados
                    </td>
                </tr>
            `;
      return;
    }

    // Cargar conteo de expedientes para cada solicitante
    const solicitantesConConteo = await Promise.all(lista.map(async (sol) => {
      try {
        const res = await fetch(`${URL_BASE}/${sol.idSolicitante}/expedientes/count`);
        const count = res.ok ? await res.json() : 0;
        return { ...sol, cantidadExpedientes: count };
      } catch (err) {
        console.error(`Error al obtener conteo para solicitante ${sol.idSolicitante}:`, err);
        return { ...sol, cantidadExpedientes: 0 };
      }
    }));

    tbody.innerHTML = solicitantesConConteo.map(sol => {
      const persona = sol.persona || {};
      const nombreCompleto = `${persona.nombres || ''} ${persona.apellidoPaterno || ''} ${persona.apellidoMaterno || ''}`.trim();

      return `
                <tr>
                    <td>${sol.idSolicitante}</td>
                    <td>${nombreCompleto}</td>
                    <td>${persona.numeroDocumento || ''}</td>
                    <td>${persona.correoElectronico || ''}</td>
                    <td>${persona.telefonoPersonal || ''}</td>
                    <td>${sol.fechaCreacion ? new Date(sol.fechaCreacion).toLocaleDateString() : ''}</td>
                    <td class="text-center">
                        <span class="badge bg-info">${sol.cantidadExpedientes || 0}</span>
                    </td>
                    <td class="text-center">
                        <div class="btn-group" role="group">
                            <button class="btn btn-sm btn-outline-warning btn-editar" data-id="${sol.idSolicitante}" title="Editar">
                                <i class="fa fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger btn-eliminar" data-id="${sol.idSolicitante}" title="Eliminar">
                                <i class="fa fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `;
    }).join('');

    // Asignar eventos a los botones generados
    document.querySelectorAll('.btn-editar').forEach(btn => {
      btn.addEventListener('click', () => editarSolicitante(btn.dataset.id));
    });
    document.querySelectorAll('.btn-eliminar').forEach(btn => {
      btn.addEventListener('click', () => eliminarSolicitante(btn.dataset.id));
    });
  }

  // Buscar solicitantes
  async function buscarSolicitantes() {
    const query = inputBuscar.value.trim();
    if (!query) {
      cargarSolicitantes();
      return;
    }

    try {
      const res = await fetch(`${URL_BUSCAR}?q=${encodeURIComponent(query)}`);
      if (!res.ok) throw new Error('Error en la búsqueda');
      const lista = await res.json();
      await renderTabla(lista);
    } catch (err) {
      console.error(err);
      alert('Error al buscar solicitantes');
    }
  }

  // Abrir modal para nuevo solicitante
  function abrirModalNuevo() {
    isEdit = false;
    if (modalTitle) modalTitle.textContent = 'Nuevo Solicitante';
    form.reset();
    const solicitanteIdInput = document.getElementById('solicitanteId');
    if (solicitanteIdInput) solicitanteIdInput.value = '';
    getModal().show();
  }

  // Abrir modal para editar
  async function editarSolicitante(id) {
    try {
      console.log('Editando solicitante ID:', id);
      const res = await fetch(`${URL_BASE}/${id}`);
      if (!res.ok) throw new Error('Error al obtener solicitante');
      const solicitante = await res.json();
      console.log('Solicitante recibido:', solicitante);
      const persona = solicitante.persona;
      console.log('Persona:', persona);

      isEdit = true;
      if (modalTitle) modalTitle.textContent = 'Editar Solicitante';

      // Verificar que todos los elementos existen
      const elements = {
        solicitanteId: document.getElementById('solicitanteId'),
        tipoDocumento: document.getElementById('tipoDocumento'),
        numeroDocumento: document.getElementById('numeroDocumento'),
        nombres: document.getElementById('nombres'),
        apellidoPaterno: document.getElementById('apellidoPaterno'),
        apellidoMaterno: document.getElementById('apellidoMaterno'),
        email: document.getElementById('email'),
        telefono: document.getElementById('telefono'),
        sexo: document.getElementById('sexo')
      };

      // Log de elementos que no existen
      Object.keys(elements).forEach(key => {
        if (!elements[key]) {
          console.error(`Elemento no encontrado: ${key}`);
        }
      });

      // Llenar formulario solo si los elementos existen
      if (elements.solicitanteId) elements.solicitanteId.value = solicitante.idSolicitante;
      if (elements.tipoDocumento) elements.tipoDocumento.value = persona.tipoDocumentoIdentidad || 1;
      if (elements.numeroDocumento) elements.numeroDocumento.value = persona.numeroDocumento || '';
      if (elements.nombres) elements.nombres.value = persona.nombres || '';
      if (elements.apellidoPaterno) elements.apellidoPaterno.value = persona.apellidoPaterno || '';
      if (elements.apellidoMaterno) elements.apellidoMaterno.value = persona.apellidoMaterno || '';
      if (elements.email) elements.email.value = persona.correoElectronico || '';
      if (elements.telefono) elements.telefono.value = persona.telefonoPersonal || '';
      if (elements.sexo) elements.sexo.value = persona.sexo || 1;

      console.log('Formulario llenado, mostrando modal');
      getModal().show();
    } catch (err) {
      console.error('Error en editarSolicitante:', err);
      alert('Error al cargar los datos del solicitante');
    }
  }

  // Guardar solicitante (Crear o Editar)
  async function guardarSolicitante() {
    if (!form.checkValidity()) {
      form.reportValidity();
      return;
    }

    const payload = {
      tipoDocumentoIdentidad: parseInt(document.getElementById('tipoDocumento').value),
      numeroDocumento: document.getElementById('numeroDocumento').value,
      nombres: document.getElementById('nombres').value,
      apellidoPaterno: document.getElementById('apellidoPaterno').value,
      apellidoMaterno: document.getElementById('apellidoMaterno').value,
      correoElectronico: document.getElementById('email').value,
      telefonoPersonal: document.getElementById('telefono').value,
      sexo: parseInt(document.getElementById('sexo').value)
    };

    const headers = { 'Content-Type': 'application/json' };
    if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;

    try {
      const res = await fetch(URL_GUARDAR, {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error('Error al guardar');

      alert(isEdit ? 'Solicitante actualizado correctamente' : 'Solicitante creado correctamente');
      getModal().hide();
      cargarSolicitantes();
    } catch (err) {
      console.error(err);
      alert('Error al guardar el solicitante');
    }
  }

  // Eliminar solicitante
  async function eliminarSolicitante(id) {
    if (!confirm('¿Está seguro de eliminar este solicitante? Esta acción no se puede deshacer.')) {
      return;
    }

    const headers = {};
    if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;

    try {
      const res = await fetch(`${URL_BASE}/${id}`, {
        method: 'DELETE',
        headers: headers
      });

      if (res.status === 409) {
        const message = await res.text();
        alert(message);
        return;
      }

      if (!res.ok) throw new Error('Error al eliminar');

      alert('Solicitante eliminado correctamente');
      cargarSolicitantes();
    } catch (err) {
      console.error(err);
      alert('Error al eliminar el solicitante');
    }
  }

  // ============================================================
  // EVENT LISTENERS
  // ============================================================

  // Botón Nuevo
  if (btnNuevo) {
    btnNuevo.addEventListener('click', abrirModalNuevo);
  }

  // Botón Guardar
  if (btnGuardar) {
    btnGuardar.addEventListener('click', guardarSolicitante);
  }

  // Botón Buscar
  if (btnBuscar) {
    btnBuscar.addEventListener('click', buscarSolicitantes);
  }

  // Input Buscar (Enter)
  if (inputBuscar) {
    inputBuscar.addEventListener('keyup', (e) => {
      if (e.key === 'Enter') buscarSolicitantes();
    });
  }

  // Botón Limpiar
  if (btnLimpiar) {
    btnLimpiar.addEventListener('click', () => {
      inputBuscar.value = '';
      cargarSolicitantes();
    });
  }

  // Inicializar
  console.log('About to call cargarSolicitantes');
  cargarSolicitantes();
});
