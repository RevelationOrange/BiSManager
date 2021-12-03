class GearSet {
    private var slots = mutableMapOf<String, String>(
        "mainHand" to "",
        "offHand" to "",
        "head" to "",
        "body" to "",
        "hands" to "",
        "legs" to "",
        "feet" to "",
        "ears" to "",
        "neck" to "",
        "wrists" to "",
        "ring0" to "",
        "ring1" to "",
    )
    private var need = mutableMapOf<String, Boolean>(
        "mainHand" to false,
        "offHand" to false,
        "head" to false,
        "body" to false,
        "hands" to false,
        "legs" to false,
        "feet" to false,
        "ears" to false,
        "neck" to false,
        "wrists" to false,
        "ring0" to false,
        "ring1" to false,
    )

    fun setItem(slot: String, item: String) { this.slots.put(slot, item) }

    fun getItem(slot: String): String { return this.slots.get(slot).toString() }

    fun setNeed(slot: String, got: Boolean) { this.need.put(slot, got) }

    fun getNeed(slot: String): Boolean? { return this.need.get(slot) }
}

class BiSTracker(name: String) {
    protected var main = GearSet()
    protected var alt0 = GearSet()
    protected var alt1 = GearSet()
    protected var mainName = ""
    protected var alt0Name = ""
    protected var alt1Name = ""
    protected var playerName = name

    fun setSlotItem(job: String, slot: String, item: String) {
        val gs: GearSet = when (job) {
            "main" -> { this.main }
            "alt0" -> { this.alt0 }
            else -> { this.alt1 }
        }
        gs.setItem(slot, item)
    }

    fun getSlotItem(job: String, slot: String): String {
        return when (job) {
            "main" -> { this.main.getItem(slot) }
            "alt0" -> { this.alt0.getItem(slot) }
            else -> { this.alt1.getItem(slot) }
        }
    }

    fun setSlotNeed(job: String, slot: String, got: Boolean) {
        val gs: GearSet = when (job) {
            "main" -> { this.main }
            "alt0" -> { this.alt0 }
            else -> { this.alt1 }
        }
        gs.setNeed(slot, got)
    }

    fun getSlotNeed(job: String, slot: String): Boolean? {
        return when (job) {
            "main" -> { this.main.getNeed(slot) }
            "alt0" -> { this.alt0.getNeed(slot) }
            else -> { this.alt1.getNeed(slot) }
        }
    }

    fun setJobName(job: String, name: String) {
        when (job) {
            "main" -> { this.mainName = name }
            "alt0" -> { this.alt0Name = name }
            else -> { this.alt1Name = name }
        }
    }

    fun getJobName(job: String): String {
        return when (job) {
            "main" -> { this.mainName }
            "alt0" -> { this.alt0Name }
            else -> { this.alt1Name }
        }
    }

    fun updatePlayerName(pname: String) { this.playerName = pname }

    fun retPlayerName(): String { return this.playerName }
}

class BiSManager {
    var playerList: MutableMap<String, BiSTracker> = mutableMapOf<String, BiSTracker>()
    var jobTranslator = mapOf("main" to "main", "primary" to "main", "first" to "main", "secondary" to "alt0",
        "alt1" to "alt0", "second" to "alt0", "tertiary" to "alt1", "third" to "alt1", "alt2" to "alt1")
    var slotTranslator = mutableMapOf<String, String>()

    init {
        for (s in arrayOf("mainHand", "weapon")) { this.slotTranslator[s.lowercase()] = "mainHand" }
        for (s in arrayOf("offHand", "shield")) { this.slotTranslator[s.lowercase()] = "offHand" }
        for (s in arrayOf("head", "hat")) { this.slotTranslator[s.lowercase()] = "head" }
        for (s in arrayOf("body", "chest")) { this.slotTranslator[s.lowercase()] = "body" }
        for (s in arrayOf("hands", "hand", "arms", "arm")) { this.slotTranslator[s.lowercase()] = "hands" }
        for (s in arrayOf("legs", "leg")) { this.slotTranslator[s.lowercase()] = "legs" }
        for (s in arrayOf("feet", "foot")) { this.slotTranslator[s.lowercase()] = "feet" }
        for (s in arrayOf("ears", "ear", "earrings", "earring")) { this.slotTranslator[s.lowercase()] = "ears" }
        for (s in arrayOf("neck", "necklace")) { this.slotTranslator[s.lowercase()] = "neck" }
        for (s in arrayOf("wrists", "wrist", "bracelets", "bracelet")) { this.slotTranslator[s.lowercase()] = "wrists" }
        for (s in arrayOf("ring0", "rightring", "rring")) { this.slotTranslator[s.lowercase()] = "ring0" }
        for (s in arrayOf("ring1", "leftring", "lring")) { this.slotTranslator[s.lowercase()] = "ring1" }
    }

    fun addPlayer(name: String) {
        val p = BiSTracker(name)
        this.playerList[name] = p
    }

    fun getPList(): MutableMap<String, BiSTracker> { return this.playerList }

    fun setPlayerName(player: String, pname: String) { this.playerList[player]?.updatePlayerName(pname) }

    fun getPlayerName(player: String) { this.playerList[player]?.retPlayerName() }

    fun setPlayerJobName(playerName: String, job: String, jobName: String) {
        if (job in jobTranslator) {
            this.playerList[playerName]?.setJobName(jobTranslator[job].toString(), jobName)
        }
        else { throw InvalidJobException() }
    }

    fun getPlayerJobName(playerName: String, job: String): String? {
        if (job in jobTranslator) {
            return this.playerList[playerName]?.getJobName(jobTranslator[job].toString())
        }
        else { throw  InvalidJobException() }
    }

    fun setPlayerJobSlotItem(player: String, job: String, slot: String, item: String) {
        if (job in jobTranslator) {
            if (slot in slotTranslator) {
                this.playerList[player]?.setSlotItem(jobTranslator[job].toString(), slotTranslator[slot].toString(), item)
            }
            else { throw InvalidSlotException() }
        }
        else { throw InvalidJobException() }
    }

    fun getPlayerJobSlotItem(player: String, job: String, slot: String): String? {
        if (job in jobTranslator) {
            if (slot in slotTranslator) {
                return this.playerList[player]?.getSlotItem(jobTranslator[job].toString(), slotTranslator[slot].toString())
            }
            else { throw InvalidSlotException() }
        }
        else { throw InvalidJobException() }
    }

    fun setPlayerJobSlotNeed(player: String, job: String, slot: String, got: Boolean) {
        if (job in jobTranslator) {
            if (slot in slotTranslator) {
                this.playerList[player]?.setSlotNeed(jobTranslator[job].toString(), slotTranslator[slot].toString(), got)
            }
            else { throw InvalidSlotException() }
        }
        else { throw InvalidJobException() }
    }

    fun getPlayerJobSlotNeed(player: String, job: String, slot: String): Boolean {
        if (job in jobTranslator) {
            if (slot in slotTranslator) {
                return this.playerList[player]?.getSlotNeed(jobTranslator[job].toString(), slotTranslator[slot].toString()) == true
            }
            else { throw InvalidSlotException() }
        }
        else { throw InvalidJobException() }
    }

    fun getAllSlots(slot: String): ArrayList<ArrayList<String>> {
        if (slot in slotTranslator) {
            val slotList: ArrayList<ArrayList<String>> = arrayListOf()
            for (player in this.playerList.keys) {
                // go through each player's main, alt0, and alt1 and grab the slot info
                // return as (playername, main/alt0/alt1, item, got)
                for (i in arrayOf("main", "alt0", "alt1")) {
                    val got = if (this.getPlayerJobSlotNeed(player, i, slot)) { "x" } else { "." }
                    slotList.add(arrayListOf(player, i,
                        this.getPlayerJobSlotItem(player, i, slot).toString(), got))
                }
            }
            return slotList
        }
        else { throw InvalidSlotException() }
    }
}

fun main(args: Array<String>) {
    val x = BiSManager()
    x.addPlayer("a")
//    for (pl in x.playerList) { println(pl) }
    x.getAllSlots("head")
}
