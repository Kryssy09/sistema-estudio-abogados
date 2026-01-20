document.addEventListener('DOMContentLoaded', function () {
  console.log('Documentos.js cargado correctamente');

  // Referencias DOM
  const inputBuscar = document.getElementById('buscadorDocumentos');
  const btnBuscar = document.getElementById('btnBuscarDocumentos');
  const btnUpload = document.getElementById('btnUploadDoc');
  const uploadModal = document.getElementById('uploadModal');
  const uploadForm = uploadModal ? uploadModal.querySelector('form') : null;
  const btnSubir = uploadModal ? uploadModal.querySelector('.btn-primary') : null;

  console.log('Elementos encontrados:', {
    inputBuscar: !!inputBuscar,
    btnBuscar: !!btnBuscar,
    btnUpload: !!btnUpload,
    uploadModal: !!uploadModal,
    uploadForm: !!uploadForm,
    btnSubir: !!btnSubir
  });

  // CSRF
  const metaToken = document.querySelector('meta[name="_csrf"]');
  const metaHeader = document.querySelector('meta[name="_csrf_header"]');
  const csrfToken = metaToken ? metaToken.content : null;
  const csrfHeader = metaHeader ? metaHeader.content : null;

  let modalInstance = null;
  let searchTimeout = null;
  let originalContent = {
    solicitud: null,
    conciliacion: null
  };

  // ============================================================
  // BÚSQUEDA PREDICTIVA
  // ============================================================

  function guardarContenidoOriginal() {
    if (!originalContent.solicitud) {
      const solicitudDiv = document.querySelector('.list-group.list-group-flush.mb-4');
      const conciliacionDiv = document.querySelectorAll('.list-group.list-group-flush')[1];

      if (solicitudDiv) originalContent.solicitud = solicitudDiv.innerHTML;
      if (conciliacionDiv) originalContent.conciliacion = conciliacionDiv.innerHTML;
    }
  }

  function restaurarContenidoOriginal() {
    if (originalContent.solicitud) {
      const solicitudDiv = document.querySelector('.list-group.list-group-flush.mb-4');
      if (solicitudDiv) solicitudDiv.innerHTML = originalContent.solicitud;
    }
    if (originalContent.conciliacion) {
      const conciliacionDiv = document.querySelectorAll('.list-group.list-group-flush')[1];
      if (conciliacionDiv) conciliacionDiv.innerHTML = originalContent.conciliacion;
    }
  }

  function mostrarResultadosBusqueda(resultados) {
    const solicitudDiv = document.querySelector('.list-group.list-group-flush.mb-4');
    const conciliacionDiv = document.querySelectorAll('.list-group.list-group-flush')[1];

    if (!solicitudDiv || !conciliacionDiv) return;

    // Filtrar resultados por carpeta
    const resultadosSolicitud = resultados.filter(r => r.rutaRelativa.startsWith('formato de solicitud'));
    const resultadosConciliacion = resultados.filter(r => r.rutaRelativa.startsWith('formato de conciliacion'));

    // Actualizar sección de Solicitud
    if (resultadosSolicitud.length > 0) {
      solicitudDiv.innerHTML = resultadosSolicitud.map(archivo => crearItemArchivo(archivo, 'formato de solicitud')).join('');
    } else {
      solicitudDiv.innerHTML = `
                <div class="text-center text-muted py-3">
                    <i class="fa fa-search fa-2x mb-2 d-block"></i>
                    <p class="mb-0">No se encontraron resultados</p>
                </div>
            `;
    }

    // Actualizar sección de Conciliación
    if (resultadosConciliacion.length > 0) {
      conciliacionDiv.innerHTML = resultadosConciliacion.map(archivo => crearItemArchivo(archivo, 'formato de conciliacion')).join('');
    } else {
      conciliacionDiv.innerHTML = `
                <div class="text-center text-muted py-3">
                    <i class="fa fa-search fa-2x mb-2 d-block"></i>
                    <p class="mb-0">No se encontraron resultados</p>
                </div>
            `;
    }
  }

  function crearItemArchivo(archivo, carpeta) {
    const iconClass = getIconClass(archivo.extension);
    return `
            <a href="/documentos/descargar?carpeta=${encodeURIComponent(carpeta)}&archivo=${encodeURIComponent(archivo.nombre)}"
               class="list-group-item list-group-item-action bg-dark text-light border-secondary mb-2">
                <div class="d-flex align-items-center">
                    <i class="${iconClass} me-3 fa-lg"></i>
                    <div class="flex-grow-1">
                        <h6 class="mb-0">${archivo.nombreSinExtension}</h6>
                        <small class="text-muted">${archivo.extension.toUpperCase()}</small>
                    </div>
                    <i class="fa fa-download text-muted"></i>
                </div>
            </a>
        `;
  }

  function getIconClass(extension) {
    const ext = extension.toLowerCase();
    if (ext === 'pdf') return 'fa fa-file-pdf text-danger';
    if (ext === 'docx' || ext === 'doc') return 'fa fa-file-word text-primary';
    if (ext === 'xlsx' || ext === 'xls') return 'fa fa-file-excel text-success';
    return 'fa fa-file text-secondary';
  }

  async function buscarArchivos(query) {
    if (!query || query.trim().length === 0) {
      restaurarContenidoOriginal();
      return;
    }

    try {
      const res = await fetch(`/documentos/buscar?q=${encodeURIComponent(query)}`);
      if (!res.ok) throw new Error('Error en la búsqueda');

      const resultados = await res.json();
      mostrarResultadosBusqueda(resultados);

    } catch (err) {
      console.error('Error al buscar:', err);
      showToast('Error al buscar documentos', 'error');
    }
  }

  // Event listener para búsqueda en tiempo real
  if (inputBuscar) {
    guardarContenidoOriginal();

    inputBuscar.addEventListener('input', function () {
      const query = this.value.trim();

      // Cancelar búsqueda anterior
      if (searchTimeout) {
        clearTimeout(searchTimeout);
      }

      // Buscar después de 300ms de inactividad
      searchTimeout = setTimeout(() => {
        buscarArchivos(query);
      }, 300);
    });

    // También buscar al hacer clic en el botón
    if (btnBuscar) {
      btnBuscar.addEventListener('click', function () {
        const query = inputBuscar.value.trim();
        buscarArchivos(query);
      });
    }
  }

  // ============================================================
  // SUBIR DOCUMENTO
  // ============================================================

  if (btnUpload && uploadModal) {
    console.log('Configurando evento click para btnUpload');
    btnUpload.addEventListener('click', function () {
      console.log('Click en botón Subir detectado');
      try {
        if (!modalInstance) {
          console.log('Creando nueva instancia de modal');
          modalInstance = new bootstrap.Modal(uploadModal);
        }
        if (uploadForm) {
          console.log('Reseteando formulario');
          uploadForm.reset();
        }
        console.log('Mostrando modal');
        modalInstance.show();
      } catch (error) {
        console.error('Error al abrir modal:', error);
        alert('Error al abrir el modal: ' + error.message);
      }
    });
    console.log('Evento click configurado correctamente');
  } else {
    console.error('No se pudo configurar el botón de subir:', {
      btnUpload: !!btnUpload,
      uploadModal: !!uploadModal
    });
  }

  if (btnSubir && uploadForm) {
    console.log('Configurando evento click para btnSubir (botón dentro del modal)');
    btnSubir.addEventListener('click', async function () {
      console.log('Click en botón Subir (dentro del modal)');
      const formData = new FormData();
      const fileInput = uploadForm.querySelector('input[type="file"]');
      const carpetaSelect = uploadForm.querySelector('select');

      console.log('Elementos del formulario:', {
        fileInput: !!fileInput,
        carpetaSelect: !!carpetaSelect,
        hasFiles: fileInput && fileInput.files && fileInput.files.length > 0,
        carpetaValue: carpetaSelect ? carpetaSelect.value : 'N/A'
      });

      if (!fileInput || !fileInput.files || fileInput.files.length === 0) {
        showToast('Por favor seleccione un archivo', 'warning');
        return;
      }

      if (!carpetaSelect || !carpetaSelect.value) {
        showToast('Por favor seleccione una carpeta', 'warning');
        return;
      }

      const file = fileInput.files[0];
      const carpeta = carpetaSelect.value;

      console.log('Preparando subida:', {
        fileName: file.name,
        fileSize: file.size,
        carpeta: carpeta
      });

      formData.append('file', file);
      formData.append('carpeta', carpeta);

      const headers = {};
      if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
      }

      try {
        btnSubir.disabled = true;
        btnSubir.innerHTML = '<i class="fa fa-spinner fa-spin me-1"></i> Subiendo...';

        console.log('Enviando petición a /documentos/subir-formato');
        const res = await fetch('/documentos/subir-formato', {
          method: 'POST',
          headers: headers,
          body: formData
        });

        const message = await res.text();
        console.log('Respuesta del servidor:', {
          status: res.status,
          message: message
        });

        if (res.status === 409) {
          showToast(message, 'warning');
        } else if (!res.ok) {
          throw new Error(message);
        } else {
          showToast('Archivo subido exitosamente', 'success');
          if (modalInstance) modalInstance.hide();

          // Recargar página para mostrar nuevo archivo
          setTimeout(() => {
            window.location.reload();
          }, 1000);
        }

      } catch (err) {
        console.error('Error al subir:', err);
        showToast('Error al subir el archivo: ' + err.message, 'error');
      } finally {
        btnSubir.disabled = false;
        btnSubir.innerHTML = '<i class="fa fa-upload me-1"></i> Subir';
      }
    });
  } else {
    console.error('No se pudo configurar el botón de subir del modal:', {
      btnSubir: !!btnSubir,
      uploadForm: !!uploadForm
    });
  }

  // ============================================================
  // UTILIDADES
  // ============================================================

  function showToast(message, type = 'info') {
    console.log('showToast llamado:', message, type);
    // Si existe la función global showToast, usarla
    if (typeof window.showToast === 'function') {
      window.showToast(message, type);
    } else {
      // Fallback a alert
      alert(message);
    }
  }

  console.log('Inicialización de documentos.js completada');
});
