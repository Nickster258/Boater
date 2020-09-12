package entity

import com.uchuhimo.konf.ConfigSpec

object BoaterSpec : ConfigSpec("") {
    val lineMaterial by optional("RED_CONCRETE")
    object BoaterDatabase : ConfigSpec() {
        val username by optional("root")
        val password by optional("password")
        val database by optional("boater_test")
        val host by optional("localhost")
        val port by optional(3306)
    }
}