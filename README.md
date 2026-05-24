# STEM Link: Plataforma de Mentoría para el Futuro STEM

**Curso:** CS 2031 Desarrollo Basado en Plataforma  
**Institución:** UTEC - Universidad de Ingeniería y Tecnología  
**Integrantes:**  
* Christian Estefano Estrada Fiestas 202420047
* [Nombre del Integrante 2]
* [Nombre del Integrante 3]

---

## Índice

1. [Introducción](#introducción)
2. [Identificación del Problema](#identificación-del-problema)
3. [Descripción de la Solución](#descripción-de-la-solución)
    * [Funcionalidades Implementadas](#funcionalidades-implementadas)
    * [Tecnologías Utilizadas](#tecnologías-utilizadas)
4. [Modelo de Entidades](#modelo-de-entidades)
    * [Descripción de Entidades](#descripción-de-entidades)
    * [Relaciones](#relaciones)
5. [Testing y Manejo de Errores](#testing-y-manejo-de-errores)
6. [Medidas de Seguridad Implementadas](#medidas-de-seguridad-implementadas)
7. [Eventos y Asincronía](#eventos-y-asincronía)
8. [GitHub & Management](#github--management)
9. [Conclusión](#conclusión)
10. [Apéndices](#apéndices)

---

## Introducción

### Contexto
En la actualidad, el avance tecnológico y científico demanda una fuerza laboral altamente capacitada en áreas STEM (Ciencia, Tecnología, Ingeniería y Matemáticas). Sin embargo, existe una desconexión crítica entre la educación secundaria y las oportunidades reales en estos campos. Muchos estudiantes con talento y curiosidad científica se ven frenados por la falta de referentes cercanos, recursos educativos especializados o una orientación vocacional efectiva que les permita visualizar un camino claro hacia carreras técnicas o científicas.

### Objetivos del Proyecto
STEM Link surge con el propósito de cerrar esta brecha, facilitando un espacio de encuentro entre escolares y mentores experimentados. Los objetivos principales son:
* **Democratizar el acceso a la mentoría:** Permitir que cualquier estudiante de secundaria, independientemente de su ubicación, pueda recibir orientación de calidad.
* **Fomentar la vocación STEM:** Inspirar a las nuevas generaciones a través del contacto directo con universitarios y profesionales.
* **Proveer una herramienta de gestión ágil:** Facilitar la programación de sesiones de mentoría gratuitas sin las fricciones administrativas tradicionales.

---

## Identificación del Problema

### Descripción del Problema
La brecha educativa en STEM no es solo una cuestión de currículo escolar; es un problema de ecosistema. Los estudiantes a menudo perciben las ciencias y las matemáticas como áreas abstractas, difíciles y desconectadas de la realidad. Esta percepción se agrava por la falta de "modelos a seguir" (role models) con los que puedan identificarse. Un estudiante de secundaria rara vez tiene la oportunidad de conversar con un investigador en IA o un ingeniero aeroespacial sobre sus dudas y aspiraciones.

### Justificación
Solucionar este problema es vital para el desarrollo económico y social. Fomentar el talento STEM desde una etapa temprana asegura que el país cuente con los innovadores necesarios para resolver los retos del mañana. Además, al ofrecer mentorías gratuitas, STEM Link aborda la desigualdad de oportunidades, permitiendo que el talento brille sin importar el nivel socioeconómico del estudiante.

---

## Descripción de la Solución

STEM Link es una plataforma web integral diseñada para orquestar el ciclo de vida completo de una mentoría, desde el descubrimiento del mentor hasta el feedback post-sesión.

### Funcionalidades Implementadas

1.  **Módulo de Autenticación y Gestión de Usuarios:**
    *   Registro seguro de usuarios con distinción de roles (Estudiante/Mentor).
    *   Autenticación basada en JWT para sesiones seguras y escalables.
    *   Gestión de perfiles de usuario y recuperación de datos personales.

2.  **Módulo de Perfiles y Búsqueda Avanzada:**
    *   Personalización de perfiles para mentores, incluyendo biografía, métricas de impacto y enlaces a redes profesionales (LinkedIn, GitHub).
    *   Sistema de etiquetado (Tags) para categorizar habilidades técnicas (ej. Python, Robótica, Cálculo).
    *   Motor de búsqueda y filtrado que permite a los alumnos encontrar mentores según sus necesidades específicas.

3.  **Gestión de Disponibilidad:**
    *   Los mentores pueden definir bloques horarios de disponibilidad semanal de forma granular.
    *   Visualización dinámica de la agenda del mentor para los estudiantes.
    *   Control de concurrencia para evitar solapamientos en la reserva de bloques.

4.  **Sistema de Reservas (Sesiones de Mentoría):**
    *   Flujo transaccional para solicitar y confirmar reservas.
    *   Cambio de estados dinámicos (PENDIENTE, CONFIRMADA, CANCELADA).
    *   Notificaciones automáticas al dispararse una solicitud de reserva.

5.  **Módulo de Notificaciones y Feedback:**
    *   Alertas in-app para mantener a los usuarios informados sobre sus sesiones.
    *   Sistema de evaluación post-sesión para recolectar feedback tanto del alumno como del mentor.

### Tecnologías Utilizadas

*   **Backend Core:** Java 21 con Spring Boot 3.4.
*   **Seguridad:** Spring Security y JSON Web Tokens (JWT).
*   **Persistencia de Datos:** Spring Data JPA con Hibernate.
*   **Base de Datos:** PostgreSQL (Relacional).
*   **Mapeo de Objetos:** ModelMapper para la transformación eficiente de Entidades a DTOs.
*   **Documentación de API:** Swagger/OpenAPI (opcional/planificado).
*   **Herramientas de Desarrollo:** Maven, Git, GitHub.

---

## Modelo de Entidades

El diseño del modelo de datos de STEM Link se centra en la flexibilidad de roles y la integridad transaccional de las reservas.

### Descripción de Entidades

1.  **User:** Entidad raíz para la autenticación. Contiene credenciales cifradas y una lista de roles (STUDENT, MENTOR).
2.  **MentorProfile:** Extensión del usuario con rol mentor. Almacena la biografía, el enlace estático para la videollamada (Meet/Zoom) y métricas de impacto.
3.  **HabilidadTecnica:** Catálogo de conocimientos (ej. "Álgebra Lineal", "C++"). Un mentor puede tener múltiples habilidades y una habilidad puede pertenecer a muchos mentores.
4.  **BloqueDisponibilidad:** Define los rangos horarios recurrentes (DayOfWeek, StartTime, EndTime) en los que un mentor está libre.
5.  **Reserva:** Representa la cita programada en una fecha específica. Vincula a un estudiante con un mentor y un bloque horario.
6.  **SesionMentoria:** Contiene los detalles específicos de lo tratado en la sesión: tema, notas y recursos compartidos.
7.  **FeedbackSesion:** Evaluación de la sesión (estrellas y comentarios), vinculada a la sesión de mentoría.

### Relaciones
*   **User 1:1 MentorProfile:** Un usuario puede tener opcionalmente un perfil de mentor.
*   **MentorProfile N:M HabilidadTecnica:** Los mentores se especializan en diversas áreas.
*   **MentorProfile 1:N BloqueDisponibilidad:** Un mentor gestiona su agenda mediante múltiples bloques.
*   **Student (User) 1:N Reserva / MentorProfile 1:N Reserva:** Las reservas actúan como el nexo transaccional.
*   **Reserva 1:1 SesionMentoria:** Cada reserva exitosa genera una sesión de contenido.
*   **SesionMentoria 1:1 FeedbackSesion:** Cada sesión permite una única evaluación.

---

## Testing y Manejo de Errores

### Niveles de Testing
*   **Pruebas Unitarias:** Implementadas con JUnit 5 y Mockito para validar la lógica de negocio en la capa de servicios, asegurando que el cálculo de disponibilidad y las reglas de reserva funcionen aisladamente.
*   **Pruebas de Integración:** Uso de `@SpringBootTest` para verificar la interacción correcta entre las capas de Controller y Repository.

### Manejo de Errores
Se ha implementado un `GlobalExceptionHandler` utilizando la anotación `@ControllerAdvice`. Esto permite:
*   Capturar excepciones personalizadas como `ResourceNotFoundException` o `UnauthorizedException`.
*   Retornar respuestas HTTP estandarizadas con códigos de estado correctos (404, 401, 400).
*   Validar datos de entrada automáticamente con `@Valid` y devolver mensajes de error claros al cliente.

---

## Medidas de Seguridad Implementadas

### Seguridad de Datos
*   **Autenticación JWT:** Los tokens JWT permiten una autenticación sin estado (stateless), mejorando la escalabilidad y seguridad de la API.
*   **Cifrado de Contraseñas:** Uso de `BCryptPasswordEncoder` para asegurar que las contraseñas nunca se almacenen en texto plano.
*   **Gestión de Permisos:** Los endpoints están protegidos mediante roles. Por ejemplo, solo un usuario con `ROLE_MENTOR` puede actualizar su disponibilidad.

### Prevención de Vulnerabilidades
*   **CORS:** Configuración estricta de Cross-Origin Resource Sharing.
*   **Protección contra Inyección SQL:** Gracias al uso de Spring Data JPA y consultas parametrizadas.

---

## Eventos y Asincronía

La plataforma utiliza un modelo de eventos para desacoplar procesos críticos.
*   **Confirmación de Reservas:** Cuando un mentor confirma una reserva, se dispara un evento asíncrono que envía una notificación por correo electrónico al estudiante.
*   **Procesamiento Asíncrono:** El uso de `@Async` permite que el envío de correos o notificaciones no bloquee el hilo principal de la ejecución, mejorando drásticamente el tiempo de respuesta de la API.

---

## GitHub & Management

*   **GitHub Projects:** Se utilizó para la gestión de tareas mediante un tablero Kanban, asignando *issues* con etiquetas de prioridad y fechas límite claras.
*   **GitHub Actions:** Se implementó un flujo de CI (Integración Continua) que ejecuta los tests automáticamente en cada *pull request*, garantizando que no se introduzcan regresiones al código principal.

---

## Conclusión

### Logros del Proyecto
Se ha logrado construir un backend robusto capaz de soportar el flujo transaccional de mentorías STEM. La arquitectura en capas y el cumplimiento de los principios SOLID permiten que el sistema sea mantenible y escalable.

### Aprendizajes Clave
El desarrollo de STEM Link ha permitido profundizar en la implementación de seguridad compleja con Spring Security, el manejo de relaciones JPA avanzadas y la importancia de la asincronía en sistemas con interacción de usuario en tiempo real.

### Trabajo Futuro
*   Integración con APIs de calendarios externos (Google Calendar).
*   Implementación de un sistema de mensajería instantánea dentro de la plataforma.
*   Desarrollo de una aplicación móvil nativa utilizando el backend actual.

---

## Apéndices

### Licencia
Este proyecto se distribuye bajo la licencia MIT.

### Referencias
*   Documentación de Spring Boot: [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
*   Guía de Seguridad JWT: [https://jwt.io/introduction/](https://jwt.io/introduction/)
