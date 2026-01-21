document.addEventListener("DOMContentLoaded", async () => {
  console.log('DOM loaded - script.js');

  // --- ELEMENTOS DEL DOM ---
  const tableBody = document.querySelector("#tableExpedientes tbody");
  const tableRecientesBody = document.querySelector("#tableRecientes tbody");
  const tableSolicitantesBody = document.querySelector("#tableSolicitantes tbody");
  const btnGenReport = document.getElementById("btnGenReport");
  const reportChartCtx = document.getElementById("reportChart")?.getContext("2d");

  // Nuevos elementos
  const btnAdjuntarDoc = document.getElementById("btnAdjuntarDoc");
  const modalAdjuntar = document.getElementById("modalAdjuntarDoc") ? new bootstrap.Modal(document.getElementById("modalAdjuntarDoc")) : null;

  // --- DATOS SIMULADOS (serán reemplazados por datos del backend) ---
  const expedientes = [
    { id: "001", solicitante: "Juan Pérez", tipo: "Conciliación", fecha: "2024-09-28", prioridad: "Alta", estado: "Pendiente" },
    { id: "002", solicitante: "Ana García", tipo: "Patrocinio Legal", fecha: "2024-09-27", prioridad: "Media", estado: "En progreso" },
    { id: "003", solicitante: "Luis Torres", tipo: "Conciliación", fecha: "2024-09-27", prioridad: "Baja", estado: "Completado" },
    { id: "004", solicitante: "Maria Rodriguez", tipo: "Capacitación", fecha: "2024-09-26", prioridad: "Media", estado: "En progreso" },
    { id: "005", solicitante: "Carlos Sanchez", tipo: "Conciliación", fecha: "2024-09-25", prioridad: "Alta", estado: "Pendiente" },
  ];

  let reportChart = null;

  // --- FUNCIONES ---

  // Renderizar el gráfico de reportes con datos reales
  const renderReportChart = async () => {
    console.log('renderReportChart llamado');
    console.log('reportChartCtx:', reportChartCtx);

    if (!reportChartCtx) {
      console.log('No se encontró el canvas reportChart');
      return;
    }

    // Evita renderizar si ya existe un gráfico
    if (reportChart) {
      console.log('Destruyendo gráfico anterior');
      reportChart.destroy();
    }

    try {
      console.log('Obteniendo datos de /reportes/estadisticas...');
      const response = await fetch('/reportes/estadisticas');
      console.log('Response status:', response.status);

      if (!response.ok) {
        throw new Error('Error al obtener estadísticas');
      }

      const tipos = await response.json();
      console.log('Datos recibidos:', tipos);

      // Verificar si hay datos
      if (Object.keys(tipos).length === 0) {
        console.log('No hay datos de expedientes');
        reportChartCtx.font = '16px Arial';
        reportChartCtx.fillStyle = '#fff';
        reportChartCtx.textAlign = 'center';
        reportChartCtx.fillText('No hay datos disponibles', reportChartCtx.canvas.width / 2, reportChartCtx.canvas.height / 2);
        return;
      }

      const data = {
        labels: Object.keys(tipos),
        datasets: [{
          label: 'Número de Expedientes',
          data: Object.values(tipos),
          backgroundColor: [
            'rgba(42, 157, 143, 0.6)',
            'rgba(233, 196, 106, 0.6)',
            'rgba(231, 111, 81, 0.6)',
            'rgba(54, 162, 235, 0.6)',
            'rgba(255, 99, 132, 0.6)',
          ],
          borderColor: [
            'rgba(42, 157, 143, 1)',
            'rgba(233, 196, 106, 1)',
            'rgba(231, 111, 81, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 99, 132, 1)',
          ],
          borderWidth: 1
        }]
      };

      console.log('Creando gráfico con datos:', data);
      reportChart = new Chart(reportChartCtx, {
        type: 'bar',
        data: data,
        options: {
          scales: {
            y: {
              beginAtZero: true,
              ticks: { color: '#fff', stepSize: 1 }
            },
            x: {
              ticks: { color: '#fff' }
            }
          },
          plugins: {
            legend: {
              labels: {
                color: '#fff'
              }
            }
          },
          responsive: true,
          maintainAspectRatio: false
        }
      });
      console.log('Gráfico creado exitosamente');

    } catch (error) {
      console.error('Error al cargar estadísticas:', error);
      reportChartCtx.font = '16px Arial';
      reportChartCtx.fillStyle = '#ff6b6b';
      reportChartCtx.textAlign = 'center';
      reportChartCtx.fillText('Error al cargar datos', reportChartCtx.canvas.width / 2, reportChartCtx.canvas.height / 2);
    }
  };

  // Renderizar la tabla de expedientes principal
  const renderTable = () => {
    if (!tableBody) return;
    tableBody.innerHTML = "";
    expedientes.forEach(exp => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${exp.id}</td>
        <td>${exp.solicitante}</td>
        <td>${exp.tipo}</td>
        <td>${exp.fecha}</td>
        <td><span class="badge bg-${exp.prioridad === 'Alta' ? 'danger' : exp.prioridad === 'Media' ? 'warning' : 'secondary'}">${exp.prioridad}</span></td>
        <td><span class="badge bg-${exp.estado === 'Pendiente' ? 'warning' : exp.estado === 'En progreso' ? 'info' : 'success'}">${exp.estado}</span></td>
        <td>
          <a href="/expedientes/editar/${exp.id}" class="btn btn-sm btn-outline-light btn-edit-exp" data-id="${exp.id}"><i class="fa fa-pencil"></i> Editar</a>
        </td>
      `;
      tableBody.appendChild(row);
    });
  };

  // Renderizar la tabla de expedientes del dashboard
  const renderDashboardTable = () => {
    if (!tableRecientesBody) return;
    tableRecientesBody.innerHTML = "";
    expedientes.slice(0, 5).forEach(exp => {
      const row = document.createElement("tr");
      row.innerHTML = `
            <td>${exp.id}</td>
            <td>${exp.solicitante}</td>
            <td>${exp.tipo}</td>
            <td>${exp.fecha}</td>
            <td><span class="badge bg-${exp.prioridad === 'Alta' ? 'danger' : 'warning'}">${exp.prioridad}</span></td>
            <td><span class="badge bg-${exp.estado === 'Pendiente' ? 'warning' : 'info'}">${exp.estado}</span></td>
        `;
      tableRecientesBody.appendChild(row);
    });
  };

  // Generar PDF
  const generatePdfReport = () => {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();

    doc.text("Reporte de Expedientes - IINCADE 4.0", 14, 20);
    doc.autoTable({
      startY: 30,
      head: [['ID', 'Solicitante', 'Tipo', 'Fecha', 'Prioridad', 'Estado']],
      body: expedientes.map(exp => [exp.id, exp.solicitante, exp.tipo, exp.fecha, exp.prioridad, exp.estado]),
      theme: 'grid',
      headStyles: { fillColor: [42, 157, 143] }
    });

    doc.save('reporte-expedientes.pdf');
  };

  // --- INICIALIZACIÓN ---
  console.log('Verificando secciones...');
  console.log('section-dashboard:', document.querySelector("#section-dashboard"));
  console.log('section-expedientes:', document.querySelector("#section-expedientes"));
  console.log('section-reportes:', document.querySelector("#section-reportes"));

  // Lógica de inicialización para cada página
  if (document.querySelector("#section-dashboard")) {
    renderDashboardTable();
  }
  if (document.querySelector("#section-expedientes")) {
    renderTable();
  }

  if (document.querySelector("#section-reportes")) {
    console.log('Sección de reportes detectada, llamando a renderReportChart...');
    await renderReportChart();
  }

  if (btnGenReport) {
    btnGenReport.addEventListener("click", generatePdfReport);
  }
  if (btnAdjuntarDoc && modalAdjuntar) {
    btnAdjuntarDoc.addEventListener("click", () => {
      modalAdjuntar.show();
    });
  }

});