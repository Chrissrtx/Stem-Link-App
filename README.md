# Informe de Proyecto: STEM Link

**Curso:** CS 2031 Desarrollo Basado en Plataforma  
**Institución:** UTEC - Universidad de Ingeniería y Tecnología  

## Integrantes

| Nombre Completo | Código | GitHub User |
| :--- | :---: | :--- |
| Christian Estefano Estrada Fiestas | 202420047 | [Chrissrtx](https://github.com/Chrissrtx) |
| Geremid Mickell Flores Alva | 202410284 | [Geremid](https://github.com/Geremid) |
| Osmar Vilchez Aguirre  | 202510122 | [Osomar1705](https://github.com/Osomar1705) |
| Rodolfo Elard Huaroc Enciso  | 202220307 | [rodolfoextremo69](https://github.com/rodolfoextremo69) |

---

## Índice

1. [Introducción](#1-introducción)
2. [Identificación del Problema](#2-identificación-del-problema)
3. [Descripción de la Solución](#3-descripción-de-la-solución)
4. [Modelo de Entidades](#4-modelo-de-entidades)
5. [Instrucciones de Ejecución](#5-instrucciones-de-ejecución)
6. [Testing y Manejo de Errores](#6-testing-y-manejo-de-errores)
7. [Medidas de Seguridad Implementadas](#7-medidas-de-seguridad-implementadas)
8. [GitHub & Management](#8-github--management)
9. [Trabajo a Futuro](#9-trabajo-a-futuro)
10. [Conclusión](#10-conclusión)
11. [Referencias](#11-referencias)

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
* **Notificaciones Multi-canal:** Alertas in-app en tiempo real y correos electrónicos automáticos (Bienvenida, Solicitud, Confirmación y Cancelación) procesados de forma asíncrona.
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
  
**Gráfico E-R:**
[![](https://mermaid.ink/img/pako:eNqlVttu2kAQ_RVrpUitRCLuBL8BgcbCgQhIq1aRrMUeYMXaa-2u0yaET-pTPyE_1rG5BMcmoaofYHZmZ_Z4Lse7Iq7wgJgE5BWjc0n9-8DA527cHRmrjRw_tgjmBvOM2_6rbqwlQy34lHHjntz170nGGFKlfgrpZQwB9eEgPFPakIKD2ujWm7_N7013MBmOnNvRsGfZ3Y9QJbpIgXTQ0MvCnTKR0T0wD0SHcn4necbIWbAEjwV5NuaH1NU3gCs3D_qk27keWJ2W7Yz7lm2fmNF0brZKD5QrWaiZCI7nKP8UHwItpBNKMWMc3uYl2aLBXQTMpdxRS8b54Z7UOa2vLctutS3bmnx32vaw0z-pHu8j6AaRb3j0cTj7BrA8dEVAE-aDoTSVOpbyjBB4r6YU2vZw2LcGX06CqHTkIcrc7HyUQERyRTXgO2j4P_j7dKCLjlTOIYmPKwFFr6WPtsL42rp1xt3x2BoOTnr_qRBL7LMjU6NFyNxsowoNKqOVoEQkXbBxcPKGYovK6XW7V-3WP3WQWrDQUaAUTsFbpFagYQ4ySXYW1Ma_I_xYUEcGeQTunqtSkAfDidXDOZ6cms13CEgzzSEHn1J0fqDHenCggcHUCKh3eiNshLMzYwScupinXYUSSn9-Pj8Xz28p1UT61gwCMD6VzNLnHY-_eqz2oxRvxTM5e6JxoUE-0NhpkOuUStvG02VTSDvkkvw2QA7dxGE8mDEEuxCSSibS4fIDpeEnIHLRvxcjxbFxoFAoQBR0yjjzsEYkn_SPuYMy4ggvvz2KgkxD2AFOFSw11HGIOdZMUgMngr38CdLVy3HZBssM4EFW4IHyCPvmIBwpkLlkHjG1jKBAfJD4xcclSQYBO2cBSF8kKQyVy_j8NfqENPghhL9zkyKaL4g5o1zhKgpjrtxeOfZbkAoBZzQKNDFL1VItCULMFfmF60r1olmslCvVWrVeqZdLBfJIzMvyxWW52WgUa5f1eqVarKwL5Ck5tXjRbFRqjVq5WS2WGo1mqV4g-BlHErjZ3HiSi8_6L8avpOQ?type=png)](https://mermaid.ai/live/edit#pako:eNqlVttu2kAQ_RVrpUitRCLuBL8BgcbCgQhIq1aRrMUeYMXaa-2u0yaET-pTPyE_1rG5BMcmoaofYHZmZ_Z4Lse7Iq7wgJgE5BWjc0n9-8DA527cHRmrjRw_tgjmBvOM2_6rbqwlQy34lHHjntz170nGGFKlfgrpZQwB9eEgPFPakIKD2ujWm7_N7013MBmOnNvRsGfZ3Y9QJbpIgXTQ0MvCnTKR0T0wD0SHcn4necbIWbAEjwV5NuaH1NU3gCs3D_qk27keWJ2W7Yz7lm2fmNF0brZKD5QrWaiZCI7nKP8UHwItpBNKMWMc3uYl2aLBXQTMpdxRS8b54Z7UOa2vLctutS3bmnx32vaw0z-pHu8j6AaRb3j0cTj7BrA8dEVAE-aDoTSVOpbyjBB4r6YU2vZw2LcGX06CqHTkIcrc7HyUQERyRTXgO2j4P_j7dKCLjlTOIYmPKwFFr6WPtsL42rp1xt3x2BoOTnr_qRBL7LMjU6NFyNxsowoNKqOVoEQkXbBxcPKGYovK6XW7V-3WP3WQWrDQUaAUTsFbpFagYQ4ySXYW1Ma_I_xYUEcGeQTunqtSkAfDidXDOZ6cms13CEgzzSEHn1J0fqDHenCggcHUCKh3eiNshLMzYwScupinXYUSSn9-Pj8Xz28p1UT61gwCMD6VzNLnHY-_eqz2oxRvxTM5e6JxoUE-0NhpkOuUStvG02VTSDvkkvw2QA7dxGE8mDEEuxCSSibS4fIDpeEnIHLRvxcjxbFxoFAoQBR0yjjzsEYkn_SPuYMy4ggvvz2KgkxD2AFOFSw11HGIOdZMUgMngr38CdLVy3HZBssM4EFW4IHyCPvmIBwpkLlkHjG1jKBAfJD4xcclSQYBO2cBSF8kKQyVy_j8NfqENPghhL9zkyKaL4g5o1zhKgpjrtxeOfZbkAoBZzQKNDFL1VItCULMFfmF60r1olmslCvVWrVeqZdLBfJIzMvyxWW52WgUa5f1eqVarKwL5Ck5tXjRbFRqjVq5WS2WGo1mqV4g-BlHErjZ3HiSi8_6L8avpOQ)

## 5. Instrucciones de Ejecución

### Requisitos Previos
* Docker y Docker Compose instalados.
* Java 21 (para ejecución local sin Docker).
* Maven (opcional, incluido vía `mvnw`).

### Pasos para iniciar con Docker
1. Clonar el repositorio.
2. Navegar a la carpeta `Stem Link App/StemLinkApp`.
3. Ejecutar el comando:
   ```bash
   docker-compose up --build
   ```
4. La API estará disponible en `http://localhost:8080`.
5. La base de datos PostgreSQL estará accesible en el puerto `5433` (según configuración del compose).

### Configuración de Variables de Entorno
Asegúrese de configurar el archivo `.env` con las credenciales de Mailtrap para las notificaciones por email y los datos de conexión a la base de datos.

## 6. Testing y Manejo de Errores

### Detalle de Testing
Se implementó una estrategia de pruebas en tres capas para garantizar la estabilidad del sistema:
* **Pruebas de Repositorio:** Se validaron las consultas personalizadas JPQL. **Detección clave:** Se logró comprobar que el filtrado por tags era insensible a mayúsculas/minúsculas y que las relaciones N:M se persistían correctamente.
* **Pruebas de Servicio (Mockito):** Se simularon escenarios de negocio complejos. **Detección clave:** Se identificaron y corrigieron fallos en la lógica de creación de perfiles automáticos y se validó que el envío de correos no bloqueara el hilo principal (asincronía).
* **Pruebas de Controlador (MockMvc):** Se verificaron los endpoints y la seguridad. **Detección clave:** Se confirmó que los usuarios sin autenticación no podían acceder a datos privados de otros usuarios (ownership checks).

### Manejo de Errores
Se utiliza un `GlobalExceptionHandler` con `@RestControllerAdvice` para centralizar la gestión de errores, devolviendo respuestas JSON estandarizadas. Se manejan excepciones personalizadas para casos de negocio como emails duplicados, recursos no encontrados, conflictos de horario y accesos no autorizados.

## 7. Medidas de Seguridad Implementadas
* **Cifrado de Datos:** Uso de BCrypt para el almacenamiento de contraseñas.
* **Validación de Complejidad:** Reglas estrictas para contraseñas (Mayúsculas, números, símbolos).
* **Control de Acceso Granular (RBAC):** Restricción de endpoints basada en roles (ej. solo mentores gestionan disponibilidad).
* **Protección contra Inyecciones:** Consultas parametrizadas en todos los repositorios (JPQL).
* **Privacidad (Ownership Checks):** Validaciones a nivel de servicio para asegurar que un usuario solo acceda y modifique sus propios datos (Notificaciones, Perfiles).

## 8. GitHub & Management
* **Kanban:** Uso de GitHub Projects para el seguimiento de tareas en tiempo real.
* **Workflow de Git:** Implementación de ramas funcionales (`feature/`) y fusión mediante Pull Requests.
* **CI/CD:** Pipeline automatizado en GitHub Actions para ejecución de pruebas unitarias e integración en cada push.

## 9. Trabajo a Futuro
* **Motor de Búsqueda Semántica:** Implementar búsqueda avanzada utilizando **Embeddings e Inteligencia Artificial** para emparejar alumnos con mentores basándose en lenguaje natural.
* **Integración con Google Calendar:** Sincronización automática de las reservas aceptadas con el calendario del mentor y el alumno.
* **Sumarios de Sesión con IA:** Generación automática de resúmenes y puntos clave de la mentoría a partir de las notas de la sesión utilizando modelos de lenguaje (LLM).
* **Integración con Pasarelas de Pago:** Soporte para servicios premium o donaciones voluntarias.

## 10. Conclusión
El desarrollo de STEM Link ha permitido integrar una arquitectura backend moderna, escalable y segura. Se ha logrado no solo cumplir con los requisitos funcionales de un sistema de mentoría, sino también añadir capas de automatización y comunicación profesional que garantizan una experiencia de usuario fluida. El proyecto demuestra la potencia de Spring Boot para manejar procesos complejos como tareas programadas y correos asíncronos bajo un diseño de código limpio.

## 11. Referencias
* Documentación de Spring Boot: [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
* Thymeleaf Templates: [https://www.thymeleaf.org/](https://www.thymeleaf.org/)
* Spring Security Reference: [https://docs.spring.io/spring-security/reference/](https://docs.spring.io/spring-security/reference/)
* Hibernate ORM Docs: [https://hibernate.org/orm/documentation/](https://hibernate.org/orm/documentation/)
* Docker Compose Specification: [https://docs.docker.com/compose/](https://docs.docker.com/compose/)
