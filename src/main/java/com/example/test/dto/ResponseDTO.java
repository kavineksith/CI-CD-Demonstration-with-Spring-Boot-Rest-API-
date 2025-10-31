package com.example.test.dto;

import java.util.UUID;

public class ResponseDTO extends UserDTO{
    private UUID id;

    public ResponseDTO() {
        super();
    }

    public ResponseDTO( UUID id, String name, String email, String password) {
        super(name, email, password);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
