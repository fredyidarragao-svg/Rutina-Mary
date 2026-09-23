package com.mary.rutina.data

/**
 * Fuente de datos de la rutina de Mary Curo. Perfil: 38 años, 1.53m, 72kg.
 * Deportes: patinaje lunes/miércoles 1.5h, natación domingo 50min.
 * Días de casa (martes, jueves, viernes, sábado) de 35min, bajo impacto en rodillas.
 * Objetivos: incrementar glúteos, reducir abdomen, fortalecer piernas, reducir brazos.
 */
object RoutineData {

    val weeklyPlan: List<DayPlan> = listOf(
        DayPlan(
            id = "lun", dia = "LUNES", titulo = "PATINAJE", subtitulo = "Resistencia suave",
            duracion = "90 min", intensidad = "MEDIA", deporte = "patinaje", esDeporte = true,
            planNormal = listOf(
                PlanItem("plancha", "3x30s", "30s", "Core opcional post-patinaje", "Seguro")
            ),
            planAlternativoId = "LUN_ALT"
        ),
        DayPlan(
            id = "mar", dia = "MARTES", titulo = "CASA 35min", subtitulo = "GLÚTEO + PIERNA",
            duracion = "35 min", intensidad = "MEDIA", deporte = null, esDeporte = false,
            planNormal = listOf(
                PlanItem("bici", "5 min", "-", "Activación suave, sin resistencia alta", "Bajo impacto"),
                PlanItem("ROT_GLUTEO", "4x15", "45s", "(rota cada semana)", ""),
                PlanItem("sentadilla-sumo-manc", "3x15", "45s", "Rango controlado, sin pasar de 90°", "Rango corto si molesta"),
                PlanItem("zancada-estatica-manc", "3x12 c/pierna", "45s", "Paso corto, sin rebote", "Sin rebote en rodilla"),
                PlanItem("plancha", "2x30s", "30s", "Cierre de core", "Seguro")
            ),
            planAlternativoId = "CASA"
        ),
        DayPlan(
            id = "mie", dia = "MIÉRCOLES", titulo = "PATINAJE", subtitulo = "Intervalos suaves",
            duracion = "90 min", intensidad = "MEDIA", deporte = "patinaje", esDeporte = true,
            planNormal = listOf(
                PlanItem("rueda", "2x8", "45s", "Rango corto, sin forzar lumbar", "Seguro")
            ),
            planAlternativoId = "MIE_ALT"
        ),
        DayPlan(
            id = "jue", dia = "JUEVES", titulo = "CASA 35min", subtitulo = "ABDOMEN + QUEMA",
            duracion = "35 min", intensidad = "MEDIA", deporte = null, esDeporte = false,
            planNormal = listOf(
                PlanItem("bici", "6 min", "-", "Activación", "Bajo impacto"),
                PlanItem("ROT_ABDOMEN", "3x12", "40s", "(rota cada semana)", ""),
                PlanItem("crunch-bicicleta", "3x15 c/lado", "40s", "Ritmo controlado, sin tirar del cuello", "Seguro"),
                PlanItem("elevacion-piernas-tumbada", "3x12", "45s", "Lumbar pegada al suelo", "Suelo acolchado"),
                PlanItem("bici", "10 min Z2", "-", "Quema suave, sin picos", "Bajo impacto")
            ),
            planAlternativoId = "CASA"
        ),
        DayPlan(
            id = "vie", dia = "VIERNES", titulo = "CASA 35min", subtitulo = "GLÚTEO + PIERNA",
            duracion = "35 min", intensidad = "MEDIA", deporte = null, esDeporte = false,
            planNormal = listOf(
                PlanItem("bici", "5 min", "-", "Activación", "Bajo impacto"),
                PlanItem("hip-thrust-barra", "4x15", "60s", "Cadera sube en línea recta, aprieta glúteo arriba", "Sin carga en rodilla"),
                PlanItem("puente-gluteo", "3x15", "45s", "Bodyweight o con mancuerna sobre cadera", "Muy bajo impacto"),
                PlanItem("elevacion-cadera-un-pie", "3x10 c/pierna", "45s", "Unilateral, controlado", "Rodilla semiflex fija"),
                PlanItem("plancha-toques", "2x10 c/lado", "45s", "Core + estabilidad", "Seguro")
            ),
            planAlternativoId = "CASA"
        ),
        DayPlan(
            id = "sab", dia = "SÁBADO", titulo = "CASA 35min", subtitulo = "BRAZOS + ABDOMEN",
            duracion = "35 min", intensidad = "SUAVE", deporte = null, esDeporte = false,
            planNormal = listOf(
                PlanItem("bici", "5 min", "-", "Activación", "Bajo impacto"),
                PlanItem("ROT_BRAZO1", "3x15", "40s", "(rota cada semana) — peso ligero, más repeticiones", ""),
                PlanItem("ROT_BRAZO2", "3x15", "40s", "(rota cada semana) — peso ligero, más repeticiones", ""),
                PlanItem("rueda", "2x8", "45s", "Rango corto controlado", "Seguro"),
                PlanItem("plancha", "2x30s", "30s", "Cierre de core", "Seguro")
            ),
            planAlternativoId = "CASA"
        ),
        DayPlan(
            id = "dom", dia = "DOMINGO", titulo = "NATACIÓN", subtitulo = "Recuperación activa",
            duracion = "50 min", intensidad = "SUAVE", deporte = "natación", esDeporte = true,
            planNormal = listOf(
                PlanItem("elevacion-piernas-tumbada", "2x12", "45s", "Abdomen suave post-natación", "Suelo acolchado")
            ),
            planAlternativoId = "DOM_ALT"
        )
    )

    val altRoutines: Map<String, AltRoutine> = mapOf(
        "LUN_ALT" to AltRoutine(
            titulo = "LUNES ALT • Glúteo + Pierna + Abdomen",
            subtitulo = "No patinaje hoy → 35min bajo impacto",
            ejercicios = listOf(
                PlanItem("bici", "6 min", "-", "Activación suave", "Bajo impacto"),
                PlanItem("hip-thrust-barra", "4x15", "60s", "Cadera sube recta, aprieta arriba", "Sin carga en rodilla"),
                PlanItem("sentadilla-sumo-manc", "3x15", "45s", "Rango controlado", "Rango corto si molesta"),
                PlanItem("puente-gluteo", "3x15", "45s", "Bodyweight o con mancuerna", "Muy bajo impacto"),
                PlanItem("crunch-bicicleta", "3x15 c/lado", "40s", "Ritmo controlado", "Seguro"),
                PlanItem("plancha", "2x30s", "30s", "Cierre de core", "Seguro")
            )
        ),
        "MIE_ALT" to AltRoutine(
            titulo = "MIÉRCOLES ALT • Pierna + Glúteo + Brazos",
            subtitulo = "No patinaje hoy → 35min bajo impacto",
            ejercicios = listOf(
                PlanItem("bici", "6 min", "-", "Activación suave", "Bajo impacto"),
                PlanItem("zancada-estatica-manc", "3x12 c/pierna", "45s", "Paso corto, sin rebote", "Sin rebote en rodilla"),
                PlanItem("elevacion-cadera-un-pie", "3x10 c/pierna", "45s", "Unilateral controlado", "Rodilla semiflex fija"),
                PlanItem("curl-biceps-ligero-manc", "3x15", "40s", "Peso ligero, tonificación", "Seguro"),
                PlanItem("extension-triceps-ligero", "3x15", "40s", "Mango kettlebell ligero sobre cabeza", "Sentado"),
                PlanItem("rueda", "2x8", "45s", "Rango corto", "Seguro")
            )
        ),
        "DOM_ALT" to AltRoutine(
            titulo = "DOMINGO ALT • Full body suave",
            subtitulo = "No natación hoy → recuperación activa en casa",
            ejercicios = listOf(
                PlanItem("bici", "10 min Z1", "-", "Muy suave", "Bajo impacto"),
                PlanItem("puente-gluteo", "3x15", "45s", "Bodyweight", "Muy bajo impacto"),
                PlanItem("elevacion-piernas-tumbada", "3x12", "45s", "Lumbar pegada al suelo", "Suelo acolchado"),
                PlanItem("plancha", "2x30s", "30s", "Core suave", "Seguro")
            )
        )
    )

    val exerciseLibrary: List<Exercise> = listOf(
        Exercise("hip-thrust-barra", "Hip Thrust con Barra", "El mejor ejercicio de glúteo", "Glúteo mayor + isquiotibiales",
            listOf("Espalda apoyada en banco/silla firme, barra sobre cadera con toalla", "Empuja cadera arriba hasta línea recta hombro-cadera-rodilla", "Aprieta glúteo 1s arriba, baja controlado"),
            "El movimiento es de cadera (bisagra), casi nula carga en la rodilla. Ideal para tu objetivo de glúteo.",
            "Prácticamente sin carga en rodilla.", "Glúteo", "4x15 • 60s", "GLUTEO", false, imagenId = "Barbell_Hip_Thrust"),
        Exercise("puente-gluteo", "Puente de Glúteo", "Activación + volumen glúteo", "Glúteo + isquiotibiales",
            listOf("Acostada, rodillas flexionadas, pies apoyados", "Sube cadera apretando glúteo, sin arquear lumbar en exceso", "Opcional: mancuerna sobre la cadera para más carga"),
            "Ideal para empezar o como cierre; sin impacto en rodilla en absoluto.",
            "Muy bajo impacto, ideal.", "Glúteo activación", "3x15 • 45s", "GLUTEO", false, imagenId = "Butt_Lift_Bridge"),
        Exercise("patada-gluteo-cuadrupedia", "Patada de Glúteo en Cuadrupedia", "Aislamiento de glúteo", "Glúteo mayor",
            listOf("Apoyo de manos y rodillas, espalda neutra", "Extiende una pierna atrás y arriba sin arquear lumbar", "Aprieta glúteo arriba, baja sin tocar el suelo"),
            "Rodilla de apoyo puede ir sobre colchoneta para más comodidad.",
            "Sin carga axial en rodilla.", "Glúteo aislado", "3x15 c/pierna • 45s", "GLUTEO", false),
        Exercise("sentadilla-sumo-manc", "Sentadilla Sumo con Mancuerna", "Glúteo + pierna interna", "Glúteo + cuádriceps + aductor",
            listOf("Pies más anchos que hombros, puntas afuera", "Mancuerna colgando con ambas manos", "Baja solo hasta donde la rodilla esté cómoda, empuja con talones"),
            "El sumo reparte más carga en glúteo/cadera y menos en rodilla que una sentadilla normal.",
            "Rango corto y controlado si hay molestia.", "Glúteo + pierna", "3x15 • 45s", "PIERNA", true, imagenId = "Plie_Dumbbell_Squat"),
        Exercise("zancada-estatica-manc", "Zancada Estática con Mancuernas", "Pierna + glúteo unilateral", "Cuádriceps + glúteo",
            listOf("Un pie adelante, uno atrás, ambos fijos (sin caminar)", "Baja recto, rodilla de atrás casi toca el suelo", "Sube sin bloquear la rodilla delantera"),
            "Estática (sin caminar) reduce el impacto comparado con la zancada caminando.",
            "Sin rebote, rango corto si molesta.", "Pierna + glúteo", "3x12 c/pierna • 45s", "PIERNA", true, imagenId = "Dumbbell_Lunges"),
        Exercise("elevacion-cadera-un-pie", "Elevación de Cadera a Un Pie", "Glúteo unilateral", "Glúteo + core",
            listOf("Acostada, una rodilla flexionada apoyada, la otra extendida al frente", "Sube cadera con la pierna de apoyo, sin torcer lumbar", "Baja controlado"),
            "Unilateral: ayuda a corregir desequilibrios entre ambos glúteos.",
            "Rodilla de apoyo semiflex, sin carga.", "Glúteo unilateral", "3x10 c/pierna • 45s", "GLUTEO", false, imagenId = "Single_Leg_Glute_Bridge"),
        Exercise("elevacion-piernas-tumbada", "Elevación de Piernas Tumbada", "Abdomen bajo", "Recto abdominal bajo",
            listOf("Acostada, manos bajo la cadera o a los lados", "Sube ambas piernas juntas sin doblar rodillas de más", "Baja sin dejar que la lumbar se despegue del suelo"),
            "Si la lumbar se arquea, dobla un poco las rodillas o reduce el rango.",
            "Suelo acolchado.", "Abdomen bajo", "3x12 • 45s", "ABDOMEN", false, imagenId = "Flat_Bench_Lying_Leg_Raise"),
        Exercise("crunch-bicicleta", "Crunch Bicicleta", "Abdomen oblicuo", "Recto abdominal + oblicuos",
            listOf("Acostada, manos detrás de la cabeza sin jalar el cuello", "Lleva codo a rodilla contraria alternando", "Ritmo controlado, no rápido"),
            "La velocidad no es el objetivo; controla cada repetición para trabajar bien el oblicuo.",
            "Seguro, sin carga en rodilla.", "Cintura", "3x15 c/lado • 40s", "ABDOMEN", false, imagenId = "Air_Bike"),
        Exercise("curl-biceps-ligero-manc", "Curl de Bíceps Ligero", "Tonificación de brazo", "Bíceps",
            listOf("Mancuernas ligeras, codos pegados al cuerpo", "Sube controlado sin balancear el cuerpo", "Baja lento 2s"),
            "Peso ligero y más repeticiones: el objetivo es tonificar, no ganar volumen.",
            "Sin impacto en rodilla.", "Brazo tonificado", "3x15 • 40s", "BRAZOS", false, imagenId = "Dumbbell_Bicep_Curl"),
        Exercise("extension-triceps-ligero", "Extensión de Tríceps Ligera", "Tonificación de brazo", "Tríceps",
            listOf("Sentada, mango kettlebell o mancuerna ligera sobre la cabeza", "Baja el peso detrás de la cabeza doblando el codo", "Extiende sin mover el hombro"),
            "Peso ligero, rango completo, controlado — tonifica sin aumentar volumen.",
            "Sentada, sin carga en rodilla.", "Brazo tonificado", "3x15 • 40s", "BRAZOS", true, imagenId = "Seated_Triceps_Press"),
        Exercise("plancha", "Plancha", "Core estable", "Core completo",
            listOf("Codos bajo hombros, cuerpo en línea recta", "Aprieta glúteo y abdomen", "Respiración normal, sin dejar caer la cadera"),
            "Si molesta la zona lumbar, reduce el tiempo antes que perder la forma.",
            "Seguro, sin apoyo de rodilla.", "Estabilidad", "2-3x30s", "ABDOMEN", false, imagenId = "Plank"),
        Exercise("plancha-toques", "Plancha con Toque de Hombros", "Core + estabilidad", "Core + hombros",
            listOf("Plancha alta, pies separados ancho cadera", "Toca hombro contrario con la mano, alterna", "Cadera lo más quieta posible"),
            "Si la cadera se mueve mucho, abre más los pies.",
            "Seguro, sin apoyo de rodilla.", "Estabilidad + abdomen", "2x10 c/lado • 45s", "ABDOMEN", false),
        Exercise("rueda", "Rueda Abdominal", "Core anti-extensión", "Recto + transverso",
            listOf("Rodillas sobre colchoneta gruesa", "Rueda al frente sin arquear lumbar, rango corto", "Vuelve con el abdomen, no con los brazos"),
            "Empieza con rango muy corto; aumenta solo si no hay molestia.",
            "Colchoneta gruesa bajo la rodilla.", "Abdomen", "2x8 • 45s", "ABDOMEN", false, imagenId = "Barbell_Ab_Rollout"),
        Exercise("bici", "Bici Estática Suave", "Cardio sin impacto", "Cuádriceps + glúteo + cardio",
            listOf("Sillín a la altura de la cadera, rodilla con leve flexión abajo", "Resistencia baja-media, ritmo constante", "Evita pedalear de pie o con resistencia alta"),
            "Es el cardio más seguro para tus rodillas: sin impacto y totalmente controlable.",
            "El más seguro para rodilla.", "Quema suave", "5-15min", "PIERNA", false, imagenId = "Bicycling_Stationary")
    )

    /** Rotación semanal (4 semanas) del ejercicio principal de glúteo en los días de casa. */
    val gluteoRotation: Map<Int, String> = mapOf(
        1 to "hip-thrust-barra",
        2 to "puente-gluteo",
        3 to "patada-gluteo-cuadrupedia",
        4 to "hip-thrust-barra"
    )

    /** Rotación semanal (4 semanas) del ejercicio principal de abdomen en los días de casa. */
    val abdomenRotation: Map<Int, String> = mapOf(
        1 to "rueda",
        2 to "plancha-toques",
        3 to "elevacion-piernas-tumbada",
        4 to "crunch-bicicleta"
    )

    /** Rotación semanal (4 semanas) de la combinación de brazos (tonificación) del sábado. */
    val brazoRotation: Map<Int, BicepsTricepsCombo> = mapOf(
        1 to BicepsTricepsCombo(listOf("extension-triceps-ligero"), listOf("curl-biceps-ligero-manc"), "Tríceps ligero + Curl ligero"),
        2 to BicepsTricepsCombo(listOf("extension-triceps-ligero"), listOf("curl-biceps-ligero-manc"), "Tríceps ligero + Curl ligero (más repeticiones)"),
        3 to BicepsTricepsCombo(listOf("extension-triceps-ligero"), listOf("curl-biceps-ligero-manc"), "Tríceps ligero + Curl ligero"),
        4 to BicepsTricepsCombo(listOf("extension-triceps-ligero"), listOf("curl-biceps-ligero-manc"), "Tríceps ligero + Curl ligero (más repeticiones)")
    )

    val categorias = listOf("TODOS", "GLUTEO", "PIERNA", "ABDOMEN", "BRAZOS")

    fun exerciseById(id: String): Exercise? = exerciseLibrary.find { it.id == id }
}
