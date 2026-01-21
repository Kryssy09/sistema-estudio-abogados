# Estructura de Templates Refactorizada

## 📁 Organización

```
src/main/resources/templates/
├── layouts/
│   └── base.html                 # Layout principal
├── fragments/
│   ├── header.html               # Header/Navegación superior
│   ├── sidebar.html              # Menú lateral
│   ├── footer.html               # Footer
│   ├── breadcrumbs.html          # Breadcrumb navigation
│   ├── page-header.html          # Header de página estándar
│   ├── forms/
│   │   └── form-header.html      # Template para formularios
│   └── tables/
│       └── data-table.html       # Template para tablas de datos
├── pages/
│   └── dashboard.html            # Dashboard principal
├── expedientes/
│   ├── list.html                 # Listado de expedientes
│   └── form.html                 # Formulario crear/editar expedientes
├── usuarios/
│   ├── list.html                 # Listado de usuarios
│   └── form.html                 # Formulario crear/editar usuarios
└── areas/
    └── ...                       # Templates para áreas (mantenidos por HTMX)
```

## 🎯 Convenciones

### 1. Layout Base
- **Archivo**: `layouts/base.html`
- **Uso**: `layout:decorate="~{layouts/base}"`
- **Variables**: `activeMenu`, `pageTitle`, `breadcrumbs`

### 2. Pages (Listados)
- **Nombre**: `list.html`
- **Layout**: Usan `layout:decorate` con el layout base
- **Fragmento**: `layout:fragment="content"`
- **Header**: Usan `fragments/page-header`

### 3. Forms (Crear/Editar)
- **Nombre**: `form.html`
- **Variable**: `isEdit` (booleano para diferenciar)
- **Lógica**: Un solo template para crear y editar
- **Header**: Dinámico según `isEdit`

### 4. Fragments Reutilizables
- **Header**: `fragments/header :: header`
- **Sidebar**: `fragments/sidebar :: sidebar(${activeMenu})`
- **Footer**: `fragments/footer :: footer`
- **Page Header**: `fragments/page-header :: page-header(title, showBackButton, backUrl)`

## 📝 Ejemplos de Uso

### Template de Listado
```html
<html layout:decorate="~{layouts/base}" th:with="activeMenu='entidad'">
<head><title>Listado</title></head>
<body>
    <div layout:fragment="content">
        <div th:replace="~{fragments/page-header :: page-header('Título', false)}"></div>
        <!-- Contenido -->
    </div>
</body>
</html>
```

### Template de Formulario
```html
<html layout:decorate="~{layouts/base}" th:with="activeMenu='entidad'">
<head><title th:text="${isEdit ? 'Editar' : 'Crear'}">Form</title></head>
<body>
    <div layout:fragment="content">
        <div th:replace="~{fragments/page-header :: page-header(${isEdit ? 'Editar' : 'Crear'}, true, '/entidad')}"></div>
        <!-- Formulario -->
    </div>
</body>
</html>
```

## 🚀 Beneficios

1. **DRY**: Eliminación de código duplicado
2. **Mantenimiento**: Cambios en un solo lugar
3. **Consistencia**: Todas las páginas con la misma estructura
4. **Scalability**: Fácil agregar nuevas páginas
5. **Clean Code**: Separación clara de responsabilidades

## 🗑️ Archivos Eliminados

### Layouts Duplicados
- `layout/_header.html` → `fragments/header.html`
- `layout/_sidebar.html` → `fragments/sidebar.html`
- `layout/_footer.html` → `fragments/footer.html`
- `layouts/administrador.html` → (integrated en `layouts/base.html`)

### Templates Duplicados
- `expedientes/crear.html` + `expedientes/editar.html` → `expedientes/form.html`
- `usuarios/crear.html` + `usuarios/editar.html` → `usuarios/form.html`
- `expedientes.html` → `expedientes/list.html`

## 🔧 Configuración Controllers

Los controllers deben actualizar sus returns:

```java
// Antes
return "expedientes/crear";
return "expedientes/editar";

// Ahora
model.addAttribute("isEdit", false);
return "expedientes/form";

model.addAttribute("isEdit", true);
return "expedientes/form";
```