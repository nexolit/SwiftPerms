package me.nexolit.ezperms.groups;

import java.util.ArrayList;
import java.util.UUID;

public class Group {
    public String name;
    public ArrayList<String> permissions = new ArrayList<String>();
    public ArrayList<UUID> players = new ArrayList<UUID>();

    public Group(String name) {
        this.name = name;
    }
}
