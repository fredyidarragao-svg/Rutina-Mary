# Rutina Mary — V1 (Android nativo)

App nativa Android (Kotlin + Jetpack Compose) con la rutina real de Mary Curo:
38 años, 1.53m, 72kg. Patinaje lunes/miércoles (1.5h), natación domingo (50min),
y 4 días de casa (martes, jueves, viernes, sábado) de 35min cada uno, con enfoque
en glúteo, pierna, abdomen y brazos — todo pensado para bajo impacto en rodillas.

Comparte la misma arquitectura que "Rutina Fredy" (proyecto hermano): Health Connect
para leer datos del Xiaomi Smart Watch 5 Active vía Mi Fitness, calendario de
continuidad con colores, check por ejercicio, fotos reales de biblioteca (dominio
público) y pictogramas propios para los 2 ejercicios sin foto disponible.

## Estructura de la semana

- Lunes / Miércoles: Patinaje 1.5h (con opción de rutina ALT en casa si no patina)
- Martes: Glúteo + Pierna (35min)
- Jueves: Abdomen + Quema (35min)
- Viernes: Glúteo + Pierna, variación (35min)
- Sábado: Brazos (tonificación) + Abdomen (35min)
- Domingo: Natación 50min (con opción ALT)

## Rotación semanal (4 semanas)

- Martes: el ejercicio principal de glúteo rota (Hip Thrust → Puente → Patada cuadrupedia → Hip Thrust)
- Jueves: el ejercicio principal de abdomen rota
- Sábado: la combinación de brazos rota (siempre peso ligero, más repeticiones — tonificación, no volumen)

## Cómo compilar

Igual que Rutina Fredy: GitHub Actions compila el APK automáticamente al hacer
push a `main`. El APK queda como artifact descargable en la pestaña Actions.

## Qué falta ajustar

- Ícono propio de la app (por ahora usa el mismo genérico de pesa)
- Verificar si Mi Fitness ya habilita Health Connect para el Xiaomi Smart Watch 5
  Active (modelo más nuevo, buenas chances de que sí)
