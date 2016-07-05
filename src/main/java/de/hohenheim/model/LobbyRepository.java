package de.hohenheim.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LobbyRepository extends JpaRepository<Lobby, Integer> {

    Lobby findByLobbyID(Integer lobbyID);

}