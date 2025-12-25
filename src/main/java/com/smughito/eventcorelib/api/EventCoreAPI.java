package com.smughito.eventcorelib.api;

/**
 * Main API interface for EventCoreLib.
 * Access this through the EventCoreLib plugin instance.
 */
public interface EventCoreAPI {

    /**
     * Gets the team manager.
     *
     * @return the team manager instance
     */
    TeamManager getTeamManager();
}
