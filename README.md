# Documentación de Proyecto: STEM Link

## 1. El Problema
Existe una brecha significativa en la educación secundaria: los escolares carecen de orientación vocacional temprana y de un acceso práctico, cercano y motivador a la educación STEM. Muchos estudiantes con potencial pierden el interés en áreas tecnológicas o científicas al enfrentarse a barreras lógicas o matemáticas sin el apoyo adecuado, lo que perpetúa la falta de talento joven preparado para las carreras del futuro.  

## 2. La Solución
STEM Link es una plataforma web que conecta a escolares de secundaria con mentores universitarios para democratizar el aprendizaje STEM.  

Para destacar en el mercado y en la evaluación del curso (innovación), el MVP pivota de un directorio tradicional a un sistema impulsado por Inteligencia de Datos Ligera:

- **Búsqueda Semántica (Matchmaking):** En lugar de filtros rígidos, los escolares buscan ayuda usando lenguaje natural (ej. "Me gustan los videojuegos y quiero aprender a programar, pero me cuesta la lógica"). El sistema utiliza modelos de embeddings para analizar la intención del texto y emparejar al estudiante con el mentor universitario cuyas habilidades técnicas y perfil se alineen mejor, calculando la Similitud del Coseno.
- **Gestión Ágil de Reservas:** Se elimina la complejidad de pasarelas de pago y automatización de videollamadas. El sistema se centra en la disponibilidad mediante "plantillas semanales" y utiliza enlaces estáticos de videollamada proporcionados por el mentor, asegurando la viabilidad del desarrollo en un ciclo académico de 14 semanas.

## 3. Stack Tecnológico
El proyecto utilizará una arquitectura moderna, separando el núcleo transaccional del motor de inteligencia artificial.

### Backend Core (Transaccional & Seguridad):
- **Lenguaje & Framework:** Java 21 con Spring Boot.
- **Módulos de Spring:** Spring Web (APIs REST), Spring Data JPA (Persistencia).
- **Seguridad:** Spring Security con implementación de JWT (JSON Web Tokens) para autenticación sin estado y autorización basada en una colección de roles dinámicos.
- **Testing:** JUnit y Mockito para pruebas unitarias de la lógica de negocio y reservas.
- **Utilidades:** ModelMapper (para transferencia de DTOs) e Hibernate.

### Motor de Matchmaking AI:
- **Lenguaje & Framework:** Microservicio ligero en Python utilizando FastAPI.
- **Librería IA:** sentence-transformers (Hugging Face) utilizando un modelo pre-entrenado multilingüe (ej. `paraphrase-multilingual-MiniLM-L12-v2`) para generar los vectores a partir del texto.

### Base de Datos:
- **Motor:** PostgreSQL.
- **Extensión Clave:** `pgvector`, que permite almacenar los vectores generados por Python directamente en columnas de la base de datos y realizar las consultas de similitud semántica de forma nativa e hiperrápida mediante SQL.

## 4. Arquitectura de Entidades (Modelo de Datos Base)
El esquema relacional está diseñado para soportar roles fluidos y búsquedas eficientes de disponibilidad, cumpliendo con la exigencia de complejidad y múltiples relaciones de la rúbrica.

- **User (Usuario General):**
  - Entidad centralizada que maneja la autenticación y las credenciales (email, password hash).
  - Almacena los roles de forma dinámica como una colección (ej. `ROLE_ESCOLAR`, `ROLE_MENTOR`), permitiendo que una misma cuenta pueda reservar sesiones o impartirlas sin duplicar registros.

- **MentorProfile (Perfil de Mentor):**
  - Relación `@OneToOne` con User. Se instancia solo si el usuario tiene el rol de mentor.
  - Encapsula datos públicos: biografía, enlace estático de videollamada (Meet/Zoom), y métricas de impacto.

- **HabilidadTecnica (Tags Semánticos):**
  - Relación `@ManyToMany` con MentorProfile.
  - Catálogo de temas (Python, C++, Álgebra, etc.). Los nombres y descripciones de estas habilidades, junto con la biografía del mentor, se vectorizan para alimentar el motor de búsqueda semántica.

- **BloqueDisponibilidad (Plantilla de Agenda):**
  - Relación `@ManyToOne` con MentorProfile.
  - Funciona como un patrón recurrente. Almacena el DayOfWeek (Enum de Java) y horas de inicio/fin (LocalTime), reflejando la disponibilidad semanal típica de un universitario sin saturar la base de datos.

- **Reserva (Transaccional):**
  - Relación `@ManyToOne` con User (el escolar) y `@ManyToOne` con MentorProfile.
  - Representa el evento programado. Contiene la fecha específica (LocalDate), horas, estado (PENDIENTE, CONFIRMADA, CANCELADA) y cruza datos con BloqueDisponibilidad para evitar solapamientos.

- **SesionMentoria (Contenido):**
  - Relación `@OneToOne` con Reserva.
  - Encapsula los metadatos generados durante la llamada: el tema específico tratado, notas del mentor y enlaces a recursos compartidos.

- **FeedbackSesion (Evaluación):**
  - Relación `@OneToOne` con SesionMentoria.
  - Almacena la calificación por estrellas, comentarios privados del mentor para su propio seguimiento y el registro del impacto generado en el escolar.
