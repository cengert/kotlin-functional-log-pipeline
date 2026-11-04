data class Logeintrag(val level: String, val nachricht: String)

fun parseZeile(zeile: String): Result<Logeintrag> {
    val worte = zeile.split(":", limit = 2)
    val level = worte.getOrNull(0)?.trim()?.uppercase() ?: ""
    val nachricht = worte.getOrNull(1)?.trim() ?: ""

    if (worte.size !=2 || level.isEmpty() || nachricht.isEmpty()) {
        return Result.failure(IllegalArgumentException("Logformat ungültig: '$zeile'"))
    }
    return Result.success(Logeintrag(level, nachricht))
}

fun verarbeiteLogs(logs: List<String>, zielLevel: String): List<String> {
    return logs
        .asSequence()
        .map { zeile -> parseZeile(zeile) }
        .mapNotNull { result -> result.getOrNull() }
        .filter { eintrag -> eintrag.level.equals(zielLevel, ignoreCase = true) }
        .map { eintrag -> eintrag.nachricht }
        .toList()
}

fun aggregiereLogs(logs: List<String>): Map<String, Int> {
    return logs
        .map { zeile -> parseZeile(zeile) }
        .flatMap { result -> result.getOrNull()?.let { listOf(it) } ?: emptyList()  }
        .groupBy { eintrag -> eintrag.level }
        .mapValues { (_, eintraege) -> eintraege.size }
        .toList()
        .sortedByDescending { (_, anzahl) -> anzahl }
        .toMap()
}

fun main() {
    val beispielLogs= listOf(
        "INFO: User login successful",
        "ERROR: Invalid password entered",
        "MUELL_OHNE_DOPPELPUNKT",
        "WARNING: Disk space almost full (92%)",
        "ERROR:",
        "ERROR: Connection timed out after 5000ms",
        ": Ich hab kein Level",
        "INFO: Content downloaded successfully",
        "DEBUG: Internal state synchronized",
        "WARNING: High memory usage deteced",
        "ERROR: Database connection lost"
    )

    val errorNachrichten = verarbeiteLogs(beispielLogs, "ERROR")
    println("Stufe1 & 3: Gefiltere Fehler-Nachrichten (ohne ungueltige):")
    println(errorNachrichten)

    val statistik = aggregiereLogs(beispielLogs)
    println("Stufe2: Log-Statistik:")
    statistik.forEach { (level, anzahl) ->
        println("Level '$level': $anzahl")
    }
}