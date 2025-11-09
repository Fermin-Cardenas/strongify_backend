package com.app.demo.DTO.Request;

public class UpdateRolRequest {
    private Long rol_id;

    public UpdateRolRequest(Long rol_id) {
        this.rol_id = rol_id;
    }

    public Long getRol_id() {
        return rol_id;
    }

    public void setRol_id(Long rol_id) {
        this.rol_id = rol_id;
    }

}
