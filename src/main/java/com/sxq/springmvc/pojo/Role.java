package com.sxq.springmvc.pojo;

import java.io.Serializable;

public class Role implements Serializable{
    private static final long serialVersionUID = -1194462093889377366L;

    private Long id ;
    private String roleName;
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        String tmp = "[%s] id=%d,roleName=%s,note=%s";
        return String.format(tmp, Role.class.getSimpleName(), id, roleName, note);
    }
}