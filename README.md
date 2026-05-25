# Informe de Proyecto: STEM Link

**Curso:** CS 2031 Desarrollo Basado en Plataforma  
**Institución:** UTEC - Universidad de Ingeniería y Tecnología  
**Integrantes:**  
* Christian Estefano Estrada Fiestas 202420047
* Geremid Mickell Flores Alva 202410284
* Osmar Fabian 202210137

---

## 1. Introducción
STEM Link es una plataforma diseñada para conectar a estudiantes de secundaria con mentores universitarios y profesionales en áreas de Ciencia, Tecnología, Ingeniería y Matemáticas (STEM). El objetivo principal es democratizar el acceso a la orientación vocacional de alta calidad, permitiendo que jóvenes con curiosidad científica encuentren referentes que los guíen en su desarrollo académico y profesional. La plataforma facilita la gestión de perfiles, la programación de sesiones virtuales y la comunicación automatizada entre los participantes.

## 2. Identificación del Problema
A pesar del crecimiento de la industria tecnológica, existe una brecha significativa en la orientación STEM para escolares de sectores vulnerables o regiones alejadas. Los estudiantes a menudo carecen de:
* **Modelos a seguir:** Falta de contacto directo con profesionales del área.
* **Información veraz:** Desconocimiento sobre la realidad de las carreras técnicas.
* **Recursos de gestión:** Dificultad para organizar sesiones de mentoría de forma gratuita y estructurada.

STEM Link aborda esta problemática eliminando las barreras administrativas y conectando directamente el talento joven con la experiencia profesional.

## 3. Descripción de la Solución

### Funcionalidades Implementadas
* **Gestión de Usuarios y Seguridad:** Registro e inicio de sesión con roles diferenciados (Estudiante/Mentor) protegidos mediante JWT y BCrypt.
* **Perfiles Profesionales:** Los mentores cuentan con perfiles personalizables que incluyen biografía, enlaces a LinkedIn y métricas de impacto. La creación del perfil es automática tras el registro.
* **Motor de Búsqueda Avanzado:** Filtrado de mentores por nombre y habilidades técnicas (tags) utilizando consultas JPQL optimizadas y seguras.
* **Gestión de Disponibilidad y Reservas:** Sistema transaccional para que mentores definan sus horarios y alumnos soliciten citas sin conflictos.
* **Notificaciones Multi-canal:** Alertas in-app en tiempo real y correos electrónicos automáticos (Bienvenida, Solicitud, Confirmación y Cancelación).
* **Automatización (Cron Jobs):** Cancelación automática de reservas pendientes tras 48 horas de inactividad para optimizar la agenda de los mentores.
* **Módulo de Feedback:** Sistema de reseñas post-sesión para medir la calidad de la mentoría y el impacto generado.

### Tecnologías Utilizadas
* **Backend:** Java 21, Spring Boot 3.4.
* **Persistencia:** Spring Data JPA, Hibernate, PostgreSQL.
* **Seguridad:** Spring Security (RBAC), JWT.
* **Comunicación:** Spring Mail (SMTP), Thymeleaf (Plantillas HTML).
* **Pruebas:** JUnit 5, Mockito, MockMvc.
* **Infraestructura:** Docker, Docker Compose, GitHub Actions.

## 4. Modelo de Entidades
El sistema se basa en un modelo relacional sólido:
* **User (1:1) MentorProfile:** Extensión de datos para usuarios con rol mentor.
* **MentorProfile (N:M) TechnicalSkill:** Catálogo de especialidades para búsqueda.
* **MentorProfile (1:N) AvailabilityBlock:** Agenda granular del mentor.
* **User (1:N) Booking:** Reservas que vinculan a un estudiante con un mentor.
* **Booking (1:1) MentorshipSession:** Ejecución física de la reserva.
* **MentorshipSession (1:1) SessionFeedback:** Evaluación de impacto del alumno.

## 5. Medidas de Seguridad Implementadas
* **Cifrado de Datos:** Uso de BCrypt para el almacenamiento de contraseñas.
* **Validación de Complejidad:** Reglas estrictas para contraseñas (Mayúsculas, números, símbolos).
* **Control de Acceso Granular (RBAC):** Restricción de endpoints basada en roles (ej. solo mentores gestionan disponibilidad).
* **Protección contra Inyecciones:** Consultas parametrizadas en todos los repositorios (JPQL).
* **Privacidad (Ownership Checks):** Validaciones a nivel de servicio para asegurar que un usuario solo acceda y modifique sus propios datos (Notificaciones, Perfiles).

## 6. GitHub & Management
* **Kanban:** Uso de GitHub Projects para el seguimiento de tareas en tiempo real.
* **Workflow de Git:** Implementación de ramas funcionales (`feature/`) y fusión mediante Pull Requests.
* **CI/CD:** Pipeline automatizado en GitHub Actions para ejecución de pruebas unitarias e integración en cada push.

## 7. Conclusión
El desarrollo de STEM Link ha permitido integrar una arquitectura backend moderna, escalable y segura. Se ha logrado no solo cumplir con los requisitos funcionales de un sistema de mentoría, sino también añadir capas de automatización y comunicación profesional que garantizan una experiencia de usuario fluida. El proyecto demuestra la potencia de Spring Boot para manejar procesos complejos como tareas programadas y correos asíncronos bajo un diseño de código limpio.
