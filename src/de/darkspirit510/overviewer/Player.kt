package de.darkspirit510.overviewer

data class Player(
        val uuid: String,
        val name: String,
        val lastSeen: Long,
        val x: Int,
        val y: Int,
        val z: Int
)