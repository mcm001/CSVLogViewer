package org.ghrobotics.falcondashboard

import org.ghrobotics.lib.mathematics.units.inches

/**
 * Stores general properties for robots and vision targets.
 */
object Properties {
    // Robot Sizes
    val kRobotLength = 32.5.inches
    val kRobotWidth = 27.inches

    // Target Sizes
    val kTargetWidth = 14.5.inches
    val kTargetThickness = kTargetWidth / 2
}