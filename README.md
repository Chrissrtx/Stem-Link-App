# STEM Link: Plataforma de Mentoría para el Futuro STEM

**Curso:** CS 2031 Desarrollo Basado en Plataforma  
**Institución:** UTEC - Universidad de Ingeniería y Tecnología  
**Integrantes:**  
* Christian Estefano Estrada Fiestas 202420047
* Geremid Mickell Flores Alva 202410284
* Osmar Fabian 202210137

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
7. [Notificaciones por Email y Asincronía](#notificaciones-por-email-y-asincronía)
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
    *   **Motor de Búsqueda por Tags:** Filtrado avanzado que permite a los alumnos encontrar mentores por habilidades técnicas específicas (ej. Python, Robótica) mediante consultas JPQL seguras.
    *   Visualización de perfiles públicos con rating y experiencia.

3.  **Gestión de Disponibilidad:**
    *   Los mentores pueden definir bloques horarios de disponibilidad semanal de forma granular.
    *   Visualización dinámica de la agenda del mentor para los estudiantes.
    *   **Validación de Conflictos:** Sistema que previene el solapamiento de bloques y asegura la integridad de la agenda del mentor.

4.  **Sistema de Reservas (Sesiones de Mentoría):**
    *   Flujo transaccional para solicitar y confirmar reservas.
    *   Cambio de estados dinámicos (PENDIENTE, CONFIRMADA, CANCELADA).
    *   **Integración con Email:** Notificaciones automáticas por correo electrónico para cada cambio de estado importante.

5.  **Módulo de Notificaciones y Feedback:**
    *   Alertas in-app para mantener a los usuarios informados en tiempo real.
    *   Sistema de evaluación post-sesión para recolectar feedback detallado del alumno.

### Tecnologías Utilizadas

*   **Backend Core:** Java 21 con Spring Boot 3.4.
*   **Seguridad:** Spring Security y JSON Web Tokens (JWT).
*   **Envío de Email:** Spring Mail con plantillas HTML dinámicas (Thymeleaf).
*   **Persistencia de Datos:** Spring Data JPA con Hibernate (PostgreSQL).
*   **Mapeo de Objetos:** ModelMapper para la transformación eficiente de Entidades a DTOs.
*   **Herramientas de Desarrollo:** Maven, Git, Docker, Mailtrap.

---

## Modelo de Entidades

El diseño del modelo de datos de STEM Link se centra en la flexibilidad de roles y la integridad transaccional de las reservas.

### Descripción de Entidades

1.  **User:** Entidad raíz para la autenticación. Contiene credenciales cifradas y una lista de roles.
2.  **MentorProfile:** Extensión del usuario con rol mentor. Almacena biografía, URL de videollamada y métricas.
3.  **TechnicalSkill:** Catálogo global de etiquetas de especialidad (ej. "IA", "Cálculo").
4.  **AvailabilityBlock:** Define los rangos horarios recurrentes de un mentor.
5.  **Booking:** Representa la reserva transaccional vinculando alumno, mentor y bloque horario.
7.  **MentorshipSession:** Representa la ejecución real de una reserva con sus notas y recursos.
8.  **SessionFeedback:** Evaluación de la sesión, permitiendo medir la calidad de la mentoría.
9.  **Notification:** Registro de alertas para el sistema de notificaciones in-app.

### Relaciones
*   **User 1:1 MentorProfile:** Un usuario puede optar por ser mentor.
*   **MentorProfile N:M TechnicalSkill:** Relación de muchos a muchos para el sistema de tags.
*   **MentorProfile 1:N AvailabilityBlock:** Un mentor gestiona su agenda mediante bloques.
*   **Student 1:N Booking / Mentor 1:N Booking:** Las reservas centralizan la interacción.
*   **Booking 1:1 MentorshipSession:** Cada reserva genera una sesión rastreable.

---

## Testing y Manejo de Errores

### Niveles de Testing
*   **Pruebas Unitarias:** Implementadas con JUnit 5 y Mockito para validar reglas de negocio.
*   **Pruebas de Integración:** Uso de `@SpringBootTest` para verificar el flujo completo desde el Controller hasta la DB.
*   **Pruebas de Email:** Test de integración dedicado (`EmailIntegrationTest`) para verificar el envío asíncrono a servidores SMTP (Mailtrap).

### Manejo de Errores
Se utiliza un `GlobalExceptionHandler` con `@RestControllerAdvice` para centralizar la gestión de errores, devolviendo respuestas JSON estandarizadas.

#### Jerarquía de Excepciones Personalizadas:
*   **404 Not Found:** `UserNotFoundException`, `NotificationNotFoundException`, `ResourceNotFoundException`.
*   **403 Forbidden:** `UnauthorizedNotificationAccessException`, `InvalidSessionParticipantException`.
*   **409 Conflict:** `EmailAlreadyExistsException`, `FeedbackAlreadySubmittedException`, `TimeSlotConflictException`.
*   **400 Bad Request:** `SessionNotCompletedException`, `InvalidOperationException`.

---

## Medidas de Seguridad Implementadas

### Seguridad de Datos
*   **Autenticación JWT:** Sesiones seguras sin estado.
*   **Cifrado BCrypt:** Todas las contraseñas son hasheadas antes de persistirse.
*   **Protección de Privacidad:** Hotfix aplicado para asegurar que los usuarios solo accedan a sus propias notificaciones y perfiles privados.

### Prevención de Vulnerabilidades
*   **Parametrización de Consultas:** Uso de JPQL con parámetros nombrados para prevenir **SQL Injection**.
*   **Validación de Dominio:** Los servicios validan la autoría de cada acción (Ownership checks) para evitar el acceso no autorizado a recursos de terceros.

---

## Notificaciones por Email y Asincronía

Cumpliendo con los estándares de calidad, el sistema de comunicación es completamente **desacoplado y asíncrono**:
*   **Spring Mail + Thymeleaf:** Se envían correos electrónicos con formato HTML profesional para:
    *   **Registro:** Bienvenida a la plataforma.
    *   **Solicitud:** Aviso al mentor sobre una nueva petición.
    *   **Confirmación:** Notificación al alumno cuando su sesión es aprobada.
    *   **Cancelación:** Alertas automáticas a ambas partes si una cita se cancela.
*   **Event-Driven Architecture:** Se utiliza `ApplicationEventPublisher` para que la lógica de negocio no dependa directamente del envío de correos, mejorando el rendimiento y la mantenibilidad.
*   **Anotación `@Async`:** El procesamiento de emails ocurre en hilos separados, garantizando respuestas inmediatas en la API.

---

## GitHub & Management

*   **GitHub Projects:** Gestión mediante tablero Kanban con estados: To Do, In Progress, Review, Done.
*   **Estrategia de Ramas:** Uso de ramas por funcionalidad (`feature/`) y merge mediante Pull Requests revisados.
*   **CI/CD:** Configuración inicial de GitHub Actions para ejecución automática de tests.

---

## Conclusión

### Logros del Proyecto
Se ha desarrollado un ecosistema backend completo que no solo maneja la persistencia de datos, sino que automatiza la comunicación con el usuario mediante un sistema de eventos y emails robusto. La arquitectura permite una búsqueda eficiente de mentores y una gestión segura de agendas.

### Aprendizajes Clave
La implementación de asincronía y el manejo de plantillas HTML dinámicas han sido fundamentales para elevar la calidad del proyecto. Asimismo, la refactorización hacia repositorios reales tras la fase de prototipado reforzó la importancia de un diseño modular.

### Trabajo Futuro
*   Implementación de Cron Jobs para cancelaciones automáticas tras 48h de inactividad.
*   Integración con pasarelas de pago para servicios premium (opcional).
*   Despliegue automatizado en entornos de nube (AWS/Azure).

---

## Apéndices

### Licencia
Este proyecto se distribuye bajo la licencia MIT.

### Referencias
*   Documentación de Spring Boot: [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
*   Thymeleaf Templates: [https://www.thymeleaf.org/](https://www.thymeleaf.org/)
